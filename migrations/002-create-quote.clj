{:up [(j/create-table-ddl
        :quotes
        [:quote :text]
        [:id :int :primary :key]
        [:timestamp "timestamp with time zone"]
        [:votes :int]
        [:awards :int]
        [:week_id :int])]
 :down [(j/drop-table-ddl :quotes)]}
