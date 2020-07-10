(ns src.flux.connections.http
  (:require
   [clojure.test :refer [deftest is]]
   [flux.core :refer :all]
   [flux.connections.http :refer [create]]))

(deftest http-solr
  (is (= org.apache.solr.client.solrj.impl.HttpSolrClient
         (type (set-default-connection (create "http://localhost:8983/solr" :flux_test))))
      "Created correct connection type - will fail if no solr is running on your localhost 
       with a collection flux_test")
  (is (= 0 (get-in (add [{:id 1} {:id 2}]) [:responseHeader :status]))
      "Was able to drop in 2 test ids")
  (is (= 0 (get-in (commit) [:responseHeader :status]))
      "Commited successfully")
  (is (some
       #(= 1 %)
       (map :id
            (get-in
             (query "*:*")
             [:response :docs])))
      "Testing if we can find back id 1")
  (is (= 0 (get-in (delete-by-query "*:*") [:responseHeader :status]))
      "Deleting everything")
  (is (= 0 (get-in (commit) [:responseHeader :status]))
      "Commited successfully"))
