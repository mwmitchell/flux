(ns flux.request.update
  (:import [org.apache.solr.client.solrj.request UpdateRequest]
           [org.apache.solr.common SolrInputDocument]))

(defn create-doc [document-map]
  (reduce-kv #(if (map? %3)
                (let [m (doto (java.util.HashMap.)
                          (.put (name (key (first %3))) (val (first %3))))]
                  (doto %1 (.addField (name %2) m))
                  %1)
                (doto %1 (.addField (name %2) %3)))
             (SolrInputDocument.) document-map))

(defn update []
  (UpdateRequest.))

(defn add [update docs]
  (.add update docs))

(defn delete-by-id [update id]
  (.deleteById update id))

(defn delete-by-query [update query]
  (.deleteByQuery update query))

(defn get-xml [update]
  (.getXML update))
