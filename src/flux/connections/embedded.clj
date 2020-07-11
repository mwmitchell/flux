(ns flux.connections.embedded
  (:import [java.io File]
           [org.apache.solr.client.solrj.embedded EmbeddedSolrServer]
           [org.apache.solr.core CoreContainer]
           [java.nio.file Paths]))

(defn- str->path [str-path]
  (-> str-path File. .toURI Paths/get))

(defn create-core-container
  "Creates a CoreContainer from a default path (solr folder beside .jar?), 
   by a solr-home path or by solr-home path and solr-config.xml path.
   
   solr-home-path
   : string to solr home
   
   solr-config-path
   : If the latter is used, $home/$solr.xml works well, as the new
     core.properties discovery mode.
   
   : See: org.apache.solr.core.CoreLocator
          and
          http://wiki.apache.org/solr/Core%20Discovery%20(4.4%20and%20beyond)
     Note: If using core.properties only, it is required to call (.load core-container)
           before creating the EmbeddedSolrServer instance.
   "
  ([]
   (CoreContainer.))
  ([^String solr-home-path]
   (CoreContainer/createAndLoad
    (str->path solr-home-path)))
  ([^String solr-home-path ^String solr-config-path]
   (CoreContainer/createAndLoad
    (str->path solr-home-path)
    (str->path solr-config-path))))

(defn create 
  "Connects to embedded server.
   
   core-container
   : created by create-core-container to use
   
   core-name
   : change to a give collection on connection already, can be 
     a keyword `:flux_test` or a string `\"flux_test\"`"
  [^CoreContainer core-container core-name]
  {:pre [(some #(% core-name) [string? keyword?])]}
  (EmbeddedSolrServer. core-container (name core-name)))

(defn create-core 
  "Creates a new core.
   
   core-container
   : created by create-core-container to use
   
   core-name
   : change to a give collection on connection already, can be 
     a keyword `:flux_test` or a string `\"flux_test\"`"
  [core-container core-name]
  (.create core-container (name core-name) {}))