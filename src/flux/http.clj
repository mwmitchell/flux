(ns flux.http
  (:import [org.apache.solr.client.solrj.impl HttpSolrClient$Builder]))

(defn create [base-url core-name]
  (-> (HttpSolrClient$Builder. (str base-url "/" (name core-name)))
      (.build)))
