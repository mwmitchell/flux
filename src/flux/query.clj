(ns flux.query
  (import [org.apache.solr.common.params MultiMapSolrParams]
          [org.apache.solr.client.solrj.request QueryRequest]
          [org.apache.solr.client.solrj SolrRequest$METHOD]))

(def method-map
  {:get SolrRequest$METHOD/GET
   :post SolrRequest$METHOD/POST})

(defn- format-param [p]
  (if (keyword? p) (name p) (str p)))

(defn- format-values [v]
  (into-array (mapv format-param (if (coll? v) v [v]))))

(defn- create-solr-params [m]
  (MultiMapSolrParams.
   (reduce-kv #(doto %1
                 (.put (format-param %2)
                       (format-values %3)))
              (java.util.HashMap.) m)))

(defn create-query [query options]
  (create-solr-params (assoc options :q query)))

(defn create-query-request
  ([params]
     (create-query-request params nil))
  ([params path]
     (create-query-request params path :get))
  ([params path method]
     {:pre [(or (nil? path) (re-find #"^\/" (str path)))
            (get method-map method)]}
     (doto
         (QueryRequest. (create-solr-params params) (get method-map method))
       (.setPath path))))
