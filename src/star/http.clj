(ns star.http
  (import [org.apache.solr.client.solrj.impl HttpSolrServer]))

(defn create [base-url core-name]
  (HttpSolrServer. (str base-url "/" (name core-name))))
