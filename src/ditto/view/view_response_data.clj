(ns ditto.view.view-response-data
  "Provides a view for viewing ditto response data."
  (:use [hiccup.core]
        [hiccup.form])
  (:require [ditto.util :as util]
            [ditto.view.common :as common]))

(defn data-page [model]
  "Constructs the data page, given a response in the model, and an error/message
   if applicable."
  (let [r (model :response)]
    (common/layout (if r (str "Response: " (r :id)) "Error")
      (html
        [:div#main.container
          (when r (html
            [:table#response-data-table.table.table-bordered
              [:tr
                [:td "ID"]
                [:td (r :id)]]
              [:tr
                [:td "Location"]
                [:td {:style "word-break: break-all;"}
                  [:a {:href (r :location)} (r :location)]]]
              [:tr
                [:td "Application Name"]
                [:td (r :app)]]
              [:tr
                [:td "Record Timestamp"]
                [:td (util/get-human-readable-date (r :recordingtimestamp))]]
              [:tr
                [:td "Last Request Timestamp"]
                [:td (util/get-human-readable-date (r :lastrequesttimestamp))]]
              [:tr
                [:td "Response Time"]
                [:td (r :responsetimemillis)]]
              [:tr
                [:td "Status Code Returned"]
                [:td (r :status)]]
              [:tr
                [:td "Content Type"]
                [:td (r :contenttype)]]
              [:tr
                [:td "Content Encoding"]
                [:td (r :contentencoding)]]]
            [:div.btn-group
              [:a.btn.btn-success
                {:href (str "/ditto-ui/" (r :app) "/playback/" (r :id))}
                "Playback"]]
            [:div.btn-group
              [:a.btn.btn-success
                {:href (str "/ditto-ui/" (r :app) "/timedplayback/" (r :id))}
                "Timed Playback"]]
            [:div.btn-group
              [:a.btn.btn-warning
                {:href (str "/ditto-ui/" (r :app) "/recording/" (r :id))}
                "Rerecord"]]
            [:div.btn-group
              [:a.btn.btn-info
                {:href (str "/ditto-ui/" (r :app) "/download/" (r :id))}
                "Download"]]
            [:form
              { :action "/ditto-ui/upload"
                :method "post"
                :enctype "multipart/form-data"}
              (label "file-input" "Upload new body")
              [:input#file-input
                {:type "file" :name "file_upload"}]
              [:input#app-name-input
                {:type "hidden" :name "app" :value (r :app)}]
              [:input#id-input
                {:type "hidden" :name "id" :value (r :id)}]
              [:div.buttons
                [:input#submit-button.btn.btn-primary
                  {:type "submit" :value "Submit"}]]
              [:p.text-warning [:small
                (str
                  "Note: The content-length header will be dynamically "
                  "modified to match the file you upload.  It is the ONLY "
                  "header where this will be the case.  Content-type will "
                  "remain the same. (Eg: You cannot upload an image in place "
                  "of a text response.")]]]))
          (when (model :message)
            [:div#message.text-success (model :message)])
          (when (model :error)
            [:div#error.text-error (model :error)])]))))
