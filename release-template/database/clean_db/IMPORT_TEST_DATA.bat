:: Imports testing data.
::

@ECHO OFF

IF %1.==. GOTO ERR
IF %2.==. GOTO ERR
IF %3.==. GOTO ERR
IF %4.==. GOTO ERR

isql %1:%2 %3 %4 06_test_data_Engine.sql
isql %1:%2 %3 %4 07_test_data_QA.sql
isql %1:%2 %3 %4 08_test_data_DN.sql
isql %1:%2 %3 %4 09_test_data_OI.sql
isql %1:%2 %3 %4 10_test_data_QE.sql

GOTO END

:ERR
ECHO USAGE: IMPORT_TEST_DATA.bat host port username password

:END
