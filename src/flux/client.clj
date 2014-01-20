(ns flux.client
  (require [flux.update :refer [create-doc]]
           [flux.query :refer [create-query]]
           [flux.response :refer [->clojure]]))

(defn query [solr-server query & [options]]
  (->clojure (.query solr-server (create-query query options))))

(defn request [solr-server request]
  (->clojure (.request solr-server request)))

(defmulti add
  (fn [_ input & _]
    (cond
     (map? input) :one
     :else :default)))

(defmethod add :one [client doc & {:as opts}]
  (->clojure (.add client (create-doc doc))))

(defmethod add :default [client docs & {:as opts}]
  (->clojure (.add client (map create-doc docs))))

(defn commit [client & {:as opts}]
  (->clojure (.commit client)))

(letfn [(v [x]
          (cond (keyword? x) (name x) :else (str x)))]
  (defn delete-by-id [client ids & {:as opts}]
    (->clojure
     (.deleteById client (if (coll? ids) (map v ids) (v ids))))))

(defn delete-by-query [client q & {:as opts}]
  (->clojure (.deleteByQuery client q)))

(defn optimize
  ([client]
     (->clojure (.optimize client)))
  ([client wait-flush wait-searcher]
     (->clojure (.optimize client wait-flush wait-searcher)))
  ([client wait-flush wait-searcher max-segments]
     (->clojure
      (.optimize client wait-flush wait-searcher max-segments))))

(defn rollback [client]
  (->clojure (.rollback client)))

(defn shutdown [client]
  (->clojure (.shutdown client)))

(defn ping [client]
  (->clojure (.ping client)))
