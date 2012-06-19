DELETE FROM DB.ODCLEANSTORE.OI_RULES;
DELETE FROM DB.ODCLEANSTORE.OI_RULES_GROUPS;

INSERT INTO DB.ODCLEANSTORE.OI_RULES_GROUPS VALUES (1, n'pravidla pro linkovani testovacich dat o zakazkach od Tomase K.', n'pravidla pro linkovani testovacich dat o zakazkach od Tomase K.');

INSERT INTO DB.ODCLEANSTORE.OI_RULES VALUES (1, 1, n'<Interlink id="title_supplier">
      <LinkType>owl:sameAs</LinkType>
      <SourceDataset dataSource="Zakazky" var="a">
        <RestrictTo> ?x purl:supplier ?a . </RestrictTo>
      </SourceDataset>
      <TargetDataset dataSource="Zakazky" var="b">
        <RestrictTo> ?y purl:supplier ?b . </RestrictTo>
      </TargetDataset>
      <LinkageRule>
        <Compare weight="1" threshold="0.0" required="true" metric="equality" id="unnamed_3">
          <Input path="?a/purl:title" id="unnamed_1"></Input>
          <Input path="?b/purl:title" id="unnamed_2"></Input>
        </Compare>
      </LinkageRule>
      <Filter></Filter>
      <Outputs>
        <Output type="sparul" minConfidence="0.95" />
      </Outputs>
    </Interlink>');
    
SPARQL INSERT INTO <http://opendata.cz/data/namedGraph/2> {
	<http://opendata.cz/data/767762e4-8180-428f-9c78-0edf60fcd911> <http://purl.org/procurement#supplier> <http://opendata.cz/data/0781c6a4-1147-473d-968a-666284ebc977> .
	<http://opendata.cz/data/0781c6a4-1147-473d-968a-666284ebc977> <http://purl.org/procurement#title> "ZAVOS s.r.o.".
}
    
INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('purl','http://purl.org/procurement#');
