(ns flux.update
  (:import [org.apache.solr.common SolrInputDocument]))

;; Checking the docs the SolrInputDocument required a none
;; empty ctor already since 7.0.0 
;; https://lucene.apache.org/solr/8_5_2/solr-solrj/index.html?org/apache/solr/common/SolrInputDocument.html

;; NOTE: The result of this function is a SolrInputDocument
;; which throws an exception when printed!
(defn create-doc ^SolrInputDocument [document-map]
  (reduce-kv (fn [^SolrInputDocument doc k v]
               (if (map? v)
                 (let [m (doto (java.util.HashMap.)
                           (.put (name (key (first v))) (val (first v))))]
                   (doto doc (.addField (name k) m))
                   doc)
                 (doto doc (.addField (name k) v))))
             (SolrInputDocument. (java.util.LinkedHashMap.)) document-map))
