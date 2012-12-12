:: Initializes the clean DB structure.
::

@ECHO OFF

IF %1.==. GOTO ERR
IF %2.==. GOTO ERR
IF %3.==. GOTO ERR
IF %4.==. GOTO ERR

isql %1:%2 %3 %4 00_autocommit_on.sql
isql %1:%2 %3 %4 01_create_rel_database.sql
isql %1:%2 %3 %4 02_stored_procedures.sql
isql %1:%2 %3 %4 03_odcs_prefixes.sql
isql %1:%2 %3 %4 04_user_accounts.sql
isql %1:%2 %3 %4 05_enable_fulltext_index.sql

GOTO END

:ERR
ECHO USAGE: INIT_STRUCTURE.bat host port username password

:END
