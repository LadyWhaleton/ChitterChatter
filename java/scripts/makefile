start:
	pg_ctl -o "-p 2337" -D /home/csmajs/stong002/cs166/ChitterChatter/data -l logfile start

stop:
	pg_ctl -p 2337 stop

db:
	psql -p 2337 mafiawailord

norma:
	psql -p 2337 mafiawailord < norma.sql

status:
	pg_ctl status
