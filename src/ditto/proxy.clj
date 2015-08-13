(ns ditto.proxy
  "Provides a function to handle proxy requests."
  (:require [clojure.string :as string]
            [ditto.util :as util]
            [ditto.vcr :as vcr]))

(defn- add-query-string-if-needed [q]
  "Return the query string with a question mark if it exists, else nil."
  (when (not (string/blank? q)) (str "?" q)))

(defn- get-url-from-request [r]
  "Put together the URL requested in the proxy handler."
  (let [scheme (name (r :scheme))
        host ((r :headers) "host")
        uri (r :uri)
        query-string (r :query-string)]
    (str scheme "://" host uri
      (add-query-string-if-needed query-string))))

(defn- get-app-from-user-agent [a]
  "Attempt to get the application name from between ditto- -ditto in the user-agent
   string.  If we find one, return it sanitized, else return null."
  (let [result (util/sanitize (second (re-find #"ditto-(\w.*)-ditto" a)))]
    (if (empty? result)
      nil
      result)))

(defn- get-app-from-request [r]
  "Come up with a application name from the request.  First we'll check the user
   agent to see if there's an application name between ditto- -ditto.  If there is
   not, we'll use the IP address.  The end result will have all non-word
   characters replaced with underscores."
  (let [user-agent ((r :headers) "user-agent")
        ip (r :remote-addr)]
    (or
      (get-app-from-user-agent user-agent)
      (util/sanitize ip))))

(defn proxy-handler [request]
  "Handles proxy requests."
  (let [app (get-app-from-request request)
        url (get-url-from-request request)
        request-headers (request :headers)]
    (when (not (contains? request-headers "ditto-request"))
      (vcr/modal app url request-headers))))
