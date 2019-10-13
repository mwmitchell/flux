(ns flux.cloud
  (:import [org.apache.solr.client.solrj.impl CloudSolrClient$Builder]))

(defn create
  ([zk-hosts]
   (-> (CloudSolrClient$Builder. zk-hosts)
       (.build)))
  ([zk-hosts default-collection]
   (let [client (create zk-hosts)]
     (.setDefaultCollection client default-collection)
     client)))