(ns cljs-spa.page.profile
  (:require [cljs-spa.util :as util]
            [cljs-spa.state :refer [!state loaded!]]
            [goog.object :as gobj]))

(defn activate [{{:keys [id]} :params}]
  (-> (util/safe-fetch (str "https://reqres.in/api/users/" id "?page=1"))
      (.then (fn [r] (.json r)))
      (.then (fn [js-data]
               (swap! !state assoc
                 :user-data
                 (js->clj js-data :keywordize-keys true))))
      (.then loaded!)))

(defn page-ui [{:keys [id]}]
  [:div [:h3 "User No. " id]
   (when-let [user-data (:user-data @!state)]
     [:div
      [:img
       {:src (-> user-data
                 :data
                 :avatar)}]
      (-> user-data
          :data
          :first_name)])])
