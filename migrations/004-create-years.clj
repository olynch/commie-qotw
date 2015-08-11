{:up [(j/create-table-ddl
        :years
        [:start "timestamp with time zone"]
        [:end "timestamp with time zone"]
        [:winner_id :int])]
 :down [(j/drop-table-ddl :years)]}
