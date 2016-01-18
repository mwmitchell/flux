(ns flux.response
  (:import [org.apache.solr.client.solrj SolrResponse]
           [org.apache.solr.common.util NamedList SimpleOrderedMap]
           [org.apache.solr.common SolrDocumentList SolrDocument]
           [org.apache.solr.common SolrInputDocument]
           [java.util ArrayList]))

;; TODO: Rename this ns to something like "conversion", not "response"

(defmulti ->clojure class)

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
  (reduce
   (fn [acc o]
     (assoc acc (keyword o) (.getFieldValue obj o)))
   {}
   (.getFieldNames obj)))

(defmethod ->clojure java.util.LinkedHashMap [obj]
  (into {} (for [[k v] obj] [(keyword k) (->clojure v)])))

(defmethod ->clojure :default [obj]
  obj)
