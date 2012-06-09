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
    
INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('purl','http://purl.org/procurement#');
