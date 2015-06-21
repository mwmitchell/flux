(ns flux.test.core
  (:require [clojure.test :refer :all]
            [flux.http :as http]
            [flux.core :refer :all]))



;;;;
;;;; General test functions
;;;;

(def conn (http/create "http://localhost:8983/solr" :flux-tests))


(def book-list [{:title_t     "Rainbows End"
                 :author_s    "Vernor Vinge"
                 :available_b true}
                {:title_t     "A Fire Upon the Deep"
                 :author_s    "Vernor Vinge"
                 :available_b true}
                {:title_t     "A Deepness in the Sky"
                 :author_s    "Vernor Vinge"
                 :available_b true}
                {:title_t     "Use of Weapons"
                 :author_s    "Iain M. Banks"
                 :available_b true}
                {:title_t     "War Made New: Weapons, Warriors, and the Making of the Modern World"
                 :author_s    "Max Boot"
                 :available_b false}
                {:title_t     "The player of games"
                 :author_s    "Iain M. Banks"
                 :available_b true}
                {:title_t     "James Bond: Choice of Weapons"
                 :author_s    "Raymond Benson"
                 :available_b true}
                {:title_t     "The Black Company"
                 :author_s    "Glen Cook"
                 :available_b true}
                {:title_t     "A Cruel Wind"
                 :author_s    "Glen Cook"
                 :available_b true}
                {:title_t     "Reap the East Wind"
                 :author_s    "Glen Cook"
                 :available_b true}
                {:title_t     "The Silver Spike"
                 :author_s    "Glen Cook"
                 :available_b false}
                {:title_t     "Black Sands"
                 :author_s    "Blair Reynolds"
                 :available_b false}
                ])

(defn wipe-test-data []
  (with-connection conn
                   (delete-by-query "*:*")
                   (commit)))

(defn random-uuid []
  (.toString (java.util.UUID/randomUUID)))


(defn range-docs
  "Returns a list of n test documents using an increasing number for their tags"
  [n]
  (map #(hash-map :id (random-uuid) :title_t (str "Document " %) :tags_ss [(str "tag" %)] :internal_i %) (range n)))



(defn docs-from-titles
  "Returns a list of test documents from a list of titles, using an increasing index for their tags"
  [titles]
  (map-indexed #(hash-map :id (random-uuid) :title_t %2 :internal_i %1) titles))


;;;;
;;;; Tests
;;;;


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
        (are [title tags] (not-empty (filter #(and (= [title] (:title_t %))
                                                   (= tags (:tags_ss %)))
                                             docs))
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
                                               :tags_ss ["independent" "new2"]}])
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
        (are [title tags] (not-empty (filter #(and (= [title] (:title_t %)) ; See above about text_general fields
                                                   (= tags (:tags_ss %)))
                                             docs))
                          "New 1" ["independent" "new1"]
                          "New 2" ["independent" "new2"]
                          "New 3" ["independent" "new3"]))
      ))
  )


