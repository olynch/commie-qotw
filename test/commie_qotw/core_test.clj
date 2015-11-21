(ns commie-qotw.core-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [ragtime.repl :as ragtime]
            [ring.mock.request :as mock]
            [commie-qotw.core :refer :all]
            [commie-qotw.db :as db]
            [commie-qotw.actions :as a]))

(defn wrap-migrations [f]
  ;; assumes a fresh db (can have ragtime table)
  (ragtime/migrate (db/load-ragtime-config))
  (a/initialize)
  (try  (f)
        (finally
          (ragtime/rollback (db/load-ragtime-config) "001-create-messages")
          (ragtime/rollback (db/load-ragtime-config) 1)))
  ;; leaves with a fresh db (plus ragtime table)
  )

(use-fixtures :each wrap-migrations)

(defn POST-request-json [uri json-map]
  (-> (mock/request :post uri)
      (mock/body (json/write-str json-map))
      (mock/content-type "application/json")))

(defn GET-request [uri]
  (mock/request :get uri))

(defn POST-response [app uri json-map]
  (let [response (app (POST-request-json uri json-map))
        body (:body response)]
    (json/read-str body :key-fn keyword)))

(defn GET-response [app uri]
  (let [response (app (GET-request uri))]
    (json/read-str (:body response) :key-fn keyword)))

(defn get-auth-token [app]
  (:token (POST-response app "/api/login" {:email "root@root.org" :password "1337"})))

(deftest test-get-message
  (testing "Sending message and then retrieving it"
    (let [token (get-auth-token app)]
      (is (= (POST-response app
                            "/admin/send-message"
                            {:title "inaugural message"
                             :body "qotw.net is dead\nlong live qotw.net"
                             :token token})
             {:success true})))
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
                                  "/api/login"
                                  {:email "root@root.org"
                                   :password "1337"})]
      (is (:success response))
      (let [token (:token response)]
        (is (= (:email (POST-response app
                                      "/api/whoami"
                                      {:token token}))
               "root@root.org"))))))

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
    (let [token (get-auth-token app)]
      (is (= (map :quote (POST-response app
                                        "/admin/submissions"
                                        {:token token}))
             ["You are.\n--Pher" "Trivial.\n--Thomas D."])))))

(deftest test-subscriptions
  (testing "adding subscribers and then removing them"
    (is (= (POST-response app
                          "/api/subscribe"
                          {:email "scrub@root.org"})
           {:success true}))
    (let [token (get-auth-token app)]
      (is (= (POST-response app
                           "/admin/subscriptions"
                           {:token token})
             [{:email "scrub@root.org"}]))
      (is (= (POST-response app
                            "/api/unsubscribe"
                            {:email "scrub@root.org"})
             {:success true}))
      (is (= (POST-response app
                           "/admin/subscriptions"
                           {:token token})
             [])))))

(deftest test-voting
  (testing "add subscriber, send out message, vote using token, look at votes"
    (is (= (POST-response app
                          "/api/subscribe"
                          {:email "scrub@root.org"})
           {:success true}))
    (is (= (POST-response app
                          "/api/submit"
                          {:quote "Your MOTHER is so applied!\n--Mr. Letarte"})
           {:success true}))
    (let [token (get-auth-token app)
          send-msg-result (POST-response app
                                         "/admin/send-message"
                                         {:title "Quotopotamus"
                                          :body "Now we care about your feedback!"
                                          :token token})]
      (is (:success send-msg-result))
      (let [[{quote-id :id} & r] (POST-response app
                                                "/admin/submissions"
                                                {:token token})]
        (is (= (POST-response app
                              "/api/vote"
                              {:token (first (:tokens send-msg-result))
                               :votes [quote-id quote-id quote-id]})
               {:success true}))
        (is (= (POST-response app
                              "/admin/votes"
                              {:token token})
               {:votes [[quote-id quote-id quote-id]]}))))))
