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
        ALTER COLUMN week_id DROP NOT NULL,
        ALTER COLUMN quote DROP NOT NULL,
        ALTER COLUMN votes DROP NOT NULL,
        ALTER COLUMN awards DROP NOT NULL,
        ALTER COLUMN submitted DROP NOT NULL;
        ALTER TABLE weeks
        ALTER COLUMN start_t DROP NOT NULL,
        ALTER COLUMN year_id DROP NOT NULL"]}
