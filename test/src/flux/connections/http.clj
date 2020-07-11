(ns src.flux.connections.http
  (:require
   [midje.sweet :refer :all]
   [flux.core :refer :all]
   [flux.connections.http :refer [create]]))

(fact "functiontest-against-cloud"
      (let [conn (create "http://localhost:8983/solr" :flux_test)]
        (type (set-default-connection conn)) => org.apache.solr.client.solrj.impl.HttpSolrClient
        (get-in (add [{:id 1} {:id 2}]) [:responseHeader :status]) => 0
        (get-in (commit) [:responseHeader :status]) => 0
        (some
         #(= 1 %)
         (map :id
              (get-in
               (query "*:*")
               [:response :docs]))) => true
        (get-in (delete-by-query "*:*") [:responseHeader :status]) => 0
        (get-in (commit) [:responseHeader :status]) => 0))
