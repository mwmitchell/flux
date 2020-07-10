<<<<<<< Updated upstream
(defproject star "0.1.0-SNAPSHOT"
=======
(defproject com.codesignals/flux "0.6.3"
>>>>>>> Stashed changes
  :description "A clojure client library for Solr"
  :url "https://github.com/mwmitchell/flux"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
<<<<<<< Updated upstream
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.apache.solr/solr-core "4.2.0"
                  :exclusions [javax.servlet/servlet-api]]
                 [org.apache.solr/solr-solrj "4.2.0"]
                 [com.vividsolutions/jts "1.13"]
                 [ring/ring-core "1.1.8"]])
=======
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.apache.solr/solr-core "8.5.2"]
                 [org.apache.solr/solr-solrj "8.5.2"]]
  :plugins [[lein-codox "0.10.7"]]
  :profiles {:dev {:dependencies [[midje "1.9.9"]
                                  [org.slf4j/slf4j-jdk14 "1.7.30"]
                                  ;[org.slf4j/slf4j-log4j12 "1.7.30"]
                                  ;[org.slf4j/slf4j-api "1.7.30"]
                                  ;[org.slf4j/jcl-over-slf4j "1.7.30"]                                  
                                  [commons-logging "1.2"]]
                   :resource-paths ["dev-resources"]
                   :plugins [[lein-midje "3.2.1"]]}}
  :source-paths ["src"]
  :test-paths ["test"])
>>>>>>> Stashed changes
