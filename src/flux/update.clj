(ns flux.update
  (import [org.apache.solr.common SolrInputDocument]))

;; NOTE: The result of this function is a SolrInputDocument
;; which throws an exception when printed!
(defn create-doc ^SolrInputDocument [document-map]
  (reduce-kv (fn [^SolrInputDocument doc k v]
               (cond
                 (= k :_childDocuments_)
                 (doto doc (.addChildDocuments (map create-doc v)))
                 (map? v)
                 (let [m (doto (java.util.HashMap.)
                           (.put (name (key (first v))) (val (first v))))]
                   (doto doc (.addField (name k) m))
                   doc)
                 :else
                 (doto doc (.addField (name k) v))))
             (SolrInputDocument.) document-map))
