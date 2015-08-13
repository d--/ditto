(ns ditto.view.home
  "Provides a view to be used by the home page."
  (:use [hiccup.core]
        [hiccup.form])
  (:require [ditto.view.common :as common]))

(defn submit-btn [value]
  "Construct a submit button with the given value."
  {:type "submit" :name "action" :value value})

(defn home-page []
  "Constructs the home page."
  (common/layout "Home"
    (html
      [:div#main.container
        (form-to {:class "form-horizontal"} [:post "submit"]
          [:div.control-group
            (label
              {:class "control-label"} "app-input" "Application Name")
            [:div.controls
              [:input#app-input
                {:type "text" :name "app" :placeholder "application"}]
              [:input.btn (submit-btn "Switch Mode")]
              [:input.btn (submit-btn "View Responses")]]]
          [:div.control-group
            (label
              {:class "control-label"} "location-input" "Location")
            [:div.controls
              [:input#location-input.input-xxlarge
                {:type "text" :name "location"
                  :placeholder "http://some.location"}]]]
          [:div.control-group
            [:div.controls
              [:div.btn-group
                [:input.btn.btn-success (submit-btn "Playback")]
                [:input.btn.btn-success (submit-btn "Timed Playback")]
                [:input.btn.btn-warning (submit-btn "Record")]
                [:input.btn.btn-info (submit-btn "View Response Data")]]]])
        [:p.text-info
          [:small
            (str
              "Note: Patterns of special characters will be converted to a "
              "single underscore character in the application name and "
              "database filename.")]]
        [:p.text-warning
          [:small
            (str
              "Note: If you attempt to play back a response that does not "
              "exist, you will receive a ")
            [:strong "404."]]]])))