(deftest basic-querying-and-sorting
  (wipe-test-data)
  (with-connection conn
                   (add (range-docs 10))
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


(deftest basic-text-querying
  (wipe-test-data)
  (with-connection conn
                   (add (docs-from-titles ["First piece"
                                           "Second book"
                                           "Last and Least important"
                                           "Third piece"
                                           "First after third"
                                           "Third after that"
                                           "Last books"
                                           "Last but definitely not least"
                                           "Final"
                                           "Last but not least"]))
                   (commit))
  (testing "Querying is not case sensitive"
    (let [result (with-connection conn (query "title_t:first"))
          docs   (get-in result [:response :docs])]
      (is docs)
      (is (= 0 (get-in result [:responseHeader :status])))
      (is (= 2 (get-in result [:response :numFound])))
      (is (= 2 (count docs)))
      (is (= ["First piece" "First after third"] (map #(first (:title_t %)) docs)))))
  (testing "Querying for a singular does not return a plural"
    (let [result (with-connection conn (query "title_t:book"))
          docs   (get-in result [:response :docs])]
      (is docs)
      (is (= 0 (get-in result [:responseHeader :status])))
      (is (= 1 (get-in result [:response :numFound])))
      (is (= 1 (count docs)))
      (is (= ["Second book"] (map #(first (:title_t %)) docs)))))
  (testing "We can do wildcard matching"
    (let [result (with-connection conn (query "title_t:book*"))
          docs   (get-in result [:response :docs])]
      (is docs)
      (is (= 0 (get-in result [:responseHeader :status])))
      (is (= 2 (get-in result [:response :numFound])))
      (is (= 2 (count docs)))
      (is (= ["Second book" "Last books"] (map #(first (:title_t %)) docs)))))
  (testing "If we pass two words with single quotes, they're matched independently"
    ;; The query could be passed as 'after third' or 'after+third'
    (let [result (with-connection conn (query "title_t:'after third'"))
          docs   (get-in result [:response :docs])]
      (is docs)
      (is (= 0 (get-in result [:responseHeader :status])))
      (is (= 3 (get-in result [:response :numFound])))
      (is (= 3 (count docs)))
      ;; Next assertion sorts them because they won't necessarily be returned in the same order they were inserted
      (is (= ["First after third" "Third after that" "Third piece"] (sort (map #(first (:title_t %)) docs))))))
  (testing "Double quotes are exact matches"
    (let [result (with-connection conn (query "title_t:\"after third\""))
          docs   (get-in result [:response :docs])]
      (is docs)
      (is (= 0 (get-in result [:responseHeader :status])))
      (is (= 1 (get-in result [:response :numFound])))
      (is (= ["First after third"] (map #(first (:title_t %)) docs)))))
  (testing "Proximity searches"
    ;; Notice we use double quotes, otherwise it appears that the proximity is ignored
    ;; We skip any titles where 'last' and 'least' appear more than two words away
    ;; (there' a total of four with those words)
    (let [result (with-connection conn (query "title_t:\"last least\"~2"))
          docs   (get-in result [:response :docs])]
      (is (= 2 (count docs)))
      (is (= ["Last and Least important" "Last but not least"] (map #(first (:title_t %)) docs)))))
  (testing "We can do exact matches with proximity searching"
    (let [result (with-connection conn (query "title_t:\"after third\"~0"))
          docs   (get-in result [:response :docs])]
      (is (= ["First after third"] (map #(first (:title_t %)) docs)))))
  (testing "Word transpositions are returned with proximity searches"
    (let [result (with-connection conn (query "title_t:\"after third\"~2"))
          docs   (get-in result [:response :docs])]
      (is (= ["First after third" "Third after that"] (map #(first (:title_t %)) docs)))))

  )


(deftest query-on-multiple-fields
  (wipe-test-data)
  (with-connection conn
                   (add book-list)
                   (commit))
  (testing "String fields only do exact matches"
    (let [result (with-connection conn (query "author_s:Iain"))]
      (is result)
      (is (= 0 (get-in result [:response :numFound])))
      (is (= 0 (get-in result [:responseHeader :status])))
      )
    ;; For the next query, single or double quotes are interchangeable
    (let [result (with-connection conn (query "author_s:'Iain M. Banks'"))]
      (is result)
      (is (= 2 (get-in result [:response :numFound])))
      (is (= 0 (get-in result [:responseHeader :status])))
      ))
  (testing "Querying for title only returns matches regardless of the author"
    (let [result (with-connection conn (query "title_t:black"))
          docs   (get-in result [:response :docs])]
      (is result)
      (is (= 2 (get-in result [:response :numFound])))
      (is (= 0 (get-in result [:responseHeader :status])))
      (are [title author] (not-empty (filter #(and (= [title] (:title_t %))
                                                   (= author (:author_s %)))
                                             docs))
                          "The Black Company" "Glen Cook"
                          "Black Sands" "Blair Reynolds")))
  (testing "Querying for title and author returns only that exact match"
    ;; Few peculiarities to be aware of here:
    ;; - If the AND is lowercase it seems to be ignored, or act as an OR
    ;; - If the name is in single quotes, no results will be returned
    ;; This is pretty much crying out for a query builder
    (let [result (with-connection conn (query "title_t:black AND author_s:\"Glen Cook\""))
          docs   (get-in result [:response :docs])]
      (is result)
      (is (= 1 (get-in result [:response :numFound])))
      (is (= 0 (get-in result [:responseHeader :status])))
      (are [title author] (not-empty (filter #(and (= [title] (:title_t %))
                                                   (= author (:author_s %)))
                                             docs))
                          "The Black Company" "Glen Cook")))
  (testing "We can query for a title OR an author"
    (let [result (with-connection conn (query "title_t:black OR author_s:\"Glen Cook\""))
          docs   (get-in result [:response :docs])]
      (is result)
      (is (= 5 (get-in result [:response :numFound])))
      (is (= 0 (get-in result [:responseHeader :status])))
      (are [title author] (not-empty (filter #(and (= [title] (:title_t %))
                                                   (= author (:author_s %)))
                                             docs))
                          "The Black Company" "Glen Cook"
                          "Black Sands" "Blair Reynolds"
                          "A Cruel Wind" "Glen Cook"
                          "Reap the East Wind" "Glen Cook"
                          "The Silver Spike" "Glen Cook"
                          )))
  (testing "We can query for multiple fields, including booleans"
    (let [result (with-connection conn (query "(title_t:black OR author_s:\"Glen Cook\") AND available_b:true"))
          docs   (get-in result [:response :docs])]
      (is (= 3 (get-in result [:response :numFound])))
      (is (= 0 (get-in result [:responseHeader :status])))
      (are [title author] (not-empty (filter #(and (= [title] (:title_t %))
                                                   (= author (:author_s %)))
                                             docs))
                          "The Black Company" "Glen Cook"
                          "A Cruel Wind" "Glen Cook"
                          "Reap the East Wind" "Glen Cook"
                          )))
  (testing "We can exclude words"
    (let [result (with-connection conn (query "title_t:weapons AND -title_t:bond AND available_b:true"))
          docs   (get-in result [:response :docs])]
      (is result)
      (is (= 1 (get-in result [:response :numFound])))
      (is (= 0 (get-in result [:responseHeader :status])))
      (are [title author] (not-empty (filter #(and (= [title] (:title_t %))
                                                   (= author (:author_s %)))
                                             docs))
                          "Use of Weapons" "Iain M. Banks")))
  )


(deftest query-ranges
  (wipe-test-data)
  (with-connection conn
                   (add (range-docs 31))
                   (commit))
  ;; Querying from * to N gets all x such that x <= N
  (let [result (with-connection conn (query "internal_i:[* TO 5]"))
        docs   (get-in result [:response :docs])]
    (is (= 6 (count docs)))
    (doseq [i (range 6)]
      (is (not-empty (filter #(= i (:internal_i %)) docs)))))
  ;; Querying from N to M gets all x such that N <= x <= M
  (let [result (with-connection conn (query "internal_i:[9 TO 14]"))
        docs   (get-in result [:response :docs])]
    (is (= 6 (count docs)))
    (doseq [i (range 9 15)]
      (is (not-empty (filter #(= i (:internal_i %)) docs)))))
  ;; Querying from N to * returns all x where N <= x, but caps it at 10 rows by default
  (let [result (with-connection conn (query "internal_i:[15 TO *]"))
        docs   (get-in result [:response :docs])]
    (is (= 10 (count docs)))
    (doseq [i (range 15 24)]
      (is (not-empty (filter #(= i (:internal_i %)) docs)))))
  ;; Querying from N to * returns all x where N <= x
  (let [result (with-connection conn (query "internal_i:[15 TO *]" {:rows 100}))
        docs   (get-in result [:response :docs])]
    (is (= 16 (count docs)))
    (doseq [i (range 15 31)]
      (is (not-empty (filter #(= i (:internal_i %)) docs)))))
  )


(deftest test-delete-by-id
  (wipe-test-data)
  (let [initial-docs (range-docs 10)]
    (with-connection conn
                     (add initial-docs)
                     (commit))
    ;; Confirm we created them all
    (let [result (with-connection conn (query "*:*"))]
      (is (= 10 (count (get-in result [:response :docs])))))
    ;; Let's remove number 3
    (let [to-delete  (nth initial-docs 2)
          del-result (with-connection conn
                                      (delete-by-id [(:id to-delete)])
                                      (commit))
          get-result (with-connection conn (query "*:*"))
          docs       (get-in get-result [:response :docs])]
      (is del-result)
      (is get-result)
      (is (= 9 (count docs)))
      (is (empty? (filter #(= (:id to-delete) (:id %)) docs))))
    ;; Let's remove a range
    (let [to-delete  (take 3 initial-docs)
          del-result (with-connection conn
                                      (delete-by-id (map :id to-delete))
                                      (commit))
          get-result (with-connection conn (query "*:*"))
          docs       (get-in get-result [:response :docs])]
      (is del-result)
      (is get-result)
      ;; Verify we still have 7 - we deleted only 2, because the third one was already gone
      (is (= 7 (count docs)))
      ;; The deleted documents are not returned
      (doseq [i to-delete]
        (is (empty? (filter #(= (:id i) (:id %)) docs))))
      ;; All other documents are there
      (doseq [i (nthrest initial-docs 3)]
        (is (not-empty (filter #(= (:id i) (:id %)) docs)))))
    ))


(deftest test-boosting
  (wipe-test-data)
  (with-connection conn
                   (add book-list)
                   (commit))
  (let [result-title  (with-connection conn (query "title_t:black^1.5 OR author_s:\"Glen Cook\""))
        docs-title    (get-in result-title [:response :docs])
        result-author (with-connection conn (query "title_t:black OR author_s:\"Glen Cook\"^2"))
        docs-author   (get-in result-author [:response :docs])
        ]
    (is (= 5 (count docs-title)))
    (is (= 5 (count docs-author)))
    ;; The lists are not returned on the same order because of different score boosting
    (is (not= docs-title docs-author))
    ;; The last item on the author-boosted list is Blair Reynolds, because Glen Cook was boosted
    (is (= "Blair Reynolds" (:author_s (last docs-author))))
    ;; The last item on the title-boosted list is Glen Cook, because "black" was boosted as a search term
    (is (= "Glen Cook" (:author_s (last docs-title))))
    )
  )