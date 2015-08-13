(ns ditto.db
  "Provides db abstraction functions over JDBC/H2 for ditto response CRUD."
  (:use [ring.util.response])
  (:require [ditto.idcalc :as idcalc]
            [ditto.util :as util]
            [clojure.java.io :as io]
            [clojure.string :as string
              :only [replace]]
            [clojure.java.jdbc :as jdbc]))

; This is where we can define a default directory.
; Probably should set this to get a java system property at some point.
(def db-file-dir "./")
(def db-file-ext ".h2.db")

(def base-db-params
  { :classname "org.h2.Driver"
    :subprotocol "h2"
    :user ""
    :password ""})

(defn- app-db-object [app]
  "Returns a JDBC connection map to a file by app name."
  (let [db-protocol "file"
        db-host db-file-dir
        db-name app]
    (assoc base-db-params :subname
      (str db-protocol ":" db-host "/" db-name))))

(defn- get-full-db-file-path [app]
  "Returns the full path to a database file by app name."
  (str db-file-dir app db-file-ext))

(defn- remove-extension [filename]
  "Removes the database extension from the filename."
  (string/replace filename
    (re-pattern (str db-file-ext "$"))
    ""))

(defn- list-db-file-names []
  "Returns a seq of all database names in the db-file-dir."
  (filter
    #(.endsWith % db-file-ext)
    (seq (.list (io/file db-file-dir)))))

(defn get-names-of-apps-with-dbs []
  "Returns a list of existing applications represented by databases."
  (map remove-extension (list-db-file-names)))

(defn- get-app-file-object [app]
  "Creates a java File object from an app name."
  (io/file (get-full-db-file-path app)))

(defn- select-db [app]
  "Creates a database file and sets up the table if the file doesn't exist."
  (let [db-file-handle (get-app-file-object app)
        selected-db (app-db-object app)]
    (when (not (.isFile db-file-handle))
      (jdbc/with-connection selected-db
        (jdbc/create-table "DittoResponse"
          [:id "BIGINT" "PRIMARY KEY" "NOT NULL"]
          [:app "VARCHAR(20000)" "NOT NULL"]
          [:location "VARCHAR(20000)" "NOT NULL"]
          [:recordingtimestamp "BIGINT" "NOT NULL"]
          [:lastrequesttimestamp "BIGINT" "NOT NULL"]
          [:responsetimemillis "INTEGER" "NOT NULL"]
          [:status "INTEGER" "NOT NULL"]
          [:contenttype "VARCHAR(2000)" "NOT NULL"]
          [:contentencoding "VARCHAR(2000)" "NOT NULL"]
          [:body "BLOB" "NOT NULL"])))
    selected-db))

(defn convert-and-store-clj-http-response [app url timestamp response]
  "Takes a clj-http response map and maps it to a row in the DittoResponse table
  of the database.  If the database doesn't exist, it will be created."
  (let [id (idcalc/calc-id app url)
        headers (response :headers)]
    (jdbc/with-connection (select-db app)
      (jdbc/update-or-insert-values "DittoResponse"
        ["ID=?" id]
        { :id id
          :app app
          :location url
          :recordingtimestamp timestamp
          :lastrequesttimestamp timestamp
          :responsetimemillis (response :request-time)
          :status (response :status)
          :contenttype (str (headers "content-type"))
          :contentencoding (str (headers "content-encoding"))
          :body (or (response :body) "")}))
    response))

(defn get-all-responses-metadata [app]
  "Retrieves all the response maps minus the response bodies."
  (jdbc/with-connection (select-db app)
    (jdbc/transaction
      (jdbc/with-query-results results
        [(str "SELECT ID, App, Location, RecordingTimestamp, "
              "LastRequestTimestamp, ResponseTimeMillis, Status, "
              "ContentType, ContentEncoding FROM DittoResponse")]
        ; This sequence must not be lazily evaluated because the database
        ; transaction will close before we do something with the result.
        (doall results)))))

(defn- jdbc-blob-to-byte-array [blob]
  "Converts a JDBC Blob object to a byte array."
  (with-open [reader (.getBinaryStream blob)]
    (util/to-byte-array reader)))

(defn get-ditto-response-by-id [app id]
  "Gets a ditto response map from the database by ID."
  (jdbc/with-connection (select-db app)
    (jdbc/transaction
      (jdbc/with-query-results results
        [(str "SELECT ID, App, Location, RecordingTimestamp, "
              "LastRequestTimestamp, ResponseTimeMillis, Status, "
              "ContentType, ContentEncoding, Body FROM DittoResponse "
              "WHERE ID=?") id]
        (let [result (first results)]
          (if result
            (assoc result :body
              (jdbc-blob-to-byte-array (result :body)))
            nil))))))

(defn update-response-body [app id body reqtime]
  "Updates the body of the response given by app and ID."
  (jdbc/with-connection (select-db app)
    (jdbc/update-values "DittoResponse"
      ["ID=?" id]
      { :body (or body "")
        :lastrequesttimestamp reqtime})))

(defn update-last-request-time [app id reqtime]
  "Updates a response's last request time."
  (jdbc/with-connection (select-db app)
    (jdbc/update-values "DittoResponse"
      ["ID=?" id]
      {:lastrequesttimestamp reqtime})))
