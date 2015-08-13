(ns ditto.runner
  "Provides a runner class for the jetty server."
  (:require [ditto.core :as core]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn -main [& args]
  (jetty/run-jetty #'core/app {:port 9090}))
