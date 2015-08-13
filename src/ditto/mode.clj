(ns ditto.mode
  "Provides functions to switch and retrieve global application modes."
  (:require [ditto.db :as db]))

(def mode-map (atom {}))

(defn get-mode [app]
  "Get the current mode of the given app."
  (or (@mode-map app) "Playback"))

(defn change-mode [app mode]
  "Change the mode of the given app to the given mode."
  (swap! mode-map assoc app mode))

(defn get-all-app-modes []
  "Get all the modes associated with existing databases."
  (let [apps (db/get-names-of-apps-with-dbs)]
    (map
      #(assoc {:app %} :mode (get-mode %))
      apps)))
