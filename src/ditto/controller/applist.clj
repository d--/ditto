(ns ditto.controller.applist
  "Provides a route to the list page where the user can act on applications."
  (:use [compojure.core])
  (:require [ditto.view.applist :as applist-view]
            [ditto.mode :as mode]))

(defn applist-page-route []
  "This route is for a page that shows the current applications that have
   database files behind them, and have modes set."
   (GET "/ditto-ui/apps" []
     (applist-view/applist-page
      {:appmodes (mode/get-all-app-modes)})))
