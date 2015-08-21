{:up [(j/create-table-ddl :users
                          [:email "varchar(256)"]
                          [:hash "varchar(256)"]
                          [:id :serial "PRIMARY KEY"])]
 :down [(j/drop-table-ddl :users)]}
