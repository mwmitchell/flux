(ns flux.query
  (:import [org.apache.solr.common.params MultiMapSolrParams]
           [org.apache.solr.client.solrj.request QueryRequest]
           [org.apache.solr.client.solrj SolrRequest$METHOD]))

(def method-map
  "maps SolrRequest$METHODs to :get and :post"
  {:get SolrRequest$METHOD/GET
   :post SolrRequest$METHOD/POST})

(defn- format-param 
  "Helper to convert input into strings
   
   p 
   : input to convert into string"
  [p]
  (if (keyword? p) (name p) (str p)))

(defn- format-values 
  "maps format-param on all values
   
   v
   : values to convert into strings"
  [v]
  (into-array (mapv format-param (if (coll? v) v [v]))))

(defn- format-range 
  "helper that allows the use of [1 3] range searches
   will convert it into \"[1 TO 3]\"
   
   coll
   : vector with [start end]"
  [coll]
  (str "[" (format-param (first coll)) " TO " (format-param (second coll)) "]"))

(defn- format-fq 
  "Format helper to convert filter queries of :whatever \"val\" 
   into whatver:val
   
   [k v]
   : mapped key and filter expr or collection (range [start end])"
  [[k v]]
  (str (format-param k) ":" (if (coll? v) (format-range v) (format-param v))))

(defn format-filter-queries 
  "converts input into filter query for solr
   
   hm
   : fq map of conditions"
  [hm]
  (mapv format-fq hm))

(defn- create-solr-params 
  "converts the filterexpressionmap into MutliMapSolrParams
   
   m 
   : map of filter conditions"
  [m]
  (MultiMapSolrParams.
   (reduce-kv (fn [^java.util.HashMap hm k v]
                (doto hm
                  (.put (format-param k)
                        (format-values v))))
              (java.util.HashMap.) m)))

(defn create-query 
  "extracts the :fq option (filter query) and adds it to solr query
   
   query
   : the normal query of solr like `*:*` added to :q 
   
   options 
   : searches for :fq and converts it into filter conditions"
  [query options]
  (let [filter-queries (format-filter-queries (:fq options))
        options (if (empty? filter-queries)
                  (dissoc options :fq)
                  (assoc :fq filter-queries))]    
    (create-solr-params (assoc options :q query))))

(defn create-query-request
  "creates a solr Request of given args
   
   method 
   : [Optional ctor3] :get or :post be used to query
   
   path
   : [Optional ctor2+3] nil or path/directory to query against?
   
   params
   : parameters at least {:q \"etc\"}"
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
