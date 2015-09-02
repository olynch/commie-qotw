{:up ["ALTER TABLE weeks
      ADD FOREIGN KEY (year_id) REFERENCES years (id);
      ALTER TABLE quotes
      ADD FOREIGN KEY (week_id) REFERENCES weeks (id)"]
 :down ["ALTER TABLE weeks
        DROP CONSTRAINT weeks_year_id_fkey;
        ALTER TABLE quotes
        DROP CONSTRAINT quotes_week_id_fkey"]}
