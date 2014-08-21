(ns flux.query
  (:require [flux.conversion :as conv]
            [flux.request.query :as q])
  (:import [org.apache.solr.common.params MultiMapSolrParams]))

(defn query [connection & args]
  (let [args2 (juxt identity q/resolve-method q/create-params)])
  (conv/->clojure
   (.request
    connection
    (q/create-query-request path (q/resolve-request-method method) (q/create-params params)))))
