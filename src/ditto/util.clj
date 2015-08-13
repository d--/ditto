(ns ditto.util
  "Provides utility functions used throughout the application."
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

(defn- limit-length [s limit]
  "Limit the length of the string s to the given limit number."
  (if (> (count s) limit)
    (subs s 0 limit)
    s))

(defn sanitize [s]
  "Make a string safe to store as a filename."
  (let [shorter-string (limit-length s 120)]
    (string/replace (or shorter-string "") #"[^\w|-]+" "_")))

(defn get-human-readable-date [date]
  "Given a java date long, return a human readable date string."
  (.format
    (java.text.SimpleDateFormat. "MM/dd/yy HH:mm:ss:SSS")
    (java.util.Date. date)))

(defn to-byte-array [f]
  "Converts an argument that can be fed in as an input stream to a byte array."
  (with-open [input (io/input-stream f)
              buffer (java.io.ByteArrayOutputStream.)]
    (io/copy input buffer) (.toByteArray buffer)))
