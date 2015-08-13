(ns ditto.controller.vcr
  "Provides routes that control the VCR."
  (:use [compojure.core])
  (:require [ditto.vcr :as vcr]))

(defn vcr-routes []
  "These routes control the VCR.  They do exactly as they say for a given
   application and id."
  (routes
    (GET "/ditto-ui/:app/playback/:id" [app id]
      (vcr/playback app id))
    (GET "/ditto-ui/:app/timedplayback/:id" [app id]
      (vcr/timed-playback app id))
    (GET "/ditto-ui/:app/recording/:id" [app id :as request]
      (vcr/rerecord-by-id app id (request :headers)))))
