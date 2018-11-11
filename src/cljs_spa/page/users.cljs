(ns cljs-spa.page.users
  (:require [cljs-spa.util :as util]
            [cljs-spa.state :refer [!state loaded!]]))

(defn activate []
  (-> (util/safe-fetch "https://reqres.in/api/users?page=1")
      (.then (fn [r] (.json r)))
      (.then (fn [js-data]
               (swap! !state assoc
                 :users-data
                 (js->clj js-data :keywordize-keys true))))
      (.then loaded!)))

(defn page-ui []
  [:div [:h3 "Users"]
   (when-let [users-data (:users-data @!state)]
     (->> users-data
          :data
          (map (fn [user] [:li
                           [:a {:href (str "#/users/" (:id user))}
                            (:first_name user)]]))
          (into [:ul])))])
