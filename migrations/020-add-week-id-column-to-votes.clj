{:up ["ALTER TABLE votes ADD COLUMN week_id integer REFERENCES weeks (id);"]
 :down ["ALTER TABLE votes DROP COLUMN week_id;"]}
