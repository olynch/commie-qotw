{:up [(j/create-table-ddl
        :weeks
        [:start_t "timestamp with time zone"]
        [:end_t "timestamp with time zone"]
        [:year_id :int]
        [:winner_id :int])]
 :down [(j/drop-table-ddl :weeks)]}
