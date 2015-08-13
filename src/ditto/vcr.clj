(ns ditto.vcr
  "Provides the core functionality of ditto:  The ability to record and play
  back responses.  These responses will be returned as ring responses maps."
  (:require [ditto.client :as client]
            [ditto.db :as db]
            [ditto.idcalc :as idcalc]
            [ditto.mapper :as mapper]
            [ditto.util :as util]
            [ditto.mode :as mode]))

(def vcr404page
  { :status 404
    :headers {"Content-Type" "text/html"}
    :body "Adjust VCR Tracking (Response Not Found) (404)"})

(defn record [app url request-headers]
  "Given an application name, URL, and request headers to forward, get and
   record a response in the database."
  (let [clj-http-response (client/ditto-clj-http-get url request-headers)]
    (db/convert-and-store-clj-http-response
      app url (System/currentTimeMillis) clj-http-response)
    (mapper/map-clj-http-response-to-ring-response clj-http-response)))

(defn rerecord-by-id [app id request-headers]
  "Given an application name, response ID, and request headers to forward,
  re-record the URL for the response, if one is found."
  (let [ditto-response (db/get-ditto-response-by-id app id)]
    (if (nil? ditto-response)
      vcr404page
      (record app (ditto-response :location) request-headers))))

(defn playback [app id]
  "Given an application name and response ID, play the response back if it
  exists."
  (let [ditto-response (db/get-ditto-response-by-id app id)
        reqtime (System/currentTimeMillis)]
    (db/update-last-request-time app id reqtime)
    (if (nil? ditto-response)
      (do
        vcr404page)
      (mapper/map-ditto-response-to-ring-response ditto-response))))

(defn timed-playback [app id]
  "Given an application name and response ID, play the response back and mimic
  the response time if it exists."
  (let [ditto-response (db/get-ditto-response-by-id app id)
        reqtime (System/currentTimeMillis)]
    (db/update-last-request-time app id reqtime)
    (if (nil? ditto-response)
      vcr404page
      (do
        (Thread/sleep (ditto-response :responsetimemillis))
        (mapper/map-ditto-response-to-ring-response ditto-response)))))

(defn playback-by-url [app url]
  "Playback the URL response."
  (playback app (idcalc/calc-id app url)))

(defn timed-playback-by-url [app url]
  "Playback the URL response and mimic the response time."
  (timed-playback app (idcalc/calc-id app url)))

(defn modal [app url request-headers]
  "Check the mode and perform the appropriate action, return the response."
  (case (mode/get-mode app)
    "Recording" (record app url request-headers)
    "Playback" (playback-by-url app url)
    "Timed Playback" (timed-playback-by-url app url)))
