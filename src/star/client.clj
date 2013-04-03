(defn star.client)

;;to switch cores via http, the base path must change
;;to switch cores via EmbeddedSolrServer, the core-name must be accessed directly

(defmulti add (fn [in] (case (map? in) :one (coll? in) :many :else :many)))

(defmethod add :one
  [client doc & {:as opts}]
  (.add client (create-doc doc)))

(defmethod add :one
  [client docs & {:as opts}]
  (.add client (map create-doc docs)))

(defn commit [client & {:as opts}]
  (.commit client))

(defmulti delete-by-id (fn [in] (cond (coll? in) :many :else :one)))
(defmethod delete-by-id :many [client ids & {:as opts}]
  (.deleteById client ids))
(defmethod delete-by-id :one [client id & {:as opts}]
  (.deleteById client id))

(defn delete-by-query [client q & {:as opts}]
  (.deleteByQuery client q))

(defn optimize
  ([client] (.optimize client))
  ([client wait-flush wait-searcher]
     (.optimize client wait-flush wait-searcher))
  ([client wait-flush wait-searcher max-segments]
     (.optimize client wait-flush wait-searcher max-segments)))

(defn query [q & {:as opts}])

(defn rollback [client]
  (.rollback client))

(defn shutdown [client]
  (.shutdown client))

(defn ping [client]
  (.ping client))

