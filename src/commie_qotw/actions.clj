(ns commie-qotw.core
  (:require [commie-qotw.db :as db]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [core.match :refer :all]))

(def submitquotemap
  (-> (insert-into :quotes)
      (columns :quote :timestamp)))

(defn submitquotesql [quotetext timestamp]
  (-> 
    submitquotemap
    (values [quotetext (c/to-long timestamp)])
    sql/format
    ))

(submitquotesql "You are. --Pher" (t/now))

(def archiverangemap
  (-> (from :messages)
      (select :title :id :timestamp)))

(defn archiverangesql [start end]
  (-> archiverangemap
      (where :datetime [:>= :timestamp (c/to-long start)] [:< :datetime (c/to-long end)])
      sql/format))

(defn archivesql 
  ([year month]
   (let [start (t/date-time year month)
         end (t/plus start (t/months 1))]
     (archiverange (c/to-long start) (c/to-long end))))
  ([year]
   (let [start (t/date-time year)
         end (t/plus start (t/years 1))]
     (archiverange (c/to-long start) (c/to-long end)))
  )
  ([]
   (-> (from :messages)
       (select :title :id :timestamp)
       sql/format)))

; String -> {:success bool}
(defn submit-quote [quote]
  (let [result (db/run (submitquotesql quote))]
    {:success (:success result)}))

; Param Map -> [{title, id, timestamp}]
(defn get-archives [params]
    (let [sql (match [params]
                     [{:month month :year year}] (archivesql year month)
                     [{:year year}] (archivesql year)
                     :else (archivesql))
          result (db.run sql)]
      (map (fn [v]
            (match v
                   [title id timestamp] {:title title :id id :timestamp timestamp}))
           result)))
