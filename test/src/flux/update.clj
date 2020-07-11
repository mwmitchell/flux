(ns src.flux.update
  (:require [flux.update :refer :all]
            [flux.converter :refer [->clojure]]
            [midje.sweet :refer :all])
  (:import [org.apache.solr.common SolrInputDocument]))

(fact "create-doc"
      (class (create-doc {:id 1})) => org.apache.solr.common.SolrInputDocument
      (.toString
       (create-doc {:id 1 :map {:int 2 :string "abc"}})) => "SolrInputDocument(fields: [id=1, map={string=abc, int=2}])")


(fact "create-doc-with-children"
      (let [doc {:id 1
                 :__childDocuments
                 [{:id "1.1"
                   :title "a title..."
                   :__childDocuments
                   [{:id "1.1.1"}]}]}
            solr-doc (create-doc doc)]
        (.hasChildDocuments solr-doc) => true
        (-> solr-doc (.getChildDocuments) first (.hasChildDocuments)) => true
        (->clojure (create-doc {:id 1 :__childDocuments [{:id 2} {:id 3} {:id 4}]}))
        => {:id 1 :__childDocuments '({:id 2} {:id 3} {:id 4})}))