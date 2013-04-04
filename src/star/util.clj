(ns star.util
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

(defn create-doc [document-map]
  (reduce-kv #(if (map? %3)
                (let [m (doto (java.util.HashMap.)
                          (.put (name (key (first %3))) (val (first %3))))]
                  (doto %1 (.addField (name %2) m))
                  %1)
                (doto %1 (.addField (name %2) %3)))
             (SolrInputDocument.) document-map))

(defn create-query [query options]
  (create-solr-params (assoc options :q query)))
