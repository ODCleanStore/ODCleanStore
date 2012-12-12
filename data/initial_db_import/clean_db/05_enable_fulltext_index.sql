/*
	Enables and rebuilds fulltext index for use with keyword queries.
*/
DB.DBA.RDF_OBJ_FT_RULE_ADD (null, null, 'All');
DB.DBA.VT_INC_INDEX_DB_DBA_RDF_OBJ ();
