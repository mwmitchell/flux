(ns flux.update
  (:import [org.apache.solr.common SolrInputDocument]))

;; Checking the docs the SolrInputDocument required a none
;; empty ctor already since 7.0.0 
;; https://lucene.apache.org/solr/8_5_2/solr-solrj/index.html?org/apache/solr/common/SolrInputDocument.html

;; NOTE: The result of this function is a SolrInputDocument
;; which throws an exception when printed!
(defn create-doc ^SolrInputDocument 
  "creates the inputDocument for solr.
   
   document-map
   : takes a map to convert 
   
   examples
   {:id 1 :map {:int 22 :string \"abc\"}} 
   will create 
   \"SolrInputDocument(fields: [id=1, map={string=abc, int=22}])\"
   
   or
   {:id 1 :_childDocuments_ [{:id 2} {:id 3}]}
   will create 
   \"SolrInputDocument(fields: [id=1], 
     children: [SolrInputDocument(fields: [id=2]), SolrInputDocument(fields: [id=3])])\"
   "
  
  [document-map]
  (reduce-kv (fn [^SolrInputDocument doc k v]
               (cond
                 (= k :_childDocuments_)
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
