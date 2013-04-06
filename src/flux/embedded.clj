(ns flux.embedded
  (import [java.io File]
          [org.apache.solr.client.solrj.embedded EmbeddedSolrServer]
          [org.apache.solr.core CoreContainer]))

(defn create-core-container
  ([solr-home-path solr-config-path]
     (CoreContainer. solr-home-path (File. solr-config-path))))

(defn create [core-container core-name]
  (EmbeddedSolrServer. core-container core-name))
