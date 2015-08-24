(ns commie-qotw.auth
  (:refer-clojure :exclude [update])
  (:require [buddy.core.nonce :as nonce]
            [buddy.core.codecs :as codecs]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.hashers :as hashers]
            [honeysql.helpers :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.core.match :as m]
            [commie-qotw.db :as db]
            [buddy.auth.protocols :as proto]
            [buddy.auth.http :as http]
            [ring.util.response :refer [response]]))

(defn random-token []
  (let [random-data (nonce/random-bytes 16)]
    (codecs/bytes->hex random-data)))

(defn store-token-map [token user_id]
  (-> (insert-into :sessions)
      (columns :token :user_id :expires)
      (values [[token user_id (c/to-sql-time (t/plus (t/now) (t/weeks 1)))]])))

(defn store-token [token user_id]
  (m/match [(db/execute! (store-token-map token user_id))]
           [{:success true}] nil
           [{:success false :error e}] (throw e)))

(defn check-token-map [token]
  (-> (from :sessions)
      (join :users [:= :sessions.user_id :users.id])
      (select :users.email :users.id)
      (where [:= :sessions.token token] [:> :sessions.expires (c/to-sql-time (t/now))])))

(defn check-token [token]
  (m/match [(db/query (check-token-map token))]
           [{:success true :result ([result & rest] :seq)}]
           result
           :else nil))

(defn check-user [email password]
  (let [{result :result} (-> (select :hash)
                             (from :users)
                             (where [:= :email email])
                             db/query)]
    (if (= (count result) 1)
      (hashers/check password (:hash (first result)))
      false)))

(defn- handle-unauthorized-default [request]
  (if (:identity request)
    {:status 403 :headers {} :body "Permission denied"}
    {:status 401 :headers {} :body "Unauthorized"}))

(defn json-token-backend
  [{:keys [authfn unauthorized-handler token-name] :or {token-name :token}}]
  {:pre [(fn? authfn)]}
  (reify
    proto/IAuthentication
    (parse [_ request]
      (get-in request [:params :token]))
    (authenticate  [_ request token]
      (let [rsq (authfn token)]
        (if (http/response? rsq)
          rsq
          (assoc request :identity rsq))))

    proto/IAuthorization
    (handle-unauthorized [_ request metadata]
      (if unauthorized-handler
        (unauthorized-handler request metadata)
        (handle-unauthorized-default request)))))

(def auth-backend
  (json-token-backend {:authfn check-token}))
