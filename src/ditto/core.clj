(ns ditto.core
  "Define the compojure routes and ring handler for ditto."
  (:use [compojure.core])
  (:require [ditto.controller.home :as home]
            [ditto.controller.mode :as mode]
            [ditto.controller.dump :as dump]
            [ditto.controller.applist :as applist]
            [ditto.controller.view-response-data :as view-response-data]
            [ditto.controller.vcr :as vcr]
            [ditto.controller.upload-download :as upload-download]
            [ditto.proxy :as ditto-proxy]
            [compojure.route :as route]
            [compojure.handler :as handler]))

(defroutes app-routes
  (home/home-page-routes)
  (mode/mode-page-routes)
  (dump/dump-page-route)
  (applist/applist-page-route)
  (view-response-data/view-response-data-route)
  (vcr/vcr-routes)
  (upload-download/upload-download-routes)
  (route/files "/ditto-static/" {:root "resources"})
  ; Declare the proxy route after all other routes except 404.
  (GET "/*" [:as request] (ditto-proxy/proxy-handler request))
  (route/not-found "404 dude"))

(def app (handler/site app-routes))
