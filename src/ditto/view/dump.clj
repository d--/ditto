(ns ditto.view.dump
  "Provides a view to be used by the dump page."
  (:use [hiccup.core]
        [hiccup.form])
  (:require [ditto.util :as util]
            [ditto.view.common :as common]))

(defn- response-row [app response]
  "Constructs a table row for the dump page given a ditto response map."
  (let [id (response :id)]
    [:tr
      [:td.span2
        (util/get-human-readable-date (response :recordingtimestamp))]
      [:td.span2
        (util/get-human-readable-date (response :lastrequesttimestamp))]
      [:td.span4 (response :location)]
      [:td.span1 (response :responsetimemillis)]
      [:td.span1 (response :status)]
      [:td.span2 (response :contenttype)]
      [:td.span1 (response :contentencoding)]
      [:td.span1
        [:div.btn-group
          [:a.btn.btn-success
            { :title "Playback"
              :href (str "/ditto-ui/" app "/playback/" id)}
            [:i.icon-play]]
          [:a.btn.btn-success
            { :title "Timed Playback"
              :href (str "/ditto-ui/" app "/timedplayback/" id)}
            [:i.icon-step-forward]]
          [:a.btn.btn-warning
            { :title "Rerecord"
              :href (str "/ditto-ui/" app "/recording/" id)}
            [:i.icon-exclamation-sign]]
          [:a.btn.btn-info
            { :title "View Response Data"
              :href (str "/ditto-ui/" app "/view/" id)}
            [:i.icon-list]]]]]))

(defn dump-page [model]
  "Constructs the dump page, given a model containing the responses for a given
  application."
  (common/layout (str "View All Responses: " (model :app))
    (html
      [:small
        [:table#dump-table.table.table-striped
          [:thead
            [:tr
              [:th.span2 "Record Timestamp"]
              [:th.span2 "Last Request Timestamp"]
              [:th.span4 "Location"]
              [:th.span1 "Response Time (ms)"]
              [:th.span1 "Status Code"]
              [:th.span2 "Content Type"]
              [:th.span1 "Content Encoding"]
              [:th.span1 "Action"]]]
          [:tbody {:style "word-break: break-all;"}
            (for [response (model :responses)]
              (response-row (model :app) response))]]])))
