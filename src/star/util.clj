(ns star.util)



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


(defn request-method [m]
  {:pre [(#{:get :post} m)]}
  (org.apache.solr.client.solrj.SolrRequest$METHOD/valueOf
   (clojure.string/upper-case (name m))))


(defn build-url [path params]
  (if (seq params)
    (str path "?" (codec/form-encode params))
    path))

(defn- create-doc [m]
  (reduce-kv #(doto %1 (.addField (name %2) %3))
             (SolrInputDocument.) m))
