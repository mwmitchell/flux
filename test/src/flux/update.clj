(ns src.flux.update
  (:require [flux.update :refer :all]            
            [midje.sweet :refer :all]))

(fact "create-doc"
  (class (create-doc {:id 1})) => org.apache.solr.common.SolrInputDocument)
