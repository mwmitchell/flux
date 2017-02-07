(ns flux.cloud
  :import (org.apache.solr.client.solrj.impl CloudSolrClient))

(defn create
  ([zk-hosts]
   (CloudSolrClient. zk-hosts))
  ([zk-hosts default-collection]
   (let [server (CloudSolrClient. zk-hosts)]
     (.setDefaultCollection server default-collection)
     server)))
