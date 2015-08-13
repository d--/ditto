(ns ditto.controller.upload-download
  "Provides routes to handle upload and download of responses."
  (:use [compojure.core])
  (:require [ditto.db :as db]
            [ditto.mapper :as mapper]
            [ditto.util :as util]
            [ring.middleware.multipart-params :as mp]
            [ring.util.response :as ring]))

(defn upload-download-routes []
  "These routes handle upload and download requests for ditto responses from the
   view response data page."
  (routes
    (GET "/ditto-ui/:app/download/:id" [app id]
      ; Set a custom header to make sure the browser treats the response as an
      ; explicit download.
      (let [ditto-response (db/get-ditto-response-by-id app id)]
        (-> (mapper/map-ditto-response-to-ring-response ditto-response)
            (ring/header "Content-Disposition" "attachment"))))

    (mp/wrap-multipart-params
      (POST "/ditto-ui/upload"
        ; Use the Ring multipart middleware to handle a multipart request where
        ; the user uploads a new response body.
        ; This generates a temporary file which is read into a byte array.
        [:as {{{file :tempfile} "file_upload"
                app "app"
                id "id"} :multipart-params}]
          (let [file-bytes (util/to-byte-array file)
                reqtime (System/currentTimeMillis)]
            (db/update-response-body app id file-bytes reqtime)
            "Upload successful!")))))
