(ns flux.document
  (:import [org.apache.solr.common SolrInputDocument]
           [org.apache.solr.common SolrInputField])
  (:require [flux.update :as u]))

(defmulti ->hash-map class)

(defmethod ->hash-map clojure.lang.PersistentArrayMap [obj]
  (java.util.HashMap.
   (into {} (for [[k v] obj]
              [(name k) (->hash-map v)]))))

(defmethod ->hash-map :default [obj]
  obj)

(defn create-doc [document-map]
  (reduce-kv
   #(doto %1
      (.addField
       (name %2)
       (if (map? %3) (->hash-map %3) %3)))
   (SolrInputDocument.)
   document-map))
