(ns flux.cloud
  (import [org.apache.solr.client.solrj.impl CloudSolrServer]))

(defn create
  ([zk-hosts]
   (CloudSolrServer. zk-hosts))
  ([zk-hosts default-collection]
   (let [server (CloudSolrServer. zk-hosts)]
     (.setDefaultCollection server default-collection)
     server)))