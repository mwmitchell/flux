(ns flux.core
  (:require [flux.client]))

;; This will be used to store a default connection to query against
(defonce _default (atom nil))

(def ^:dynamic *connection* nil)

(defn set-default-connection
  "Set the solr connection that flux should use by default when no
  alternative is specified."
  [conn]
  (reset! _default conn))

(defmacro with-connection [connection & body]
  `(binding [*connection* ~connection]
     ~@body))

(defmacro create-fn [name]
  `(defn ~name [& args#]
     (apply (ns-resolve 'flux.client '~name) (or *connection* @_default) args#)))

(create-fn ping)
(create-fn add)
(create-fn query)
(create-fn request)
(create-fn commit)
(create-fn optimize)
(create-fn rollback)
(create-fn delete-by-id)
(create-fn delete-by-query)
(create-fn shutdown)
