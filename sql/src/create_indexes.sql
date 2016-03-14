/*CREATE OR REPLACE FUNCTION insert_index() RETURNS void AS 
$new_index$
BEGIN
	IF NOT EXISTS (
	    SELECT 1 
	    FROM   pg_catalog.pg_class c
	    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
	    WHERE  n.nspname = 'msgTimestamp'
	    AND    c.relname = 'MESSAGE'
	    ) THEN
	    CREATE INDEX msgTimestamp ON MESSAGE USING BTREE(msg_timestamp);
	END IF;
END  $new_index$ LANGUAGE plpgsql;
SELECT insert_index();
DROP FUNCTION insert_index();*/

CREATE INDEX msgTimestamp ON MESSAGE USING BTREE(msg_timestamp);