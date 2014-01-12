(ns flux.client
  (require [flux.update :refer [create-doc]]
           [flux.query :refer [create-query]]
           [flux.response :refer [response-base]]))

(defn query [solr-server query & [options]]
  (response-base (.query solr-server (create-query query options))))

(defmulti add
  (fn [_ input & _]
    (cond
     (map? input) :one
     :else :default)))

(defmethod add :one [client doc & {:as opts}]
  (response-base (.add client (create-doc doc))))

(defmethod add :default [client docs & {:as opts}]
  (response-base (.add client (map create-doc docs))))

(defn commit [client & {:as opts}]
  (response-base (.commit client)))

(letfn [(v [x]
          (cond (keyword? x) (name x) :else (str x)))]
  (defn delete-by-id [client ids & {:as opts}]
    (response-base
     (.deleteById client (if (coll? ids) (map v ids) (v ids))))))

(defn delete-by-query [client q & {:as opts}]
  (response-base (.deleteByQuery client q)))

(defn optimize
  ([client]
     (response-base (.optimize client)))
  ([client wait-flush wait-searcher]
     (response-base (.optimize client wait-flush wait-searcher)))
  ([client wait-flush wait-searcher max-segments]
     (response-base
      (.optimize client wait-flush wait-searcher max-segments))))

(defn rollback [client]
  (response-base (.rollback client)))

(defn shutdown [client]
  (.shutdown client))

(defn ping [client]
  (.ping client))
