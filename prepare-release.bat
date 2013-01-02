:: Create directory for the release
rmdir /s release
mkdir release


::  Build maven project
cd odcleanstore
cmd /c mvn clean install javadoc:aggregate -P javadoc >>..\maven-build.log 2>&1
::cmd /c mvn clean install -Dmaven.test.skip=true  >>..\maven-build.log 2>&1

cd ..

:: Add binaries
xcopy release-template\bin release\bin /s /e /i
del release\bin\webapp\.gitignore
xcopy odcleanstore\engine\target\lib release\bin\engine\lib /i
echo F | xcopy odcleanstore\engine\target\odcs-engine-?.?.?.jar release\bin\engine\odcs-engine.jar
echo F | xcopy odcleanstore\webfrontend\target\odcs-webfrontend-*.war release\bin\webapp\odcs-webfrontend.war
echo F | xcopy odcleanstore\core\target\odcs-core-*.jar release\bin\
echo F | xcopy odcleanstore\simplescraper\target\odcs-simplescraper-?.?.?-jar-with-dependencies.jar release\bin\odcs-simplescraper.jar
echo F | xcopy odcleanstore\simpletransformer\target\odcs-simpletransformer-*.jar release\bin\
echo F | xcopy odcleanstore\installer\target\odcs-installer-?.?.?-jar-with-dependencies.jar release\bin\odcs-installer.jar


:: Configuration
mkdir release\config
echo F | xcopy data\odcs_configuration\odcs.ini release\config
echo F | xcopy data\virtuoso_configuration\virtuoso.ini-clean release\config
echo F | xcopy data\virtuoso_configuration\virtuoso.ini-dirty release\config


:: Database
xcopy release-template\database release\database /s /e /i
echo F | xcopy data\initial_db_import\clean_db\*.sql release\database\clean_db
xcopy data\initial_db_import\clean_db\update_db_scripts release\database\clean_db\update_db_scripts /s /e /i
echo F | xcopy data\initial_db_import\dirty_db\*.sql release\database\dirty_db

mkdir release\database\install
echo F | xcopy data\initial_db_import\clean_db\clear_rel_database.sql release\database\install\clean-db-clear.sql
echo F | xcopy data\initial_db_import\clean_db\05_enable_fulltext_index.sql release\database\install\clean-db-fulltext-index.sql
type data\initial_db_import\clean_db\00_autocommit_on.sql^
     data\initial_db_import\clean_db\01_create_rel_database.sql^
     data\initial_db_import\clean_db\02_stored_procedures.sql^
     data\initial_db_import\clean_db\03_odcs_prefixes.sql^
     data\initial_db_import\clean_db\04_user_accounts.sql^
     > release\database\install\clean-db-import.sql
echo F | xcopy data\initial_db_import\dirty_db\clear_rel_database.sql release\database\install\dirty-db-clear.sql
type data\initial_db_import\dirty_db\00_autocommit_on.sql^
     data\initial_db_import\dirty_db\01_create_rel_database.sql^
     data\initial_db_import\dirty_db\02_stored_procedures.sql^
     data\initial_db_import\dirty_db\03_odcs_prefixes.sql^
     data\initial_db_import\dirty_db\04_grant_sparql_update.sql^
     > release\database\install\dirty-db-import.sql
     
:: Documentation
xcopy release-template\doc release\doc /s /e /i
xcopy odcleanstore\target\site\apidocs release\doc\javadoc /Q /s /e /i

:: Example data, scripts & forms
xcopy release-template\example release\example /s /e /i
xcopy release-template\example-queries release\example-queries /s /e /i

:: Files in the root distribution directory
echo F | xcopy release-template\*.cmd release
echo F | xcopy release-template\*.sh release
echo F | xcopy release-template\*.txt release