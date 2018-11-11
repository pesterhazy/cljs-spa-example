(ns cljs-spa.router
  (:require [goog.object :as gobj]
            [cljs-spa.state :refer [!state]]
            [reagent.core :as r]
            [router5]
            ["router5/plugins/browser" :as router5-browser-plugin]))

(defn handle-load-error [e]
  (when (-> e
            ex-data
            :load-error)
    (swap! !state assoc :page-state :failed))
  (throw e))

(defn handle-rejection [e]
  (js/console.error e)
  (-> (js/Noty. #js {:text (.-message e), :type "error", :timeout 1500})
      .show))

(defn middleware* [name->route to-state from-state]
  (let [on-activate (some-> to-state
                            (gobj/get "name")
                            name->route
                            :on-activate)
        on-deactivate (some-> from-state
                              (gobj/get "name")
                              name->route
                              :on-deactivate)
        p (if on-deactivate
            (-> (js/Promise. (fn [resolve]
                               (resolve (on-deactivate from-state))))
                (.catch handle-rejection))
            (js/Promise.resolve))]
    (-> p
        (.then (fn []
                 (if on-activate
                   (-> (js/Promise.
                         (fn [resolve]
                           (resolve (swap! !state assoc :page-state :loading))))
                       (.then (fn []
                                (on-activate
                                  (js->clj to-state :keywordize-keys true))))
                       (.catch handle-load-error)
                       (.catch handle-rejection))
                   (swap! !state assoc :page-state :loaded))
                 true)))))

(defn create-router [routes]
  (let [name->route (->> routes
                         (map (juxt :name identity))
                         (into {}))]
    (let [router (router5/createRouter (clj->js routes))]
      (.usePlugin ^js router
                  ((.-default router5-browser-plugin) #js {:useHash true}))
      (.useMiddleware ^js router
                      (fn [router]
                        (fn [to-state from-state]
                          (middleware* name->route to-state from-state)))))))

(defn stop-router [router] (.stop router))

(defn router-ui [initial-props]
  (let [!unsubscribe-fn (atom nil)
        unsubscribe (fn []
                      (when-let [fun @!unsubscribe-fn]
                        (fun)
                        (reset! !unsubscribe-fn nil)))
        !route (r/atom (.getState (:router initial-props)))
        on-change (fn [o] (reset! !route (gobj/get o "route")))
        subscribe (fn [router]
                    (unsubscribe)
                    (reset! !unsubscribe-fn (.subscribe router on-change)))]
    (let [router (:router initial-props)]
      (reset! !route (.getState router))
      (subscribe router))
    (r/create-class
      {:component-will-receive-props (fn [_ new-argv]
                                       (subscribe (-> new-argv
                                                      second
                                                      :router))),
       :component-will-unmount unsubscribe,
       :reagent-render
         (fn [{:keys [render-fn]}]
           (assert (fn? render-fn) "Must be a function: render-fn")
           (let [route @!route]
             (render-fn {:name (gobj/get route "name"),
                         :params (js->clj (gobj/get route "params")
                                          :keywordize-keys
                                          true)})))})))
