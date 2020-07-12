(defproject com.codesignals/flux "0.7.0"
  :description "A clojure client library for Solr 8.5.2"
  :url "https://github.com/mwmitchell/flux"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.apache.solr/solr-core "8.5.2"]
                 [org.apache.solr/solr-solrj "8.5.2"]]
  :plugins [[lein-codox "0.10.7"]]
  :profiles {:dev {:dependencies [[midje "1.9.9"]
                                  [org.slf4j/slf4j-jdk14 "1.7.30"]
                                  ;[org.slf4j/slf4j-log4j12 "1.7.30"]                              
                                  [commons-logging "1.2"]]
                   :resource-paths ["dev-resources"]
                   :plugins [[lein-midje "3.2.1"]]}}
  :source-paths ["src"]
  :test-paths ["test"]
  :codox {:source-uri "https://github.com/mwmitchell/flux/tree/master/{filepath}#L{line}"
          :metadata {:doc/format :markdown}})
