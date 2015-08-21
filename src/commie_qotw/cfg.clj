(ns commie-qotw.cfg
  (:require [environ.core :refer [env]]))

(def db-spec
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :host (env :database-host)
   :port (env :database-port)
   :db-name (env :database-db-name)
   :user (env :database-username)
   :password (env :database-password)
   })
