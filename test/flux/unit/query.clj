(ns flux.unit.query
  (:require [flux.query :refer :all]
            [midje.sweet :refer :all]))

(fact "create-query-request"
  (create-query-request {:q "*:*"}) => anything)

(fact "create-query-request w/path"
  (create-query-request "/docs" {:q "*:*"}) => anything)

(fact "create-query-request w/path and method"
  (create-query-request :post "/docs" {:q "*:*"}) => anything)

(fact "format-filter-queries"
  (let [fqs {:country "IT" :range ["1" "6"]}]
    (format-filter-queries fqs) => ["country:IT" "range:[1 TO 6]"]))
