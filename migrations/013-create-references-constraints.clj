{:up ["ALTER TABLE weeks
      ADD FOREIGN KEY (year_id) REFERENCES years (id);
      ALTER TABLE quotes
      ADD FOREIGN KEY (week_id) REFERENCES weeks (id)"]
 :down ["ALTER TABLE weeks
        DROP FOREIGN KEY year_id;
        ALTER TABLE quotes
        DROP FOREIGN KEY week_id"]}
