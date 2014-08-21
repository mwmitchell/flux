(ns flux.request.ping
  (:import [org.apache.solr.client.solrj.request SolrPing]))

(defn ping []
  (SolrPing.))
