(defproject com.codesignals/flux "0.1.1"
  :description "A clojure client library for Solr"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.apache.solr/solr-core "4.4.0"]
                 [org.apache.solr/solr-solrj "4.4.0"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}})
