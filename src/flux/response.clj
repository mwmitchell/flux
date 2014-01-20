(ns flux.response
  (:import [org.apache.solr.client.solrj SolrResponse]
           [org.apache.solr.common.util NamedList SimpleOrderedMap]
           [org.apache.solr.common SolrDocumentList SolrDocument]
           [org.apache.solr.common SolrInputDocument]
           [java.util ArrayList]))

;; TODO: Rename this ns to something like "conversion", not "response"

(defmulti ->clojure class)

(defmethod ->clojure SimpleOrderedMap [obj]
  (reduce
   (fn [acc o]
     (assoc acc (keyword (.getKey o)) (->clojure (.getValue o))))
   {}
   (iterator-seq (.iterator obj))))

(defmethod ->clojure NamedList [obj]
  (mapv
   #(vector (.getKey %) (->clojure (.getValue %)))
   (iterator-seq (.iterator obj))))

(defmethod ->clojure ArrayList [obj]
  (mapv ->clojure obj))

(defmethod ->clojure SolrDocumentList [obj]
  (merge
   {:numFound (.getNumFound obj)
    :start (.getStart obj)
    :docs (mapv ->clojure (iterator-seq (.iterator obj)))}
   (when-let [ms (.getMaxScore obj)]
     {:maxScore ms})))

(defmethod ->clojure SolrDocument [obj]
  (reduce
   (fn [acc f]
     (assoc acc (keyword f) (->clojure (.getFieldValue obj f))))
   {}
   (.getFieldNames obj)))

(defmethod ->clojure SolrResponse [obj]
  (->clojure (.getResponse obj)))

(defmethod ->clojure SolrInputDocument [obj]
  (reduce
   (fn [acc o]
     (assoc acc (keyword o) (.getFieldValue obj o)))
   {}
   (.getFieldNames obj)))

(defmethod ->clojure :default [obj]
  obj)
