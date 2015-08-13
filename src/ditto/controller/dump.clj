(ns ditto.controller.dump
  "Provides a route to the dump page for a given application."
  (:use [compojure.core])
  (:require [ditto.db :as db]
            [ditto.util :as util]
            [ditto.view.dump :as dump-view]))

(defn dump-page-route []
  "This routes to the dump page for a given application, where the user can view
   all of the ditto responses in the database for that application."
  (GET "/ditto-ui/:app/dumpall" [app]
    (let [app (util/sanitize app)]
      (dump-view/dump-page
        { :app app
          :responses (db/get-all-responses-metadata app)}))))
