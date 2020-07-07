(ns src.flux.response
  (:require [flux.update :as update]
            [flux.response :refer :all]
            [midje.sweet :refer :all]))

(fact "create-map-from-document"
  (let [doc (update/create-doc {:id 1})]
    (->clojure doc) => {:id 1}))
