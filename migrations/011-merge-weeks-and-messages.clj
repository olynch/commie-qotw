{:up ["ALTER TABLE weeks ADD COLUMN message_title text, ADD COLUMN message_body text;
      DROP TABLE messages"]
 :down ["ALTER TABLE weeks DROP COLUMN message_title, DROP COLUMN message_body;
        CREATE TABLE messages (
        title text,
        body text,
        id serial PRIMARY KEY,
        week_id int"]}
