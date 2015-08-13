(ns ditto.controller.view-response-data
  "Provides a route to view response data for a given response by ID."
  (:use [compojure.core])
  (:require [ditto.db :as db]
            [ditto.view.view-response-data :as view]))

(defn- build-response-data-model [app id]
  "Builds the model to send down to the view, containing a response and any
  error messages."
  (let [response (db/get-ditto-response-by-id app id)]
    { :response response
      :error (when (nil? response) "Response does not exist.")}))

(defn view-response-data-route []
  "This route is for the page where you can view the metadata for a single
  response, given an application and ID."
  (GET "/ditto-ui/:app/view/:id" [app id]
    (view/data-page (build-response-data-model app id))))
