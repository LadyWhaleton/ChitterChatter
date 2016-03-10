mkdir /tmp/$USER
mkdir /tmp/$USER/data
cd /tmp/$USER
initdb
pg_ctl -o "-p $PGPORT" -D $PGDATA -l logfile start
createdb -p $PGPORT $DB_NAME
psql -p $PGPORT $DB_NAME

# initdb
# pg_ctl -o "-p $PGPORT" -D $PGDATA -l logfile start
# psql -p $PGPORT $DB_NAME

# cd $PGDATA
# pg_ctl -p $PGPORT stop
# pg_ctl stop -m f
# pg_ctl status
# psql -p $PGPORT $DB_NAME < yourscript.sql 
# /home/csmajs/stong002/tmp/stong002/data

# Delete Chat. Messages -> uSers -> chat