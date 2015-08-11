{:up [(j/create-table-ddl
        :weeks
        [:start "timestamp with time zone"]
        [:end "timestamp with time zone"]
        [:year_id :int]
        [:winner_id :int])]
 :down [(j/drop-table-ddl :weeks)]}
