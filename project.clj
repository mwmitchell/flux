(defproject com.codesignals/flux "0.6.0"
            :description "A clojure client library for Solr"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.apache.solr/solr-core "5.2.1"]
                           [org.apache.solr/solr-solrj "5.2.1"]
                           [javax.servlet/servlet-api "2.5"]]

            :plugins [[lein-ancient "0.6.5"]]
            :profiles {:dev {:dependencies   [[midje "1.6.3"]
                                              [org.slf4j/slf4j-log4j12 "1.7.12"]
                                              [commons-logging "1.2"]]
                             :resource-paths ["dev-resources"]
                             :plugins        [[lein-midje "3.1.1"]]}})
