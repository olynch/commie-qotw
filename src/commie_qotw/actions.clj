(ns commie-qotw.actions
  (:refer-clojure :exclude [update])
  (:require [commie-qotw.db :as db]
            [commie-qotw.auth :as auth]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.hashers :as hashers]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [ring.util.response :refer [response status content-type]]
            [clojure.set :refer [rename-keys]]
            [clojure.core.match :as m]))

(defn get-cur-week-id []
  (m/match [(-> (select :id)
                (from :weeks)
                (limit 1)
                (order-by [:start_t :desc])
                db/query)]
           [{:success true :result ([{:id cur-week-id}] :seq)}] cur-week-id
           [{:success false :error e}] (throw e)))

(defn get-cur-year-id []
  (m/match [(-> (select :id)
                (from :years)
                (limit 1)
                (order-by [:start_t :desc])
                db/query)]
           [{:success true :result ([{:id cur-year-id}] :seq)}] cur-year-id
           [{:success false :error e}] (throw e)))

(defn submit-quote-map 
  ([quotetext submitted week_id]
   (-> (insert-into :quotes)
       (columns :quote :submitted :week_id)
       (values [[quotetext (c/to-sql-time submitted) week_id]])))
  ([quotetext week_id]
   (submit-quote-map quotetext (t/now) week_id)))

; String -> {:success bool}
(defn submit-quote [quote]
  (let [cur-week-id (get-cur-week-id)
        result (db/execute! (submit-quote-map quote cur-week-id))]
    (response {:success (:success result)})))

(defn archive-range-map [start end]
  (-> (from :weeks)
      (select :message_title :id :end_t)
      (where [:<> :end_t nil])
      (where [:>= :end_t (c/to-sql-time start)] [:< :end_t (c/to-sql-time end)])
      (order-by [:end_t :desc])))

(defn archive-date-map
  ([year month]
   (let [start (t/date-time year month)
         end (t/plus start (t/months 1))]
     (archive-range-map start end)))
  ([year]
   (let [start (t/date-time year)
         end (t/plus start (t/years 1))]
     (archive-range-map start end))
  )
  ([]
   (-> (from :weeks)
       (select :message_title :id :end_t)
       (where [:<> :end_t nil])
       (order-by [:end_t :desc]))))

; Param Map -> [{:title, :id, :timestamp}]
(defn get-archives [params]
  (let [query-map (m/match [params]
                           [{:month month :year year}] (archive-date-map year month)
                           [{:year year}] (archive-date-map year)
                           :else (archive-date-map))
        result (:result (db/query query-map))]
      (response result)))

(defn get-message-map [id]
  (-> (select :message_title :message_body :id)
      (from :weeks)
      (where [:= :id id])))

(defn get-message [messageID]
  (let [{success :success result :result} (db/query (get-message-map messageID))]
    (if (= (count result) 1)
      (response (rename-keys (first result) {:message_title :title :message_body :body}))
      (response {:success false}))))

(def get-lastmessage-map
  (-> (select :id)
      (from :weeks)
      (where [:<> :end_t nil])
      (limit 1)
      (order-by [:start_t :desc])))

(defn get-lastmessage []
  (let [{result :result success :success} (db/query get-lastmessage-map)]
    (response (first result))))

(defn create-new-year-map []
  (-> (insert-into :years)
      (columns :start_t)
      (values [[(c/to-sql-time (t/now))]])))

(defn create-new-week-map []
  (-> (insert-into :weeks)
      (columns :year_id :start_t)
      (values [[(get-cur-year-id) (c/to-sql-time (t/now))]])))

(defn end-current-week-map [title body]
  (let [cur-week-id (get-cur-week-id)]
    (-> (update :weeks)
        (sset {:message_title title
               :message_body body
               :end_t (c/to-sql-time (t/now))})
        (where [:= :id cur-week-id]))))

(defn send-message [title body]
  (let [end-current-week-query (end-current-week-map title body)
        create-new-week-query (create-new-week-map)
        end-res (db/execute! end-current-week-query)
        create-res (db/execute! create-new-week-query)]
    (response {:success (every? identity (map :success [end-res create-res]))})))

(defn sign-up-map [email password]
  (-> (insert-into :users)
      (columns :email :hash)
      (values [[email (hashers/encrypt password)]])))

(defn sign-up [email password]
  (if (-> (select :email)
          (from :users)
          (where [:= :email email])
          db/query
          count
          (>= 1))
    (response (db/execute! (sign-up-map email password)))
    (response {:success false :error "There is already a user with that email"})))

(defn login [email password]
  (let [valid? (auth/check-user email password)]
    (if valid?
      (let [token (auth/random-token)
            {[{user_id :id} & r] :result} (-> (select :id)
                                              (from :users)
                                              (where [:= :email email])
                                              db/query)]
        (println (str "user_id: " user_id))
        (auth/store-token token user_id)
        (response {:token token :email email}))
      (response {:error "Invalid authentication data"}))))

(defn whoami [request]
  (if (authenticated? request)
    (response (:identity request))
    (throw-unauthorized {:message "You are not logged in"})))

(defn get-submissions-map []
  (let [cur-week-id (get-cur-week-id)]
    (-> (from :quotes)
        (select :quote)
        (where [:= :week_id cur-week-id]))))

(defn get-submissions []
  (-> (get-submissions-map)
      db/query
      :result
      response))

(defn get-admins-map []
  (-> (select :email)
      (from :users)))

(defn get-admins []
  (-> (get-admins-map)
      db/query
      :result
      response))

(defn rm-sessions-map [email]
  (-> (delete-from :sessions :users)
      (join :users [:= :sessions.user_id :users.id])
      (where [:= :users.email email])))

(defn rm-user [email]
  (-> (rm-sessions-map email)
      db/query
      response))

(defn vote [token vote1 vote2 vote3])

(defn handle-404 []
  (-> (response "Not found")
      (status 404)
      (content-type "text/plain")))

(defn initialize []
  (db/execute! (create-new-year-map))
  (db/execute! (create-new-week-map))
  (sign-up "root@root.org" "1337"))

(defn populate []
  (initialize)
  (submit-quote "You are.\n --Pher")
  (submit-quote "SOO Applied.\n --Mr. Letarte")
  (submit-quote "Let's burn his house down.\n --Yonah Bornsweil")
  (send-message "EXAMPLE MESSAGE" "This message has been brought to you by Owen \"The Programmer\" Lynch"))
