{:up [(j/create-table-ddl
       :messages
       [:title :text]
       [:body :text]
       [:id :int :primary :key]
       [:week_id :int])]
 :down [(j/drop-table-ddl :messages)]}
