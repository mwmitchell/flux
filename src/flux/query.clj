(ns flux.query
  (import [org.apache.solr.common.params MultiMapSolrParams]
          [org.apache.solr.common SolrInputDocument]))

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
