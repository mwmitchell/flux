(ns flux.update
  (import [org.apache.solr.common SolrInputDocument]))

;; NOTE: The result of this function is a SolrInputDocument
;; which throws an exception when printed!
(defn create-doc [document-map]
  (reduce-kv #(if (map? %3)
                (let [m (doto (java.util.HashMap.)
                          (.put (name (key (first %3))) (val (first %3))))]
                  (doto %1 (.addField (name %2) m))
                  %1)
                (doto %1 (.addField (name %2) %3)))
             (SolrInputDocument.) document-map))
