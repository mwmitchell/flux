(ns flux.http
  (:import (org.apache.solr.client.solrj.impl HttpSolrClient)))

(defn create [base-url core-name]
  (HttpSolrClient. (str base-url "/" (name core-name))))
