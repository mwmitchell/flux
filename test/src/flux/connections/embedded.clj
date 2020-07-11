(ns src.flux.connections.embedded
  (:require
   [midje.sweet :refer :all]
   [flux.core :refer :all]
   [flux.connections.embedded :refer :all]))

(def spy (atom nil))

(fact "functiontest-against-embedded"
      (let [cc (create-core-container)] 
        (try    
          (type cc) => org.apache.solr.core.CoreContainer
          (.load cc) => nil
          (.waitForLoadingCoresToFinish cc 60000) => nil          
          (let [con (create cc :flux_test)]
            (try 
              (type (set-default-connection con)) =>
              org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
              (get-in (add [{:id 1} {:id 2}]) [:responseHeader :status]) => 0
              (get-in (commit) [:responseHeader :status]) => 0
              (let [docs (get-in
                          (query "*:*")
                          [:response :docs])]
                (reset! spy docs)
                (some
                 #(= 1 %)
                 (map :id docs)) => true)
              (get-in (delete-by-query "*:*") [:responseHeader :status]) => 0
              (get-in (commit) [:responseHeader :status]) => 0
              (finally (.close con))))
          (finally (doto cc
                     (.shutdown))))))

;; Helper to create the core for testing
;; or debugging or just inspiration
;; (def cc2 (create-core-container))
;; (.load cc2)
;; (.getAllCoreNames cc2)
;; If corenames are empty creat it
;; (.create cc2 "flux_test" {})
;; (.shutdown cc2)
;; (.isShutDown cc2)