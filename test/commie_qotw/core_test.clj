(ns commie-qotw.core-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [ragtime.repl :as ragtime]
            [ring.mock.request :as mock]
            [commie-qotw.core :refer :all]
            [commie-qotw.db :as db]
            [commie-qotw.actions :as a]))

(defn wrap-fresh-db [f]
                                        ; assumes a fresh db (can have ragtime table)
  (ragtime/migrate (db/load-ragtime-config))
  (a/initialize)
  (f)
  (ragtime/rollback (db/load-ragtime-config) "001-create-messages")
  (ragtime/rollback (db/load-ragtime-config) 1)
                                        ; leaves with a fresh db (plus ragtime table)
  )

(use-fixtures :once wrap-fresh-db)

(defn POST-request-json [uri json-map]
  (-> (mock/request :post uri)
      (mock/body (json/write-str json-map))
      (mock/content-type "application/json")))

(defn GET-request [uri]
  (mock/request :get uri))

(defn POST-response [app uri json-map]
  (let [response (app (POST-request-json uri json-map))]
    (println response)
    (json/read-str (:body response) :key-fn keyword)))

(defn GET-response [app uri]
  (let [response (app (GET-request uri))]
    (json/read-str (:body response) :key-fn keyword)))

(deftest test-get-message
  (testing "Sending message and then retrieving it"
    (is (= (POST-response app
                          "/api/send-message"
                          {:title "inaugural message"
                           :body "qotw.net is dead\nlong live qotw.net"})
           {:success true}))
    (let [{last-msg-id :id} (GET-response app "/api/lastmessage")]
      (is (= (POST-response app
                            "/api/message"
                            {:messageID last-msg-id})
             {:id last-msg-id
              :title "inaugural message"
              :body "qotw.net is dead\nlong live qotw.net"})))))

(deftest test-login
  (testing "Logging in as test admin"
    (let [response (POST-response app
                                  "/admin/login"
                                  {:email "root@root.org"
                                   :password "1337"})]
      (is (:success response))
      (let [token (:token response)]
        (is (= (POST-response app
                              "/api/whoami"
                              {:token token})
               ))))))

(deftest test-submit-quotes
  (testing "submitting quotes and then retrieving them"
    (is (= (POST-response app
                          "/api/submit"
                          {:quote "You are.\n--Pher"})
           {:success true}))
    (is (= (POST-response app
                          "/api/submit"
                          {:quote "Trivial.\n--Thomas D."})
           {:success true}))
    (is (= ()))))
