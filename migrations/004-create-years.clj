{:up [(j/create-table-ddl
        :years
        [:start_t "timestamp with time zone"]
        [:end_t "timestamp with time zone"]
        [:winner_id :int])]
 :down [(j/drop-table-ddl :years)]}
