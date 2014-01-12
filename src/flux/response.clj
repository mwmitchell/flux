(ns flux.response
  (:import [org.apache.solr.client.solrj.response SolrResponseBase]
           [org.apache.solr.common.util NamedList SimpleOrderedMap]
           [org.apache.solr.common SolrDocumentList]
           [java.util ArrayList]))

(defn create-map-from-document [document]
  (into {}
        (for [fld (.getFieldNames document)]
          {(keyword fld) (.getFieldValue document fld)})))

(declare convert-value)

(defn convert-key
  [k]
  (if (string? k)
    (keyword k)
    k))

(defn remap-response
  [l]
  {:maxScore (.getMaxScore l)
   :numFound (.getNumFound l)
   :start (.getStart l)
   :docs (map create-map-from-document (iterator-seq (.iterator l)))})

(defn convert-map-entry
  "Converts a MapEntry from a NamedList into a vector with 2 values. Nested NamedLists are recursively converted. "
  [map-entry]
  (let [k (convert-key (.getKey map-entry))
        v (convert-value (.getValue map-entry))]
    [k v]))

(defn- uniquify-paired-seq
  "removes values when dup-key-in-paired-seq? returns true"
  [coll]
  (map #(first (val %1)) (group-by first coll)))

(defn convert-named-list
  "Converts a NamedList into a array-map. Nested NamedLists are recursively converted. "
  [named-list]
  (let [itrseq (iterator-seq (.iterator named-list))
        converted (map convert-map-entry itrseq)
        reduced (uniquify-paired-seq converted)
        flattened (apply concat reduced)
        result (apply array-map flattened)]
    result))

(defn convert-value
  [v]
  (cond
   (instance? SimpleOrderedMap v) (convert-named-list v)
   (instance? SolrDocumentList v) (remap-response v)
   (instance? NamedList v) (convert-named-list v)
   (instance? ArrayList v) (vec (map convert-value v))
   :else v))

;; (defn update-response [ur]
;;   {:status (.getStatus ur)
;;    :elaspsedTime (.getElapsedTime ur)
;;    :qTime (.getQTime ur)
;;    :requestUrl (.getRequestUrl ur)})

(defn response-base
  [^SolrResponseBase r]
  (convert-named-list (.getResponse r)))
