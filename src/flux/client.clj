(ns flux.client
  (require [flux.update :refer [create-doc]]
           [flux.query :refer [create-query]]
           [flux.response :refer [->clojure]])
  (import [org.apache.solr.client.solrj SolrServer]
          [org.apache.solr.common SolrInputDocument]))

(defn query [^SolrServer solr-server query & [options]]
  (->clojure (.query solr-server (create-query query options))))

(defn request [^SolrServer solr-server request]
  (->clojure (.request solr-server request)))

(defmulti add
  (fn [_ input & _]
    (cond
     (map? input) :one
     :else :default)))

(defmethod add :one [^SolrServer client doc & {:as opts}]
  (->clojure (.add client (create-doc doc))))

(defmethod add :default [^SolrServer client docs & {:as opts}]
  (->clojure (.add client ^java.util.Collection (map create-doc docs))))

(defn commit [^SolrServer client & {:as opts}]
  (->clojure (.commit client)))

(letfn [(v [x]
          (cond (keyword? x) (name x) :else (str x)))]
  (defn delete-by-id [^SolrServer client ids & {:as opts}]
    (->clojure
     (let [ids (if (coll? ids) (map v ids) (v ids))]
      (.deleteById ^SolrServer client ^java.util.List ids)))))

(defn delete-by-query [^SolrServer client q & {:as opts}]
  (->clojure (.deleteByQuery client q)))

(defn optimize
  ([^SolrServer client]
     (->clojure (.optimize client)))
  ([^SolrServer client wait-flush wait-searcher]
     (->clojure (.optimize client wait-flush wait-searcher)))
  ([^SolrServer client wait-flush wait-searcher max-segments]
     (->clojure
      (.optimize client wait-flush wait-searcher max-segments))))

(defn rollback [^SolrServer client]
  (->clojure (.rollback client)))

(defn shutdown [^SolrServer client]
  (->clojure (.shutdown client)))

(defn ping [^SolrServer client]
  (->clojure (.ping client)))
