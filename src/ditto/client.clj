(ns ditto.client
  "Wraps clj-http client for the needs of ditto."
  (:require [clj-http.client :as client]))

(defn- fix-headers [headers]
  "Returns a header map with the ditto header and without host/content-length."
  (-> (assoc headers "ditto-request" "true")
      (dissoc "host" "content-length")))

(defn ditto-clj-http-get [url request-headers]
  "Returns a clj-http response map."
  (client/with-middleware [#'clj-http.client/wrap-method
                           #'clj-http.client/wrap-url
                           #'clj-http.client/wrap-request-timing
                           #'clj-http.client/wrap-lower-case-headers]
    (client/get url
      { :as :byte-array
        :decompress-body false
        :throw-exceptions false
        :headers (fix-headers request-headers)})))

; HACK: Kill clj-http's angry cookie handling.
(ns clj-http.cookies)
(defn wrap-cookies [& nope])
