(ns flux.unit.response
  (:require [flux.update :as update])
  (:use clojure.test
        flux.response
        midje.sweet))

(fact "create-map-from-document"
  (let [doc (update/create-doc {:id 1})]
    (create-map-from-document doc) => {:id 1}))
