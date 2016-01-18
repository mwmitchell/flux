(ns flux.collections
  (:require [flux.client :as client]
            [flux.query :as q]))

(defn all-replicas
  [connection collection]
  (let [request (q/create-query-request :get "/admin/collections"
                                        {:action "clusterstatus" :collection collection})
        response (client/request connection request)
        shards (get-in response [:cluster :collections (keyword collection) "shards"])]
    (into {} (map (fn [[_ v]] (get v "replicas")) shards))))

(defn active?
  "Returns true if replica is in active state"
  [[_ {:strs [state]}]]
  (= "active" state))

(defn recovering?
  "Returns true if replica is in recovery state"
  [[_ {:strs [state]}]]
  (= "recovery" state))

(defn down?
  "Returns true if replica is in down state"
  [[_ {:strs [state]}]]
  (= "down" state))

(defn not-active?
  "Returns true if replica is not in active state"
  [replica]
  (not (active? replica)))

(defn node-name
  "Returns the node name of the replica"
  [[_ {:strs [node_name]}]]
  node_name)

(letfn [ ;; Returns the solr node name for given host/port
        (solr-node-name [host-port]
          (str host-port "_solr"))]
  
  (defn hosted-by?
    "Returns true if given replica is hosted locally by given host/port"
    [replica host-port]
    (= (node-name replica) (solr-node-name host-port))))

(defn leader?
  "Returns true if given replica is a leader"
  [[_ {:keys [leader]}]]
  (= "true" leader))

(letfn [(try-all-replicas [connection collection]
          (try
            (all-replicas connection collection)
            ;; TODO: catch specific exc.. not throwable
            (catch Throwable _ {})))]
  
  (defn wait-until-active
    "Waits (infinitely) until all solr replicas hosted by given host/port belonging to given collection are in active state"
    [connection collection host-port]
    (loop [replicas (try-all-replicas connection collection)]
      (when-not (->> replicas
                     (filter active?)
                     (filter #(hosted-by? %1 host-port))
                     (seq))
        ;; TODO: allow optional timeout val
        (Thread/sleep 1000)
        (recur (try-all-replicas connection collection))))))

(defn create-collection
  "Create a SolrCloud collection"
  ([connection collection-name num-shards]
   (create-collection connection collection-name num-shards 1 {}))
  ([connection collection-name num-shards replication-factor]
   (create-collection connection collection-name num-shards replication-factor {}))
  ([connection collection-name num-shards replication-factor params]
   (let [with-params (assoc params
                            "action" "create"
                            "name" collection-name
                            "numShards" num-shards
                            "replicationFactor" replication-factor)]
     (client/request
      connection
      (q/create-query-request :get "/admin/collections" with-params)))))

(defn delete-collection
  "Delete a SolrCloud collection"
  [connection collection-name]
  (let [with-params {"action" "delete" "name" collection-name}]
    (client/request
     connection
     (q/create-query-request :get "/admin/collections" with-params))))
