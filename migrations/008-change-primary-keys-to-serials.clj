{:up ["ALTER TABLE quotes DROP COLUMN id, ADD COLUMN id SERIAL PRIMARY KEY;
      ALTER TABLE weeks ADD COLUMN id SERIAL PRIMARY KEY;
      ALTER TABLE years ADD COLUMN id SERIAL PRIMARY KEY;
      ALTER TABLE messages DROP COLUMN id, ADD COLUMN id SERIAL PRIMARY KEY"]
 :down ["ALTER TABLE quotes DROP COLUMN id, ADD COLUMN id int PRIMARY KEY;
        ALTER TABLE weeks DROP COLUMN id;
        ALTER TABLE years DROP COLUMN id;
        ALTER TABLE messages DROP COLUMN id, ADD COLUMN id int PRIMARY KEY"]}