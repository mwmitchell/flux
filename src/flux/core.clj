(ns flux.core
  (require [flux.client]))

(def ^:dynamic *connection*)

(defmacro with-connection [connection & body]
  `(binding [*connection* ~connection]
     ~@body))

(defmacro create-fn [name]
  `(defn ~name [& args#]
     (apply (ns-resolve 'flux.client '~name) *connection* args#)))

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
