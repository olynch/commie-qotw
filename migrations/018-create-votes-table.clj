{:up [(j/create-table-ddl :votes
                          [:token "varchar(16)"]
                          [:vote1 :int "REFERENCES quotes (id)"]
                          [:vote2 :int "REFERENCES quotes (id)"]
                          [:vote3 :int "REFERENCES quotes (id)"]
                          [:id :serial "PRIMARY KEY"])]
 :down [(j/drop-table-ddl :votes)]}
