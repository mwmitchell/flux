(ns src.flux.converter
  (:require [flux.update :as update]
            [flux.converter :refer :all]
            [midje.sweet :refer :all]))

(fact "create-map-from-document"
      (let [doc (update/create-doc {:id 1})]
        (->clojure doc) => {:id 1})
      (->clojure "2.2") => 2.2
      (->clojure "2") => 2
      (->clojure "2,2") => "2,2"
      (->clojure "false") => false
      (->clojure "true") => true)
