(defproject com.codesignals/flux "0.6.2"
  :description "A clojure client library for Solr"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.apache.solr/solr-core "8.5.2"]
                 [org.apache.solr/solr-solrj "8.5.2"]]
  :profiles {:dev {:dependencies [[midje "1.9.9"]
                                  [org.slf4j/slf4j-log4j12 "1.7.30"]
                                  [commons-logging "1.2"]]
                   :resource-paths ["dev-resources"]
                   :plugins [[lein-midje "3.2.1"]]}}
  :source-paths ["src"]
  :test-paths ["test"])
