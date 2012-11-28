SET AUTOCOMMIT ON;

/*
        ===========================================================================
        TEMPORARY GRAPH LIST - CAN BE USED TO CLEAR DATABASE
        ===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.TEMPORARY_GRAPHS
(
        graphName NVARCHAR(255) NOT NULL,

        PRIMARY KEY (graphName)
);


/* 
  Dump all triples in the given named graph to a file, serialized as TTL.
  @param srcgraph dumped graph URI
  @param out_file (absolute) path to the written file; backslashes must be escaped
  Adapted from http://www.openlinksw.com/dataspace/dav/wiki/Main/VirtDumpLoadRdfGraphs 
*/
CREATE PROCEDURE dump_graph_ttl (
        IN  srcgraph VARCHAR, 
        IN  out_file VARCHAR) {
    DECLARE  file_name VARCHAR;
    DECLARE  env, ses ANY;
    DECLARE  ses_len, max_ses_len INTEGER;
    SET ISOLATION = 'uncommitted';
    max_ses_len := 10000000;
    string_to_file(
            out_file, 
            sprintf ('# Dump of graph <%s>, as of %s\n', srcgraph, CAST (NOW() AS VARCHAR)), 
            -2);
    env := vector (dict_new (16000), 0, '', '', '', 0, 0, 0, 0);
    ses := string_output ();
    FOR (SELECT * FROM (
            SPARQL DEFINE input:storage "" 
            SELECT ?s ?p ?o { 
	        GRAPH `iri(?:srcgraph)` { ?s ?p ?o } 
	    }) AS sub OPTION (LOOP)) DO {
        http_ttl_triple (env, "s", "p", "o", ses);
        ses_len := length (ses);
        IF (ses_len > max_ses_len) {
            string_to_file (out_file, ses, -1);
            ses := string_output ();
        }
    }
    IF (LENGTH (ses)) {
        http (' .\n', ses);
        string_to_file (out_file, ses, -1);
    }
};

INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('odcs', 'http://opendata.cz/infrastructure/odcleanstore/');
INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('odcs-data', 'http://opendata.cz/infrastructure/odcleanstore/data/');
INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('odcs-metadata', 'http://opendata.cz/infrastructure/odcleanstore/metadata/');
INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('odcs-provenance', 'http://opendata.cz/infrastructure/odcleanstore/provenanceMetadata/');


call USER_CREATE('SILK', 'odcs');
call USER_GRANT_ROLE('SILK','SPARQL_UPDATE',0);




