(ns ditto.view.mode
  "Provides a view to be used by the mode page."
  (:use [hiccup.core]
        [hiccup.form])
  (:require [ditto.view.common :as common]))

(defn- make-submit-btn [app app-mode btn-mode btn-class]
  "Makes submit button attributes and rainbows if it's the current mode."
  (let [selected (= btn-mode app-mode)
        attributes {:name "mode" :type "submit" :value btn-mode}
        classes (str "btn " btn-class)]
    (if selected
      (->
        (assoc attributes :class (str classes " rainbowbg"))
        (assoc :disabled "true"))
      (assoc attributes :class classes))))

(defn mode-page [model]
  "Constructs the mode page, given a model containing app and mode, and error if
   there's an error."
  (common/layout (str "Mode: " (model :app)) (html
    [:div#main.container
      [:div#top
        [:p [:h2 (model :app)]]
        (form-to [:post "switch"]
          [:div.btn-group
            [:input
              (make-submit-btn
                (model :app) (model :mode) "Playback" "btn-success")]
            [:input
              (make-submit-btn
                (model :app) (model :mode) "Timed Playback" "btn-warning")]
            [:input
              (make-submit-btn
                (model :app) (model :mode) "Recording" "btn-danger")]])
        [:div#message
          [:p.text-info (model :message)]]
        (when (model :error)
          [:div#error
            [:p.text-error "Error: " (model :error)]])]])))
