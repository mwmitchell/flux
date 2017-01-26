(ns flux.update
  (:import (org.apache.solr.common SolrInputDocument
                                   SolrInputField)))

;; NOTE: The result of this function is a SolrInputDocument
;; which throws an exception when printed!
(defn create-doc ^SolrInputDocument [document-map]
  (reduce-kv (fn [^SolrInputDocument doc k v]
               (if (map? v)
                 (doto doc (.addChildDocument (create-doc v)))
                 (doto doc (.addField (name k) v))))
             (SolrInputDocument. (java.util.HashMap.)) document-map))
