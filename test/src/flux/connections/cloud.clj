(ns src.flux.connections.cloud
  (:require
   [midje.sweet :refer :all]
   [flux.core :refer :all]
   [flux.collections :refer [changeDefaultCollection]]
   [flux.connections.cloud :refer [create]]))

(fact "functiontest-against-cloud"
      (let [conn (create ["localhost:2181"])]
        (type (set-default-connection conn)) => org.apache.solr.client.solrj.impl.CloudSolrClient
        (changeDefaultCollection conn :flux_test) => nil
        (get-in (add [{:id 1} {:id 2}]) [:responseHeader :status]) => 0
        (get-in (commit) [:responseHeader :status]) => 0
        (some
         #(= 1 %)
         (map :id
              (get-in
               (query "*:*")
               [:response :docs]))) => true
		 (some
         #(= 1 %)
         (map :id
              (get-in
               (query "*:*" {:fq {:id 1}})
               [:response :docs]))) => true
        (get-in (delete-by-query "*:*") [:responseHeader :status]) => 0
        (get-in (commit) [:responseHeader :status]) => 0))

(fact "ctor-test-cloud"
      (type (set-default-connection (create ["localhost:2181"] "/"))) =>
      org.apache.solr.client.solrj.impl.CloudSolrClient
      (type (set-default-connection (create ["localhost:2181"] "/" :flux_test)))
      => org.apache.solr.client.solrj.impl.CloudSolrClient)
