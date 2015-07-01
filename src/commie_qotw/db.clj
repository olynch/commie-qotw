(ns commie-qotw.db
  (:require [honeysql.core :as sql]
            [honeysql.helpers :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [commie-qotw.cfg :as cfg]))

; Messages table keys:
; title : string
; body : string
; id : int
; weekid : int

; Quotes table keys:
; quote : string
; id : int
; timestamp : timestamp with time zone
; votes : int
; awards : int (0 = none, 1 = of week, 2 = of year)
; weekid : int

; Weeks table keys:
; start : timestamp with time zone
; end : timestamp with time zone
; yearid : int
; winner : int

; Years table keys:
; start : timestamp with time zone
; end : timestamp with time zone
; winnerid : int

; Admins table keys:
; username : string
; password_hash : string
; password_salt : string
; id : int

; Sessions table keys:
; key : string
; id : int
; userid : int
; expires : timestamp

(defn pool [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               (.setMaxIdleTimeExcessConnections (* 30 60))
               (.setMaxIdleItme (* 3 60 60)))]
    {:datasource cpds}))

(def pooled-db (delay (pool cfg/db-spec)))

(defn db-connection [] @pooled-db)

;  { :success bool :result [] }
(defn run [query]
  {:mock true})
