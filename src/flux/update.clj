(ns flux.update
  (:import [org.apache.solr.common SolrInputDocument]))

;; Checking the docs the SolrInputDocument required a none
;; empty ctor already since 7.0.0 
;; https://lucene.apache.org/solr/8_5_2/solr-solrj/index.html?org/apache/solr/common/SolrInputDocument.html

;; NOTE: The result of this function is a SolrInputDocument
;; which throws an exception when printed!
(defn create-doc 
  "creates the inputDocument for solr.
   
   document-map
   : takes a map to convert, adding children with the keyword `:__childDocuments [...]`.
   
   examples
   `{:id 1 :vector {:add [\"22\" \"abc\"]}}`
   will create 
   `\"SolrInputDocument(fields: [id=1, vector={add=[\"22\" \"abc\"]}])\"`
   
   or
   `{:id 1 :__childDocuments [{:id 2} {:id 3}]}`
   will create 
   `\"SolrInputDocument(fields: [id=1], 
     children: [SolrInputDocument(fields: [id=2]), SolrInputDocument(fields: [id=3])])\"`
   "
  
  ^SolrInputDocument [document-map]
  (reduce-kv (fn [^SolrInputDocument doc k v]
               (cond
                 (= k :__childDocuments)
                 (doto doc (.addChildDocuments (map create-doc v)))
                 
                 (map? v)
                 (let [m (java.util.HashMap.)]                   
                   (doseq [[kv fv] v]
                     (doto m
                       (.put (name kv) fv)))
                   (doto doc (.addField (name k) m))
                   doc)
                 
                 :else
                 (doto doc (.addField (name k) v))))
             
             (SolrInputDocument. (java.util.LinkedHashMap.)) 
             document-map))
