(ns star.core
  (require [star [client :as client]
            [response :as response]]
           [clojure.string :as string]
           [ring.util.codec :as codec])
  (import [org.apache.solr.client.solrj SolrRequest]
          [org.apache.solr.common SolrInputDocument]
          [org.apache.solr.common.params MultiMapSolrParams]
          [org.apache.solr.client.solrj SolrServer SolrQuery]))
