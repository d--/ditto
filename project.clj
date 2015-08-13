(defproject d.lab/ditto "0.1.0-SNAPSHOT"
  :description "ditto: Recording / Playback Proxy"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [compojure "1.1.5"]
                 [clj-http "0.7.0"]
                 [com.h2database/h2 "1.3.170"]
                 [hiccup "1.0.3"]
                 [ring/ring-jetty-adapter "1.2.1"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler ditto.core/app}
  :aot [ditto.runner]
  :main ditto.runner)
