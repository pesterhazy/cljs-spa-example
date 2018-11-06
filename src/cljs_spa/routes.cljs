(ns cljs-spa.routes
  (:require [cljs-spa.layout :as layout]
            [cljs-spa.page.home :as home]
            [cljs-spa.page.users :as users]
            [cljs-spa.page.profile :as profile]
            [cljs-spa.page.clock :as clock]))

(def the-routes
  [{:name "home", :path "/", :render-fn (fn [params] [home/page-ui params])}
   {:name "users",
    :path "/users",
    :render-fn (fn [params] [layout/page-state-ui [users/page-ui params]]),
    :on-activate users/activate}
   {:name "user",
    :path "/users/:id",
    :render-fn (fn [params] [layout/page-state-ui [profile/page-ui params]]),
    :on-activate profile/activate}
   {:name "clock",
    :path "/clock",
    :render-fn (fn [params] [clock/page-ui params]),
    :on-activate clock/activate,
    :on-deactivate clock/deactivate}])

(def name->render-fn
  (->> the-routes
       (map (juxt :name :render-fn))
       (into {})))

(defn switch-ui
  [{route-name :name, params :params}]
  (let [render-fn (name->render-fn route-name (fn [] [layout/not-found-ui]))]
    [layout/layout-ui (render-fn params)]))
