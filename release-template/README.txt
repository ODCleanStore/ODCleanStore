This is an ODCleanStore release 1.1.0.

For more information about ODCleanStore, please see the official documentation
in doc/ subdirectory or visit

  http://sourceforge.net/p/odcleanstore/wiki/Home/

or consult issues and ask questions in our mailing list:

  odcleanstore-user@lists.sourceforge.net
  http://lists.sourceforge.net/lists/listinfo/odcleanstore-user

=========================================
Installing ODCleanStore
=========================================

ODCleanStore provides an installer with graphical user interface. 
Before you run the installer, make sure to read 
"ODCleanStore Administrator's and Installation Manual.pdf" in doc/ subdirectory
and have all the prerequisities satisfied (such as having created and STARTED two
OpenLink Virtuoso database instances).

In order to start the installer run install.cmd (on Windows) or install.sh 
(on Linux). Follow the instructions in the installer and in the installation 
manual.

-----------------------------------------

Alternatively, you may try to install ODCleanStore manually. Manual 
installation is intended for experienced users and developers, however,
and is not officially supported. When you decide to use the installer, you may 
skip this section and continue with section "Running ODCleanStore".

1) Prepare prerequisities of installation described in installation manual the
   same way you would with the official installer.
   
2) Import clean database: 
   Execute script init_structure.bat (init_structure.sh) in database/clean_db/ 
   passing connection information to the clean database instance. Before 
   importing, make sure you have the Viruoso's isql binary in your PATH.
   Typically, the script can be executed as follows:
   
   > init_structure.bat localhost 1111 dba dba

   To import some sample data, you can also execute import_test_data.bat 
   (import_test_data.sh).
   Note: Using the script is the preferred way to execute the queries.
   Executing the queries through Virtuoso Conductor may fail.
   
3) Import dirty database:
   Execute script init_structure.bat (init_structure.sh) in database/dirty_db/ 
   passing connection information to the dirty database instance.
   Typically, the script can be executed as follows:
   
   > init_structure.bat localhost 1112 dba dba

4) You may need to stop and start again the Virtuoso service for clean database 
   so that all queries take effect.

5) Set configuration options in config/odcs.ini .
   Most important configuration options are:
     db.clean.* - JDBC and SPARQL connection settings for the clean 
                  database instance
     db.dirty.* - JDBC and SPARQL connection settings for the dirty
                  database instance

6) Set correct absolute path to odcs.ini configuration file (step 5) in
   administration frontend webarchive. Scripts update-war-path.cmd and 
   update-war-path.sh in the bin/ subdirectory set the path to location of 
   odcs.ini within the release directory.
   
   Alternatively, the path to odcs.ini configuration file can be set manually 
   using utility /bin/frontend-config.jar
   
   > java -jar bin/frontend-config.jar bin/webapp/odcs-webfrontend.war <absolute path to odcs.ini>
   
   or by manually editting the path in file 
   WEB-INF/classes/config/application.properties inside the .war archive.
   
     
7) Run run-engine.cmd or run-engine.sh, respectively, from the bin/ subdirectory
   (and leave the window open).
   
8) In order to use administration web interface, deploy the web archive 
   bin/webapp/odcs-webfrontend.war to your web server.
   
   In Tomcat, for example, this can be done by adding the following to <Host> 
   section of server.xml:  

   <Context path="" docBase="{path to odcs-webfrontend.war}"></Context>

Now your ODCleanStore installation should be up and running.

=========================================
Running ODCleanStore
=========================================

ODCleanStore Engine can be started by executing the run-engine.cmd script 
(Windows) or run-engine.sh script (on Unix) in the installation directory of 
ODCleanStore Engine. The Engine will load its coniguration from the odcs.ini 
file in this directory and start listening on Input and Output Webservice ports 
for requests.

Administration Frontend is a standard Java web application and its lifecycle 
depends on its servlet container { please refer to documentation of your server 
container. Typically, Administration Frontend can be reached by navigating to 
http://localhost:8080/ in a web browser.

-----------------------------------------

If you installed ODCleanStore manually, you can use run-engine.cmd or 
run-engine.sh scripts in the bin/ subdirectory of the distribution.

=========================================
Trying out ODCleanStore
=========================================

In order to try out ODCleanStore, navigate to Administration Frontend in your 
web browser (e.g. http://localhost:8080/). 

By default, there are these registered users:
    * username: "adm", password: "adm", roles: ADM, PIC, ONC
    * username: "scraper", password: "reparcs", roles: SCR
    
There should be one default pipeline created by default. You may customize it 
by assigning  transformers to it. The functionality of Input and Output 
Webservices can be then demonstrated:
    
* To send data to the input web service, you can run run-example.cmd (.sh) from
   directory example/. The script will send data in example-data.rdf
   to Input Webservice together with metadata in example-metadata.properties
   and example-provenance-metadata.rdf
   
   Parameters for Input Webservice can be changed in 
   example-metadata.properties, most notably the name of the pipeline to 
   process the data in the pipelineName option and location of Input Webservice.
      
   Script regenerate-uuid.cmd (.sh) runs an utility that regenerates UUID in
   example-metadata.properties. This is necessary because every request
   to Input Webservice needs to have an unique UUID.

* To query the output web service, start with query-*.html documents in 
  the example-queries/ subdirectory.
  Detailed documentation is in the user manual.

=========================================
Upgrading
=========================================

In order to upgrade from versions 0.3.2 and higher:

1) Replace relevant binaries in the installation directory with their new 
   versions from the /bin directory and redeploy the web archive from 
   /bin/webapp.
   
2) Configuration in /config/odcs.ini may not be backward compatible. Use the new 
   version of configuration file and adjust it manually.
   
3) Database instances can be upgraded using update scripts in 
   /database/clean_db/update_db_scripts
   /database/dirty_db/update_db_scripts
   
   executed in the indicated order. To upgrade from version 0.3.2 to 0.3.4, 
   for example, run all scripts from subdirectories 
   /database/clean_db/update_db_scripts/0.3.3/ and 
   /database/clean_db/update_db_scripts/0.3.4/.
