(ns flux.test.core
  (:require [clojure.test :refer :all]
            [flux.http :as http]
            [flux.core :refer :all]))



;
; General test functions
;

(def conn (http/create "http://localhost:8983/solr" :flux-tests))


(defn wipe-test-data []
  (with-connection conn
                   (delete-by-query "*:*")
                   (commit)))

(defn random-uuid []
  (java.util.UUID/randomUUID))


(defn get-test-docs
  "Returns a list of n test documents using an increasing number for their tags"
  [n]
  (map #(hash-map :id (random-uuid) :title_t (str "Document " %) :tags_ss [(str "tag" %)] :internal_i %) (range n)))


;
; Tests
;


(deftest add-document
  (wipe-test-data)
  (testing "Can add a single document"
    (let [add-result   (with-connection conn
                                        (add {:id         (random-uuid)
                                              :title_t    "A test document"
                                              :created_dt (java.util.Date.)
                                              :author_s   "ricardo"
                                              :tags_ss    ["tag1" "tag2" "tag3"]})
                                        (commit))
          query-result (with-connection conn
                                        (query "*:*"))]
      (is add-result)
      (is (= 0 (get-in add-result [:responseHeader :status])))
      (is query-result)
      (are [id value] (= value (get-in query-result [:response id]))
                      :numFound 1
                      :start 0)
      (is (= 0 (get-in query-result [:responseHeader :status])))
      ; Check the documents
      (let [docs (get-in query-result [:response :docs])]
        (is (= 1 (count docs)))
        (is (= ["tag1" "tag2" "tag3"] (:tags_ss (first docs))))
        (is (= "ricardo" (:author_s (first docs))))                   ; string fields are returned as strings
        (is (= ["A test document"] (:title_t (first docs))))          ; text_general fields are returned as lists
        )
      )))


(deftest add-multiple-documents
  (wipe-test-data)
  (testing "Can add multiple documents with a single add call"
    (let [add-result   (with-connection conn
                                        (add [{:id         (random-uuid)
                                               :title_t    "A second document"
                                               :created_dt (java.util.Date.)
                                               :tags_ss    ["tag2" "tag4"]}
                                              {:id         (random-uuid)
                                               :title_t    "And a third document"
                                               :created_dt (java.util.Date.)
                                               :tags_ss    ["tag3"]}
                                              {:id         (random-uuid)
                                               :title_t    "tagless"
                                               :created_dt (java.util.Date.)}])
                                        (commit))
          query-result (with-connection conn
                                        (query "*:*"))]
      (is add-result)
      (is (= 0 (get-in add-result [:responseHeader :status])))
      ; Check the documents
      (let [docs (get-in query-result [:response :docs])]
        (is (= 3 (count docs)))
        (are [title tags] (filter #(and (= [title] (:title_t %))
                                        (= tags (:tags_ss %)))
                                  docs)
                          "A second document" ["tag2" "tag4"]
                          "And a third document" ["tag3"]
                          "tagless" nil))
      ))
  (testing "Can add multiple documents with multiple calls and a single commit"
    (let [add-result   (with-connection conn
                                        (add [{:id      (random-uuid)
                                               :title_t "New 1"
                                               :tags_ss ["independent" "new1"]}])
                                        (add [{:id      (random-uuid)
                                               :title_t "New 2"
                                               :tags    ["independent" "new2"]}])
                                        (add [{:id_ss   (random-uuid)
                                               :title_t "New 3"
                                               :tags_ss ["independent" "new3"]}])
                                        (commit))
          query-result (with-connection conn
                                        (query "*:*"))]
      (is add-result)
      (is (= 0 (get-in add-result [:responseHeader :status])))
      ; Check the documents
      (let [docs (get-in query-result [:response :docs])]
        (is (= 6 (count docs)))
        (are [title tags] (filter #(and (= [title] (:title_t %))      ; See above about text_general fields
                                        (= tags (:tags_ss %)))
                                  docs)
                          "New 1" ["independent" "new1"]
                          "New 2" ["independent" "new2"]
                          "New 3" ["independent" "new3"]))
      ))
  )

(deftest basic-querying-and-sorting
  (wipe-test-data)
  (with-connection conn
                   (add (get-test-docs 10))
                   (commit))
  (testing "Querying for all returns them in insert order"
    (let [result (with-connection conn (query "*:*"))
          docs   (get-in result [:response :docs])]
      (is (= 10 (count docs)))
      (doseq [i (range 10)]
        (is (= i (:internal_i (nth docs i))))
        (is (= [(str "tag" i)] (:tags_ss (nth docs i)))))))
  (testing "We can request them in descending order"
    (let [result (with-connection conn (query "*:*" {:sort "internal_i desc"}))
          docs   (get-in result [:response :docs])]
      (is (= 10 (count docs)))
      (doseq [i (reverse (range 10))]
        (is (= i (:internal_i (nth docs (- 9 i))))))))
  (testing "Cannot sort by a text_general field"
    (is (thrown? Throwable (with-connection conn (query "*:*" {:sort "title_t desc"})))))
  (testing "Querying for a number of rows"
    (let [result (with-connection conn (query "*:*" {:rows 6}))
          docs   (get-in result [:response :docs])]
      (is (= 10 (get-in result [:response :numFound])))
      (is (= 0 (get-in result [:response :start])))
      (is (= 6 (count docs)))
      (doseq [i (range 6)]
        (is (= [(str "tag" i)] (:tags_ss (nth docs i)))))))
  (testing "Querying for a starting point"
    (let [result (with-connection conn (query "*:*" {:start 3}))
          docs   (get-in result [:response :docs])]
      (is (= 10 (get-in result [:response :numFound])))
      (is (= 3 (get-in result [:response :start])))
      (is (= 7 (count docs)))
      (doseq [i (range 7)]
        (is (= [(str "tag" (+ i 3))] (:tags_ss (nth docs i)))))))
  (testing "Querying for a starting point and a number of rows"
    (let [result (with-connection conn (query "*:*" {:start 4 :rows 3}))
          docs   (get-in result [:response :docs])]
      (is (= 10 (get-in result [:response :numFound])))
      (is (= 4 (get-in result [:response :start])))
      (is (= 3 (count docs)))
      (doseq [i (range 3)]
        (is (= [(str "tag" (+ i 4))] (:tags_ss (nth docs i)))))))
  )



