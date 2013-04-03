(ns star.embedded
  (import [java.io File]
          [org.apache.solr.client.solrj.embedded EmbeddedSolrServer]
          [org.apache.solr.core CoreContainer]))

(defn create-core-container
  ([solr-home-path]
     (CoreContainer. solr-home-path))
  ([solr-home-path solr-config-path]
     (CoreContainer. solr-home-path (File. solr-config-path))))

;; (create-core-container "/Users/goodieboy/Downloads/apache-solr-4.0.0/example/solr"
;;                        "/Users/goodieboy/Downloads/apache-solr-4.0.0/example/solr/solr.xml")

(defn create-embedded [core-container core-name]
  (EmbeddedSolrServer. core-container core-name))
