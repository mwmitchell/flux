(ns star.http
  (import [org.apache.solr.client.solrj.impl HttpSolrServer]))

(letfn [(create-server-url [{:keys [protocol host port path core]}]
          (str protocol "://" host ":" port path (if core (str "/" (name core)))))]
  (defn create [base-config]
    (let [config (merge {:protocol "http"
                         :host "127.0.0.1"
                         :port 8983
                         :path "/solr"
                         :core-name nil}
                        base-config)]
      (HttpSolrServer.  (create-server-url config)))))
