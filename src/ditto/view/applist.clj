(ns ditto.view.applist
  "Provides the list view where applications are listed with modes and actions."
  (:use [hiccup.core])
  (:require [ditto.view.common :as common]
            [ditto.db :as db]))

(defn applist-page [model]
  "Constructs the list page, given a model that contains applications n' stuff"
  (common/layout "List Applications"
    (html
      [:div#main.container
        [:table.table.table-hover
          [:tr
            [:th "Application Name"]
            [:th "Mode"]]
          (for [appmode (model :appmodes)]
            [:tr
              [:td (appmode :app)]
              [:td (appmode :mode)]])]])))
