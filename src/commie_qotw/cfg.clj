(ns commie-qotw.cfg
  (:require [environ.core :refer [env]]))

(def db-spec
  {:classname "org.postgresql.Driver"
   :subprotocol "postgressql"
   :subname (env :database-url)
   :username (env :database-username)
   :password (env :database-password)})
