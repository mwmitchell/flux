(ns flux.connections.cloud
  (:import [org.apache.solr.client.solrj.impl CloudSolrClient$Builder]))

(defn create
  "connect to a zookeeper cloud
   
   zk-hosts
   : a list of zookeepers one to connect to and rest as failover 
   minimum like `[\"localhost:2181\"]`
   
   chroot
   : the path to the root ZooKeeper node containing Solr data. 
     \"/\" when empty
   
   default-collection
   : change to a give collection on connection already, can be 
     a keyword `:flux_test` or a string `\"flux_test\"`
   "
  ([zk-hosts]
   (.build 
    (CloudSolrClient$Builder. zk-hosts (java.util.Optional/empty))))
  ([zk-hosts chroot]
   (.build 
    (CloudSolrClient$Builder. zk-hosts (java.util.Optional/of chroot))))
  ([zk-hosts chroot default-collection]
   (let [client (.build 
                 (CloudSolrClient$Builder. zk-hosts (java.util.Optional/of chroot)))]
     (.setDefaultCollection client (name default-collection))
     client)))