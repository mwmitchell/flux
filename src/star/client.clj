(ns star.client
  (import [java.io File]
          [org.apache.solr.client.solrj.impl HttpSolrServer]
          [org.apache.solr.client.solrj.embedded EmbeddedSolrServer]
          [org.apache.solr.core CoreContainer]))

(defmulti create-client :type)

(defn create-core-container [solr-home-path solr-config-path]
  (CoreContainer. solr-home-path
                  (File. solr-config-path)))

(defn create-embedded-client [core-container & [core-name]]
  (if core-name
    (EmbeddedSolrServer. core-container core-name)
    (EmbeddedSolrServer. core-container)))

(defn create-http-client [base-url]
  (HttpSolrServer. base-url))

(defmethod create-client :embedded [base-config]
  {:pre [(:core-container base-config) (:core-name base-config)]}
  (create-embedded-client (:core-container base-config) (:core-name base-config)))

(letfn [(create-server-url [{:keys [protocol host port path core]}]
          (str protocol "://" host ":" port path (if core (str "/" (name core)))))]
  (defmethod create-client :default [base-config]
    (let [config (merge {:protocol "http"
                         :host "127.0.0.1"
                         :port 8983
                         :path "/solr"
                         :core-name nil}
                        base-config)]
      (create-http-client (create-server-url config)))))
