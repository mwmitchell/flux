(ns flux.embedded
  (import [java.io File]
          [org.apache.solr.client.solrj.embedded EmbeddedSolrServer]
          [org.apache.solr.core CoreContainer]
          [java.nio.file Paths]
          [java.net URI]))

(defn- str->path [str-path]
  (-> str-path File. .toURI Paths/get))

(defn create-core-container
  "Creates a CoreContainer from a solr-home path and solr-config.xml path
   OR just a solr-home path.
   If the latter is used, $home/$solr.xml works well, as the new
   core.properties discovery mode.
   See: org.apache.solr.core.CoreLocator
        and
        http://wiki.apache.org/solr/Core%20Discovery%20(4.4%20and%20beyond)
   Note: If using core.properties only, it is required to call (.load core-container)
         before creating the EmbeddedSolrServer instance."
  ([^String solr-home]
   (CoreContainer. solr-home))
  ([^String solr-home-path ^String solr-config-path]
   (CoreContainer/createAndLoad
    (str->path solr-home-path)
    (str->path solr-config-path))))

(defn create [^CoreContainer core-container core-name]
  {:pre [(some #(% core-name) [string? keyword?])]}
  (EmbeddedSolrServer. core-container (name core-name)))
