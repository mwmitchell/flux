(ns star.core
  (require [star.client :as client]
           [ring.util.codec :as codec])
  (import [org.apache.solr.client.solrj SolrRequest]))

(def ^:dynamic *client*)


(defmacro with-client [client & body]
  (binding [*client* client]
    ~@body))

(defn build-url [path params]
  (if (seq params)
    (str path "?" (codec/form-encode params))
    path))

(defn request [path params method]
  (.request *client* (SolrRequest. method (build-url path params))))

(defn add [docs & {:as opts}])

(defn commit [& {:as opts}])

(defn delete-by-id [ids & {:as opts}])

(defn delete-by-query [query & {:as opts}])

(defn optimize [& {:as opts}])

(defn query [q & {:as opts}])

(defn rollback [])

(defn shutdown [])

(defn ping [])
