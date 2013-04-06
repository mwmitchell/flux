(ns flux.unit.core
  (:use clojure.test
        flux.core
        midje.sweet)
  (:require [flux.client :as client]))

(fact "query"
  (with-connection ..connection..
    (query ..query..) => anything
    (provided (client/query ..connection.. ..query..) => ..anything..)))

(fact "delete-by-id"
  (with-connection ..connection..
    (delete-by-id ..id..) => anything
    (provided (client/delete-by-id ..connection.. ..id..) => ..anything..)))

(fact "delete-by-id"
  (with-connection ..connection..
    (delete-by-query ..query..) => anything
    (provided (client/delete-by-query ..connection.. ..query..) => ..anything..)))

(fact "commit"
  (with-connection ..connection..
    (commit) => anything
    (provided (client/commit ..connection..) => ..anything..)))

(fact "optimize"
  (with-connection ..connection..
    (optimize) => anything
    (provided (client/optimize ..connection..) => ..anything..)))

(fact "add"
  (with-connection ..connection..
    (add ..doc..) => anything
    (provided (client/add ..connection.. ..doc..) => ..anything..)))

(fact "rollback"
  (with-connection ..connection..
    (rollback) => anything
    (provided (client/rollback ..connection..) => ..anything..)))

(fact "shutdown"
  (with-connection ..connection..
    (shutdown) => anything
    (provided (client/shutdown ..connection..) => ..anything..)))
