(ns flux.client
  (:require [flux.update :refer [create-doc]]
            [flux.query :refer [create-query]]
            [flux.response :refer [->clojure]])
  (:import [org.apache.solr.client.solrj SolrClient]))

(defn query [^SolrClient solr-server query & [options]]
  (->clojure (.query solr-server (create-query query options))))

(defn request [^SolrClient solr-server request]
  (->clojure (.request solr-server request)))

(defmulti add
  (fn [_ input & _]
    (cond
     (map? input) :one
     :else :default)))

(defmethod add :one [^SolrClient client doc & {:as opts}]
  (->clojure (.add client (create-doc doc))))

(defmethod add :default [^SolrClient client docs & {:as opts}]
  (->clojure (.add client ^java.util.Collection (map create-doc docs))))

(defn commit [^SolrClient client & {:as opts}]
  (->clojure (.commit client)))

(letfn [(v [x]
          (cond (keyword? x) (name x) :else (str x)))]
  (defn delete-by-id [^SolrClient client ids & {:as opts}]
    (->clojure
     (let [ids (if (coll? ids) (map v ids) (v ids))]
      (.deleteById ^SolrClient client ^java.util.List ids)))))

(defn delete-by-query [^SolrClient client q & {:as opts}]
  (->clojure (.deleteByQuery client q)))

(defn optimize
  ([^SolrClient client]
     (->clojure (.optimize client)))
  ([^SolrClient client wait-flush wait-searcher]
     (->clojure (.optimize client wait-flush wait-searcher)))
  ([^SolrClient client wait-flush wait-searcher max-segments]
     (->clojure
      (.optimize client wait-flush wait-searcher max-segments))))

(defn rollback [^SolrClient client]
  (->clojure (.rollback client)))

(defn shutdown [^SolrClient client]
  (->clojure (.shutdown client)))

(defn ping [^SolrClient client]
  (->clojure (.ping client)))
