(ns flux.unit.response
  (:import [org.apache.solr.common.util NamedList])
  (:require [flux.update :as update]
            [flux.response :refer :all]
            [midje.sweet :refer :all]))

(fact "create-map-from-document"
  (let [doc (update/create-doc {:id 1})]
    (->clojure doc) => {:id 1}))

(fact "Convert named list"
  (let [nl (NamedList. (into-array {"a" 1 "b" 2}))]
    (->clojure nl) => {:a 1 :b 2}))
