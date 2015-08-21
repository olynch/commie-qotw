{:up [(j/create-table-ddl :sessions
                          [:token "varchar(256)"]
                          [:id :serial "PRIMARY KEY"]
                          [:user_id :int]
                          [:expires :timestamp])]
 :down [(j/drop-table-ddl :sessions)]}
