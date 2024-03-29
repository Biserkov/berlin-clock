(ns berlin-clock.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def app-clock (atom (js/Date.)))

(defn bulb [state id]
  ^{:key id}
  [:div.bulb {:class (name state)}])

(defn bulbs-row [bulbs color]
  [:div.clock-row
   (doall
    (map #(bulb (color % color) %) (range bulbs)))])

(defn seconds-component [seconds]
  [:div.seconds
   [bulbs-row 1 (if (even? seconds)
                  :yellow
                  :off)]])

(defn bulbs-line-component [toggled size]
  [:div.bulb-line
   [bulbs-row toggled (if (= size 11)
                        #(if (#{2 5 8} %) :red :yellow)
                        :yellow)]
   [bulbs-row (- size toggled) :off]])

(defn set-clock! []
  ;(prn "Setting the time baby! " (.getSeconds (js/Date.)))
  (reset! app-clock (js/Date.)))

(def clock
  (with-meta (fn []
               (let [hours        (.getHours @app-clock)
                     minutes      (.getMinutes @app-clock)
                     seconds      (.getSeconds @app-clock)]
                 [:div.clock
                  [seconds-component seconds]
                  [bulbs-line-component (quot hours 5) 4]
                  [bulbs-line-component (mod hours 5) 4]
                  [bulbs-line-component (quot minutes 5) 11]
                  [bulbs-line-component (mod minutes 5) 4]]))
    {:component-did-mount #(prn :intervalID (js/setInterval set-clock! 1000))}))

(reagent/render-component [clock]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
