(ns flux.connections.cloud
  (:import [org.apache.solr.client.solrj.impl CloudSolrClient$Builder]))

(defn create
  ([zk-hosts]
   (.build 
    (CloudSolrClient$Builder. zk-hosts (java.util.Optional/empty))))
  ([zk-hosts chroot]
   (.build 
    (CloudSolrClient$Builder. zk-hosts (java.util.Optional/of chroot))))
  ([zk-hosts chroot default-collection]
   (let [client (-> (CloudSolrClient$Builder. zk-hosts (java.util.Optional/of chroot))       
                    (.build))]
     (. client setDefaultCollection (name default-collection))
     client)))