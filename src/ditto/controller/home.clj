(ns ditto.controller.home
  "Provides routes dealing with the home page."
  (:use [compojure.core])
  (:require [ditto.idcalc :as idcalc]
            [ditto.util :as util]
            [ditto.vcr :as vcr]
            [ditto.view.home :as home-view]
            [compojure.route :as route]
            [ring.util.response :as ring]))

(defn home-page-routes []
  "These are the routes dealing with the home page view, and the actions taken
   there."
  (routes
    (GET "/ditto-ui/" []
      ; Returns the basic home page view.
      (home-view/home-page))

    (POST "/ditto-ui/submit"
      [:as
        {{action :action
          app :app
          url :location} :params
        headers :headers}]
      ; Redirect the user to the appropriate page based on the submit.
      (let [id (idcalc/calc-id (util/sanitize app) url)
            app (util/sanitize app)
            ui-base (str "/ditto-ui/")]
        (case action
          "Switch Mode"
            (ring/redirect (str ui-base app "/switch"))
          "View Responses"
            (ring/redirect (str ui-base app "/dumpall"))
          "Playback"
            (ring/redirect (str ui-base app "/playback/" id))
          "Timed Playback"
            (ring/redirect (str ui-base app "/timedplayback/" id))
          "Record"
            (do
              (vcr/record app url (dissoc headers "host"))
              (ring/redirect (str ui-base app "/view/" id)))
          "View Response Data"
            (ring/redirect (str ui-base app "/view/" id))
          (route/not-found "Invalid action."))))))
