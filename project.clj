(defproject electrojet "0.1.0-SNAPSHOT"
  :description "A clojure client library for Solr"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.apache.solr/solr-core "4.2.0"
                  :exclusions [javax.servlet/servlet-api]]
                 [org.apache.solr/solr-solrj "4.2.0"]
                 [com.vividsolutions/jts "1.13"]
                 [ring/ring-core "1.1.8"]])
