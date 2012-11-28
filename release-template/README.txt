This is an ODCleanStore release 1.0.0.

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

Alternatively, you may try to install ODCleanStore manually. 
Manual installation is intended for experienced users and developers, however,
and is not officially supported.

1) Prepare prerequisities of installation described in installation manual the
   same way you would with the official installer.
   
2) Import clean database: 
   Execute queries in NUMBERED files in database/clean_db/*.sql over the clean 
   database instance in the indicated order. Alternatively, queries in ALL.sql
   can be executed INSTEAD of the numbered files. 
    
   Queries can be executed using the isql utility:
   > isql -S 1111
   
   Note: Using the isql utility is the preferred way to execute the queries.
   Executing the queries through Virtuoso Conductor may fail.
   
3) Import dirty database:
   Execute queries in NUMBERED files in database/dirty_db/*.sql over the dirty
   database instance in the indicated order.
   
   Queries can be executed using the isql utility:
   > isql -S 1112

4) You may need to stop and start again the Virtuoso service for clean database 
   so that all queries take effect.

5) Set configuration options in config/odcs.ini .
   Most important configuration options are:
     db.clean.* - JDBC and SPARQL connection settings for the clean 
                  database instance
     db.dirty.* - JDBC and SPARQL connection settings for the dirty
                  database instance
     
6) Run run-engine.cmd or run-engine.sh, respectively (and leave the window open).

7) Set correct absolute path to odcs.ini configuration file (step 5) in
   administration frontend webarchive. Scripts update-war-path.cmd and 
   update-war-path.sh in the bin/ subdirectory set the path to location of 
   odcs.ini within the release directory.
   
   Alternatively, the path to odcs.ini configuration file can be set manually 
   using utility /bin/frontend-config.jar
   
   > java -jar bin/frontend-config.jar bin/webapp/odcs-webfrontend-<version>.war <absolute path to odcs.ini>
   
   or by manually editting the path in file 
   WEB-INF/classes/config/application.properties inside the .war archive.
   
8) In order to use administration web interface, deploy the web archive 
   bin/webapp/odcs-webfrontend-<version>.war to your web server.
   
   In Tomcat, for example, this can be done by adding the following to <Host> 
   section of server.xml:  

   <Context path="" docBase="{path to odcs-webfrontend-<version>.war}"></Context>

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

By default, there is a single registered user:
    * username: "adm", password: "adm" 
    
In order to create data processing pipelines, you will need to create a new 
user account or add more roles to the default account. There should be one
default pipeline created by default. You may customize it by assigning 
transformers to it. The functionality of Input and Output Webservices can be 
then demonstrated:
    
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

1) Replace relevant binaries from /bin directory with their new versions and redeploy 
   web archive from /bin/webapp.
   
2) Configuration in /config/odcs.ini may not be backward compatible. Use the new 
   version of configuration file and adjust it manually.
   
3) Database instances can be upgraded using update scripts in 
   /database/clean_db/update_db_scripts
   /database/dirty_db/update_db_scripts
   
   To upgrade from version 0.3.2 to 0.3.4, for example, run both scripts
   update_to_0.3.3.sql and update_to_0.3.4.sql.            