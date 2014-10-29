(ns flux.collections
  (:require [flux.core :as core]
            [flux.cloud :as cloud]
            [flux.query :as q]))

(defn solr-node-name
  "Returns the solr node name for given host/port"
  [host-port]
  (str host-port "_solr"))

(defn all-replicas
  [connection collection]
  (try
    (core/with-connection connection
                          (let [response (core/request
                                           (q/create-query-request :get "/admin/collections"
                                                                   {:action "clusterstatus" :collection collection}))
                                shards (get-in response [:cluster :collections (keyword collection) "shards"])]
                            (into {}
                                  (map (fn [[k v]] (get v "replicas")) shards))))
    (catch Exception e {})))

(defn active?
  "Returns true if replica is in active state"
  {:static true}
  [replica]
  (= "active" (get-in (second replica) ["state"])))

(defn recovering?
  "Returns true if replica is in recovery state"
  {:static true}
  [replica]
  (= "recovery" (get-in (second replica) ["state"])))

(defn down?
  "Returns true if replica is in down state"
  {:static true}
  [replica]
  (= "down" (get-in (second replica) ["state"])))

(defn not-active?
  "Returns true if replica is not in active state"
  {:static true}
  [replica]
  (not (active? replica)))

(defn hosted-by?
  "Returns true if given replica is hosted locally by given host/port"
  {:static true}
  [replica host-port]
  (= (solr-node-name host-port) (get-in (second replica) ["node_name"])))

(defn leader?
  "Returns true if given replica is a leader"
  {:static true}
  [replica]
  (= "true" (get-in (second replica) ["leader"])))

(defn wait-until-active
  "Waits (infinitely) until all solr replicas hosted by given host/port belonging to given collection are in active state"
  [connection collection host-port]
  (loop []
    (let [replicas (all-replicas connection collection)]
      (when (or
              (empty? replicas)
              (some
                not-active?
                (filter
                  (fn [x] (hosted-by? x host-port))
                  (all-replicas connection collection))))
        (Thread/sleep 1000)
        (recur)))))

(defn create-collection
  "Create a SolrCloud collection"
  ([connection collection-name num-shards] (create-collection connection collection-name num-shards 1 {}))
  ([connection collection-name num-shards replication-factor] (create-collection connection collection-name num-shards replication-factor {}))
  ([connection collection-name num-shards replication-factor params]
   (core/with-connection connection (let [with-params (assoc params "action" "create"
                                                                    "name" collection-name
                                                                    "numShards" num-shards
                                                                    "replicationFactor" replication-factor)]
                                      (core/request
                                        (q/create-query-request :get "/admin/collections" with-params))))))

(defn delete-collection
  "Delete a SolrCloud collection"
  [connection collection-name]
  (core/with-connection connection (let [with-params {"action" "delete"
                                                      "name"   collection-name}]
                                     (core/request
                                       (q/create-query-request :get "/admin/collections" with-params)))))
