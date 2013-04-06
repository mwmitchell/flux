(ns electrojet.client
  (require [electrojet.util :refer [create-doc create-query]]
           [electrojet.response :as response]))

(defn query [solr-server query & [options]]
  (response/response-base (.query solr-server (create-query query options))))

(defmulti add
  (fn [_ input & _]
    (cond
     (map? input) :one
     :else :default)))

(defmethod add :one [client doc & {:as opts}]
  (response/update-response
   (.add client (create-doc doc))))

(defmethod add :default [client docs & {:as opts}]
  (response/update-response
   (.add client (map create-doc docs))))

(defn commit [client & {:as opts}]
  (response/update-response (.commit client)))

(letfn [(v [x]
          (cond (keyword? x) (name x) :else (str x)))]
  (defn delete-by-id [client ids & {:as opts}]
    (response/update-response
     (.deleteById client (if (coll? ids) (map v ids) (v ids))))))

(defn delete-by-query [client q & {:as opts}]
  (response/update-response
   (.deleteByQuery client q)))

(defn optimize
  ([client]
     (response/update-response
      (.optimize client)))
  ([client wait-flush wait-searcher]
     (response/update-response
      (.optimize client wait-flush wait-searcher)))
  ([client wait-flush wait-searcher max-segments]
     (response/update-response
      (.optimize client wait-flush wait-searcher max-segments))))

(defn rollback [client]
  (response/update-response
   (.rollback client)))

(defn shutdown [client]
  (.shutdown client))

(defn ping [client]
  (.ping client))
