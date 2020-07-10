(ns src.flux.connections.cloud
  (:require
   [clojure.test :refer [deftest is]]
   [flux.core :refer :all]
   [flux.collections :refer [changeDefaultCollection]]
   [flux.connections.cloud :refer [create]]))

(deftest cloud-solr
  (let [conn (create ["localhost:2181"])]
    (is (= org.apache.solr.client.solrj.impl.CloudSolrClient
           (type (set-default-connection conn)))
        "Created correct connection type will fail intentionally 
         if there is no zookeeper running to connect to solr with
         collection flux_test")
    (is (nil? (changeDefaultCollection conn :flux_test))
        "Failed to change the default collection"))
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

(deftest cloud-solr-ctor
  (is (= org.apache.solr.client.solrj.impl.CloudSolrClient
         (type (set-default-connection (create ["localhost:2181"] "/"))))
      "Created correct connection type ")
  (is (= org.apache.solr.client.solrj.impl.CloudSolrClient
         (type (set-default-connection (create ["localhost:2181"] "/" :new_core))))
      "Created correct connection type "))
