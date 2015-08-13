; This namespace uses hashcode because we had to make sure that the ID
; calculation was exactly the same as the older version of the service.

(ns ditto.idcalc
  "Calculates IDs for the database given an application name and URL.")

(defn calc-id [app url]
  "Given an app and a URL, calculate the ID to use in the database."
  (hash (str app url)))
