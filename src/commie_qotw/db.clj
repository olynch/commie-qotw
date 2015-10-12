(ns commie-qotw.db
  (:import [com.mchange.v2.c3p0 ComboPooledDataSource]
           [java.io File])
  (:refer-clojure :exclude [update])
  (:require [honeysql.core :as sql]
            [honeysql.helpers :refer :all]
            [clojure.java.jdbc :as j]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [commie-qotw.cfg :as cfg]
            [ragtime.repl]
            [ragtime.jdbc]))

; Quotes table keys:
; quote : string
; id : serial
; submitted : timestamp
; votes : int
; awards : int (0 = none, 1 = selected for email, 2 = of week, 3 = of year)
; weekid : int

; Weeks table keys:
; id : serial
; start_t : timestamp
; end_t : timestamp
; message_title : text
; message_body : text
; year_id : int
; winner : int

; Years table keys:
; id : serial
; start_t : timestamp
; end_t : timestamp
; winner_id : int

; Users table keys:
; email : varchar(256)
; password_hash : varchar(256)
; id : serial

; Sessions table keys:
; token : string
; id : serial
; user_id : int
; expires : timestamp

(defn pool [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) "://" (:host spec) ":" (:port spec) "/" (:db-name spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               (.setMaxIdleTimeExcessConnections (* 30 60))
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(def pooled-db (delay (pool cfg/db-spec)))

(defn db-connection [] @pooled-db)

(let [pattern (re-pattern (str "([^" File/separator "]*)" File/separator "?$"))]
 (defn- basename [file]
  (second (re-find pattern (str file)))))

(defn- remove-extension [file]
 (second (re-matches #"(.*)\.[^.]*" (str file))))

(defmethod ragtime.jdbc/load-files ".clj" [files]
  (for [file files]
    (-> (slurp file)
        (read-string)
        (#(list 'do '(require '[clojure.java.jdbc :as j]) %))
        (eval)
        (update-in [:id] #(or % (-> file basename remove-extension)))
        (ragtime.jdbc/sql-migration))))

(def ragtime-config 
  (delay
    {:datastore (ragtime.jdbc/sql-database (db-connection))
     :migrations (ragtime.jdbc/load-directory "migrations")}))

(defn load-ragtime-config [] @ragtime-config)

(defn migrate []
  (ragtime.repl/migrate (load-ragtime-config)))

(defn rollback []
  (ragtime.repl/rollback (load-ragtime-config)))

; In goes a HoneySQL query map, out goes the result
(defn query [query-map]
  (->> query-map
       sql/format
       (j/query (db-connection))))

(defn execute! [query-map]
  (let [[rows-changed] (->> query-map
                            sql/format
                            (j/execute! (db-connection)))]
    rows-changed))

