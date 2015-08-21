{:up ["ALTER TABLE quotes
      ALTER COLUMN week_id SET NOT NULL,
      ALTER COLUMN quote SET NOT NULL,
      ALTER COLUMN votes SET NOT NULL,
      ALTER COLUMN awards SET NOT NULL,
      ALTER COLUMN submitted SET NOT NULL;
      ALTER TABLE weeks
      ALTER COLUMN start_t SET NOT NULL,
      ALTER COLUMN year_id SET NOT NULL"]
 :down ["ALTER TABLE quotes
        ALTER COLUMN week_id SET NULL,
        ALTER COLUMN quote SET NULL,
        ALTER COLUMN votes SET NULL,
        ALTER COLUMN awards SET NULL,
        ALTER COLUMN submitted SET NULL;
        ALTER TABLE weeks
        ALTER COLUMN start_t SET NULL,
        ALTER COLUMN year_id SET NULL"]}
