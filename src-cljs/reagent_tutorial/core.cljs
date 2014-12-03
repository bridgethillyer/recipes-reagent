(ns reagent-tutorial.core
  (:require [clojure.string :as string]
            [reagent.core :as r]))

(enable-console-print!)

;; The "database" of your client side UI.
(def app-state
  (r/atom
   {:recipes
    [{:title "Mochi" :url "http://www.japanesecooking101.com/sweet-mochi-recipe/"}
     {:title "Black Beans and Rice" :url "http://allrecipes.com/recipe/cuban-black-beans-i/"}
     {:title "Chicken a la King" :url "http://www.bettycrocker.com/recipes/chicken-a-la-king/2fd637ba-2c30-4db5-95d0-e57b4547251e"}
     {:title "Mexican Chocolate Tofu Pudding" :url "http://www.nytimes.com/2009/05/20/dining/201mrex.html"}]}))



(defn update-recipes! [f & args]
  (apply swap! app-state update-in [:recipes] f args))

(defn add-recipe! [c]
  (update-recipes! conj c))

(defn remove-recipe! [c]
  (update-recipes! (fn [cs]
                      (vec (remove #(= % c) cs)))
                    c))

(defn display-recipe[{:keys [title url] :as recipe}]
  [:a {:href url} title])

;; TODO check that url starts with http
(defn parse-recipe[recipe-str]
  (let [[title url :as parts] (string/split recipe-str #"http")]
      {:title title :url (str "http" url)}))

;; UI components
(defn recipe [r]
  [:li
   [:span (display-recipe r)]
   [:button {:on-click #(remove-recipe! r)}
    "Delete"]])

(defn new-recipe[]
  (let [val (r/atom "")]
    (fn []
      [:div
       [:input {:type "text"
                :placeholder "Title URL"
                :value @val
                :on-change #(reset! val (-> % .-target .-value))}]
       [:button {:on-click #(when-let [r (parse-recipe @val)]
                              (add-recipe! r)
                              (reset! val ""))}
        "Add"]])))

(defn recipe-list []
  [:div
   [:h1 "Recipe list"]
   [:ul
    (for [r (:recipes @app-state)]
      [recipe r])]
   [new-recipe]])

;; Render the root component
(defn start []
  (r/render-component
   [recipe-list]
   (.getElementById js/document "root")))
