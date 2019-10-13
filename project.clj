(defproject com.codesignals/flux "0.6.1"
  :description "A clojure client library for Solr"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.apache.solr/solr-core "8.2.0"]
                 [org.apache.solr/solr-solrj "8.2.0"]]
  :profiles {:dev {:dependencies [[midje/midje "1.9.9"]
                                  [org.slf4j/slf4j-log4j12 "1.7.28"]
                                  [commons-logging "1.2"]]
                   :resource-paths ["dev-resources"]
                   :plugins [[lein-midje "3.2.1"]]}})