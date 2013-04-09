(ns flux.unit.update
  (:use clojure.test
        flux.update
        midje.sweet))

(fact "create-doc"
  (class (create-doc {:id 1})) => org.apache.solr.common.SolrInputDocument)
