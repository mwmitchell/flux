;; (ns src.flux.connections.embedded
;;   (:require
;;    [clojure.test :refer [deftest is]]
;;    [flux.core :refer :all]
;;    [flux.connections.embedded :refer :all]))

;; (deftest embedded-solr
;;   (let [cc (create-core-container)]
;;    (is (= org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
;;           (type (set-default-connection (create cc :flux_test))))
;;        "Created correct connection type"))
;;   (is (= 0 (get-in (add [{:id 1} {:id 2}]) [:responseHeader :status]))
;;       "Was able to drop in 2 test ids")
;;   (is (= 0 (get-in (commit) [:responseHeader :status]))
;;       "Commited successfully")
;;   (is (some
;;        #(= 1 %)
;;        (map :id
;;             (get-in
;;              (query "*:*")
;;              [:response :docs])))
;;       "Testing if we can find back id 1")
;;   (is (= 0 (get-in (delete-by-query "*:*") [:responseHeader :status]))
;;       "Deleting everything")
;;   (is (= 0 (get-in (commit) [:responseHeader :status]))
;;       "Commited successfully"))

;; (with-connection 
;;   (let [cc (create-core-container)]  
;;     (create cc :flux_test))
;;   (add [{:id 1} {:id 2}]))
      