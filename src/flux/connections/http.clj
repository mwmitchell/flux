(ns flux.connections.http
  (:import [org.apache.solr.client.solrj.impl HttpSolrClient$Builder]))

(defn create 
  "Connect to solr by url and core-name
   
   base-url 
   : string like `\"http://localhost:8983/solr\"`
  
   core-name
   : the core to connect to like `:flux_test` or `\"flux_test\"`"
  [base-url core-name]
  (.build 
   (HttpSolrClient$Builder. (str base-url "/" (name core-name)))))
