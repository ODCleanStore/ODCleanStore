INSERT INTO DB.ODCLEANSTORE.OI_RULES_GROUPS (label, description) VALUES (n'zakazky TK', n'pravidla pro linkovani testovacich dat o zakazkach od Tomase K.');
    
INSERT INTO DB.ODCLEANSTORE.OI_RULES VALUES (
	1,
	(SELECT id FROM DB.ODCLEANSTORE.OI_RULES_GROUPS WHERE label = n'zakazky TK'),
    n'title_supplier', 
	n'owl:sameAs', 
	n'?x <http://purl.org/procurement#supplier> ?a .', n'?y <http://purl.org/procurement#supplier> ?b .',
	n'<LinkageRule>
  <Compare weight="1" threshold="0.0" required="true" metric="equality" id="unnamed_3">
    <Input path="?a/&lt;http://purl.org/procurement#title&gt;" id="unnamed_1"></Input>
    <Input path="?b/&lt;http://purl.org/procurement#title&gt;" id="unnamed_2"></Input>
  </Compare>
</LinkageRule>',
	null,
	null
);

INSERT INTO DB.ODCLEANSTORE.OI_OUTPUTS VALUES (1, 1, (SELECT id FROM DB.ODCLEANSTORE.OI_OUTPUT_TYPES WHERE label = 'DB'), 0.95, null, null, null);
INSERT INTO DB.ODCLEANSTORE.OI_OUTPUTS VALUES (2, 1, (SELECT id FROM DB.ODCLEANSTORE.OI_OUTPUT_TYPES WHERE label = 'FILE'), 0.95, null, 'C:/test/links.nt', (SELECT id FROM DB.ODCLEANSTORE.OI_FILE_FORMATS WHERE label = 'NTRIPLES'));

INSERT INTO DB.ODCLEANSTORE.OI_RULES_UNCOMMITTED SELECT * FROM DB.ODCLEANSTORE.OI_RULES;
INSERT INTO DB.ODCLEANSTORE.OI_OUTPUTS_UNCOMMITTED SELECT * FROM DB.ODCLEANSTORE.OI_OUTPUTS;
    
SPARQL INSERT INTO <http://opendata.cz/data/namedGraph/2> {
	<http://opendata.cz/data/767762e4-8180-428f-9c78-0edf60fcd911> <http://purl.org/procurement#supplier> <http://opendata.cz/data/0781c6a4-1147-473d-968a-666284ebc977> .
	<http://opendata.cz/data/0781c6a4-1147-473d-968a-666284ebc977> <http://purl.org/procurement#title> "ZAVOS s.r.o.".
};
    
/*INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('purl','http://purl.org/procurement#');*/


