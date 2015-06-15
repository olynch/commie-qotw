(ns commie-qotw.db
  (:require [honeysql.core :as sql]
            [honeysql.helpers :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

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

; returns a map of { :success bool :result [] }
(defn run [query]
  {:mock true})
