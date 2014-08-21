(ns flux.request.query
  (:import [org.apache.solr.client.solrj StreamingResponseCallback]
           [org.apache.solr.client.solrj.request QueryRequest]
           [org.apache.solr.client.solrj SolrRequest$METHOD]
           [org.apache.solr.common.params MultiMapSolrParams]))

(defn- format-param [p]
  (if (keyword? p) (name p) (str p)))

(defn- format-values [v]
  (into-array (mapv format-param (if (coll? v) v [v]))))

(defn create-params [m]
  (MultiMapSolrParams.
   (reduce-kv #(doto %1
                 (.put (format-param %2)
                       (format-values %3)))
              (java.util.HashMap.) m)))

(def ^:private method-map
  {:get SolrRequest$METHOD/GET
   :post SolrRequest$METHOD/POST})

(defn resolve-request-method [kw]
  (get method-map kw))

(defn create-streaming-callback [a b]
  (proxy
      [StreamingResponseCallback] []
    (streamDocListInfo [& args] (apply a args))
    (streamSolrDocument [& args] (apply b args))))

(defn create-query-request
  ([params]
     (create-query-request nil params))
  ([path params]
     (create-query-request :get path params))
  ([method path params]
     {:pre [(or (nil? path) (re-find #"^\/" (str path)))
            (resolve-request-method method)]}
     (doto
         (QueryRequest. params (resolve-request-method method))
       (.setPath path))))
