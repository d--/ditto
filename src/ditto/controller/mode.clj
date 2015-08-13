(ns ditto.controller.mode
  "Provides routes that serve the mode page and deal with mode switching."
  (:use [compojure.core])
  (:require [ditto.mode :as mode]
            [ditto.view.mode :as mode-view]))

(defn- change-mode-or-error [app mode]
  "Change the app mode or return an error to the model (for the user to see) if
   a bad one was supplied."
  (if (contains? #{"Playback" "Timed Playback" "Recording"} mode)
    (do
      (mode/change-mode app mode)
      (mode-view/mode-page
        { :app app
          :mode (mode/get-mode app)}))
    (mode-view/mode-page
      { :app app
        :mode (mode/get-mode app)
        :error "Invalid mode."})))

(defn mode-page-routes []
  "These routes handle serving the mode page and changing app modes."
  (routes
    (GET "/ditto-ui/:app/switch" [app]
      ; Return the mode page with the app set in the model.
      (mode-view/mode-page
        { :app app
          :mode (mode/get-mode app)}))

    (POST "/ditto-ui/:app/switch" {{app :app mode :mode} :params}
      ; Return the mode page with the newly switched app mode, or an error.
      (change-mode-or-error app mode))))
