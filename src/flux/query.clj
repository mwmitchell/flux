(ns flux.query
  (:import [org.apache.solr.common.params MultiMapSolrParams]
           [org.apache.solr.client.solrj.request QueryRequest]
           [org.apache.solr.client.solrj SolrRequest$METHOD]))

(def method-map
  {:get SolrRequest$METHOD/GET
   :post SolrRequest$METHOD/POST})

(defn- format-param [p]
  (if (keyword? p) (name p) (str p)))

(defn- format-values [v]
  (into-array (mapv format-param (if (coll? v) v [v]))))

(defn- format-range [coll]
  (str "[" (format-param (first coll)) " TO " (format-param (second coll)) "]"))

(defn- format-fq [[k v]]
  (str (format-param k) ":" (if (coll? v) (format-range v) (format-param v))))

(defn format-filter-queries [hm]
  (mapv format-fq hm))

(defn- create-solr-params [m]
  (MultiMapSolrParams.
   (reduce-kv (fn [^java.util.HashMap hm k v]
                (doto hm
                  (.put (format-param k)
                        (format-values v))))
              (java.util.HashMap.) m)))

(defn create-query [query options]
  (let [filter-queries (format-filter-queries (:fq options))]
    (create-solr-params (assoc (assoc options :fq filter-queries) :q query))))

(defn create-query-request
  ([params]
     (create-query-request nil params))
  ([path params]
     (create-query-request :get path params))
  ([method path params]
     {:pre [(or (nil? path) (re-find #"^\/" (str path)))
            (get method-map method)]}
     (doto
         (QueryRequest. (create-solr-params params) (get method-map method))
       (.setPath path))))
