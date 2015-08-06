(ns commie-qotw.cfg
  (:require [environ.core :refer [env]]))

(def db-spec
  {:classname "org.postgresql.Driver"
   :subprotocol "postgres"
   :subname (env :database-path)
   ;:username (env :database-username)
   ;:password (env :database-password)
   })
