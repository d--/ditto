(ns ditto.mapper
  "Provides functions to map ditto and clj-http responses to ring responses."
  (:require [clojure.java.io :as io]
            [ring.util.response :as ring]))

(defn- build-ring-response [body status content-type content-encoding]
  "Builds a ring response given a body, status code, content-type header and
  content-encoding header."
  (-> (ring/response body)
      (ring/status status)
      (ring/content-type content-type)
      (ring/header "Content-Encoding" content-encoding)))

(defn map-clj-http-response-to-ring-response [clj-http-response]
  "Converts a clj-http response map to a ring response map to hand back to the
  requesting client."
  (let [body (clj-http-response :body)]
    (build-ring-response
      (if body (io/input-stream body) "")
      (clj-http-response :status)
      ((clj-http-response :headers) "content-type")
      ((clj-http-response :headers) "content-encoding"))))

(defn map-ditto-response-to-ring-response [ditto-response]
  "Converts a ditto response map to a ring response map to hand back to the
  requesting client."
  (let [body (ditto-response :body)]
    (build-ring-response
      (if body (io/input-stream body) "")
      (ditto-response :status)
      (ditto-response :contenttype)
      (ditto-response :contentencoding))))
