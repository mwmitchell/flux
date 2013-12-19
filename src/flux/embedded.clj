(ns flux.embedded
  (import [java.io File]
          [org.apache.solr.client.solrj.embedded EmbeddedSolrServer]
          [org.apache.solr.core CoreContainer]))

(defn create-core-container
  "Creates a CoreContainer from a solr-home path and solr-config.xml path
   or just a solr-home path.
   If the later is used, $home/$solr.xml works as well as the new
   core.properties discovery mode.
   See docs on org.apache.solr.core.CoreLocator"
  ([^String solr-home]
     (let [cc (CoreContainer. solr-home)]
       (doseq [cd (.. cc (getCoresLocator) (discover cc))]
         (.create cc cd))
       cc))
  ([^String solr-home-path ^String solr-config-path]
     (CoreContainer/createAndLoad solr-home-path (File. solr-config-path))))

(defn create [core-container core-name]
  (EmbeddedSolrServer. core-container core-name))
