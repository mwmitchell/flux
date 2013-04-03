(ns star.core
  (require [star [client :as client]
            [response :as response]]
           [clojure.string :as string]
           [ring.util.codec :as codec])
  (import [org.apache.solr.client.solrj SolrRequest]
          [org.apache.solr.common SolrInputDocument]
          [org.apache.solr.common.params MultiMapSolrParams]
          [org.apache.solr.client.solrj SolrServer SolrQuery]))

(defn format-param [p]
  (if (keyword? p) (name p) (str p)))

(defn format-values [v]
  (into-array (mapv format-param (if (coll? v) v [v]))))

(defn- create-solr-params [m]
  (MultiMapSolrParams.
   (reduce-kv #(doto %1
                 (.put (format-param %2)
                       (format-values %3)))
              (java.util.HashMap.) m)))

;;(create-solr-params {:age ["10" "11"] :id 1 :name '("one" "two") :fred #{1}})

(.query my-client (create-solr-params {:q "*:*"}))

(def ^:dynamic *client*)

(defmacro with-client [client & body]
  `(binding [*client* ~client]
     ~@body))

(def my-client (client/create-embedded-client (client/create-core-container "../rk/apij/resources/solr" "../rk/apij/resources/solr/solr.xml") "hotels"))

(defn query [m]
  {:pre [(#{:get :post} m)]}
  (org.apache.solr.client.solrj.SolrRequest$METHOD/valueOf
   (clojure.string/upper-case (name m))))

(with-client my-client
  (request :get "/solr/hotels/select" {:q "*:*" :rows 0}))

(SolrRequest. (request-method :get) "/solr")

(defn build-url [path params]
  (if (seq params)
    (str path "?" (codec/form-encode params))
    path))

(defn request [method path params]
  (.request *client* (SolrRequest. (request-method method) (build-url path params))))

(defn- create-doc [m]
  (reduce-kv #(doto %1 (.addField (name %2) %3))
             (SolrInputDocument.) m))

(defmulti add (fn [in] (case (map? in) :one (coll? in) :many :else :many)))

(defmethod add :one
  [doc & {:as opts}]
  (.add *client* (create-doc doc)))

(defmethod add :one
  [docs & {:as opts}]
  (.add *client* (map create-doc docs)))

(defn commit [& {:as opts}]
  (.commit *client*))

(defmulti delete-by-id (fn [in] (cond (coll? in) :many :else :one)))
(defmethod delete-by-id :many [ids & {:as opts}]
  (.deleteById *client* ids))
(defmethod delete-by-id :one [id & {:as opts}]
  (.deleteById *client* id))

(defn delete-by-query [q & {:as opts}]
  (.deleteByQuery *client* q))

(defn optimize
  ([] (.optimize *client*))
  ([wait-flush wait-searcher]
     (.optimize *client* wait-flush wait-searcher))
  ([wait-flush wait-searcher max-segments]
     (.optimize *client* wait-flush wait-searcher max-segments)))

(defn query [q & {:as opts}])

(defn rollback []
  (.rollback *client*))

(defn shutdown []
  (.shutdown *client*))

(defn ping []
  (.ping *client*))
