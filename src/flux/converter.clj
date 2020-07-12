(ns flux.converter
  (:import [org.apache.solr.client.solrj SolrResponse]
           [org.apache.solr.common.util NamedList]
           [org.apache.solr.common SolrDocumentList SolrDocument]
           [org.apache.solr.common SolrInputDocument]
           [java.util ArrayList]))

;; Stores the setup of output choosen to be strings or parsed
(defonce string-only? (atom false))

(defn set-stringoutput-only 
  "Set to true if you prefer string output 
   instead of Longs, Doubles and Booleans in return
   
   bool
   : true = strings only; false = try to parse output"
  [bool]
  (when (boolean? bool)
    (reset! string-only? bool)))

(defmulti ->clojure 
  "Converts the solr Data back to clojure collections
   
   class
   : can be NamedList, SolrDocumentList, SolrDocument, SolrResponse, SolrInputDocument,
     java.util.LinkedHashMap, String or whatever (whatever is returned only)"
  class)

(defmethod ->clojure NamedList [^NamedList obj]
  (into {} (for [[k v] obj] [(keyword k) (->clojure v)])))

(defmethod ->clojure ArrayList [obj]
  (mapv ->clojure obj))

(defmethod ->clojure SolrDocumentList [^SolrDocumentList obj]
  (merge
   {:numFound (.getNumFound obj)
    :start (.getStart obj)
    :docs (mapv ->clojure (iterator-seq (.iterator obj)))}
   (when-let [ms (.getMaxScore obj)]
     {:maxScore ms})))

(defmethod ->clojure SolrDocument [^SolrDocument obj]
  (reduce
   (fn [acc f]
     (assoc acc (keyword f) (->clojure (.getFieldValue obj f))))
   {}
   (.getFieldNames obj)))

(defmethod ->clojure SolrResponse [^SolrResponse obj]
  (->clojure (.getResponse obj)))

(defmethod ->clojure SolrInputDocument [^SolrInputDocument obj]
  (let [fields 
        (reduce
         (fn [acc o]
           (assoc acc (keyword o) (.getFieldValue obj o)))
         {}
         (.getFieldNames obj))
        children (.getChildDocuments obj)]
    (if (nil? children)
      fields
      (assoc fields :__childDocuments (map ->clojure children)))))

(defmethod ->clojure java.util.LinkedHashMap [obj]
  (into {} (for [[k v] obj] [(keyword k) (->clojure v)])))

(defmethod ->clojure String [obj]
  (if @string-only? 
    obj 
    (try
      (Long/parseLong obj)
      (catch NumberFormatException _
        (try
          (Double/parseDouble obj)
          (catch NumberFormatException _
            (cond
              (= obj "false")
              false
              (= obj "true")
              true
              :else
              obj)))))))

(defmethod ->clojure :default [obj]
  obj)
