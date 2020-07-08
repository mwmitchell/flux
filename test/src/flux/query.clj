(ns src.flux.query
  (:require [flux.query :refer :all]
            [midje.sweet :refer :all]))

(fact "create-query-request"
  (create-query-request {:q "*:*"}) => anything)

(fact "create-query-request w/path"
  (create-query-request "/docs" {:q "*:*"}) => anything)

(fact "create-query-request w/path and method"
  (create-query-request :post "/docs" {:q "*:*"}) => anything)
