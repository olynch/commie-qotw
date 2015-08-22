(ns commie-qotw.core-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [ring.mock.request :as mock]
            [commie-qotw.core :refer :all]))

(defn json-request [uri json-map]
  (-> (request :post uri)
      (body (json/write-string json-map))
      (content-type "application/json")))

(defn get-response [app uri json-map]
  (let [response (app (json-request uri json-map))]
    (json/read-str (:body response) :key-fn keyword)))

(defn expected-response [app uri json-map expected-response]
  (let [ret-json (get-response app uri json-map)]
    (= ret-json expected-response)))

(deftest test-get-message
  (testing "Sending message and then retrieving it"
    (is (expected-response app
                           "/api/send-message"
                           {:title "inaugural message"
                            :body "qotw.net is dead\nlong live qotw.net"}
                           {:success true}))
    (is (let [{last-msg-id :id} (get-response app "/api/lastmessage" {})]
          (expected-response app
                           "/api/message"
                           {:messageID last-msg-id}
                           {:title "inaugural message"
                            :body "qotw.net is dead\nlong live qotw.net"}))))
