:: Create directory for the release
rmdir /s release
mkdir release


::  Build maven project
cd odcleanstore
mvn clean install javadoc:aggregate -P javadoc
:: mvn clean install -Dmaven.test.skip=true

cd ..

:: Add binaries
xcopy release-template\bin release\bin /s /e /i
del release\bin\webapp\.gitignore
xcopy odcleanstore\engine\target\lib release\bin\engine\lib /i
copy odcleanstore\engine\target\odcs-engine-*.jar release\bin\engine\odcs-engine.jar
copy odcleanstore\webfrontend\target\odcs-webfrontend-*.war release\bin\webapp\odcs-webfrontend.war
copy odcleanstore\core\target\odcs-core-*.jar release\bin\
copy odcleanstore\simplescraper\target\odcs-simplescraper-*-jar-with-dependencies.jar release\bin\odcs-simplescraper.jar
copy odcleanstore\simpletransformer\target\odcs-simpletransformer-*.jar release\bin\
copy odcleanstore\installer\target\odcs-installer-*-jar-with-dependencies.jar release\bin\odcs-installer.jar


:: Configuration
mkdir release\config
copy data\odcs_configuration\odcs.ini release\config
copy data\virtuoso_configuration\virtuoso.ini-clean release\config
copy data\virtuoso_configuration\virtuoso.ini-dirty release\config


:: Database
xcopy release-template\database release\database /s /e /i
copy data\initial_db_import\clean_db\*.sql release\database\clean_db
xcopy data\initial_db_import\clean_db\update_db_scripts release\database\clean_db\update_db_scripts /s /e /i
copy data\initial_db_import\dirty_db\*.sql release\database\dirty_db

mkdir release\database\install
copy data\initial_db_import\clean_db\clear_rel_database.sql release\database\install\clean-db-clear.sql
copy data\initial_db_import\clean_db\05_enable_fulltext_index.sql release\database\install\clean-db-fulltext-index.sql
type data\initial_db_import\clean_db\00_autocommit_on.sql^
     data\initial_db_import\clean_db\01_create_rel_database.sql^
     data\initial_db_import\clean_db\02_stored_procedures.sql^
     data\initial_db_import\clean_db\03_odcs_prefixes.sql^
     data\initial_db_import\clean_db\04_user_accounts.sql^
     > release\database\install\clean-db-import.sql
copy data\initial_db_import\dirty_db\clear_rel_database.sql release\database\install\dirty-db-clear.sql
type data\initial_db_import\dirty_db\00_autocommit_on.sql^
     data\initial_db_import\dirty_db\01_create_rel_database.sql^
     data\initial_db_import\dirty_db\02_stored_procedures.sql^
     data\initial_db_import\dirty_db\03_odcs_prefixes.sql^
     data\initial_db_import\dirty_db\04_grant_sparql_update.sql^
     > release\database\install\dirty-db-import.sql
     
:: Documentation
xcopy release-template\doc release\doc /s /e /i
xcopy odcleanstore\target\site\apidocs release\doc\javadoc /s /e /i

:: Example data, scripts & forms
xcopy release-template\example release\example /s /e /i
xcopy release-template\example-queries release\example-queries /s /e /i

:: Files in the root distribution directory
copy release-template\*.cmd release
copy release-template\*.sh release
copy release-template\*.txt release