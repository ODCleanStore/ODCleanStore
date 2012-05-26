delete from DB.ODCLEANSTORE.OI_RULES;
delete from DB.ODCLEANSTORE.oi_rules_groups;

insert into DB.ODCLEANSTORE.oi_rules_groups values (1, 'pravidla pro linkovani testovacich dat o zakazkach od Tomase K.','pravidla pro linkovani testovacich dat o zakazkach od Tomase K.');

insert into DB.ODCLEANSTORE.OI_RULES values (1,1,'<Interlink id="title_supplier">
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
    
insert into DB.DBA.SYS_XML_PERSISTENT_NS_DECL values ('purl','http://purl.org/procurement#');