{:up [(j/create-table-ddl :subscribers
                          [:id :serial "PRIMARY KEY"]
                          [:email "varchar(256)"])]
 :down [(j/drop-table-ddl :subscribers)]}
