INSERT INTO DB.ODCLEANSTORE.OI_RULES_GROUPS (label, description) VALUES (n'test group', n'test OI group');
    
INSERT INTO DB.ODCLEANSTORE.OI_RULES_UNCOMMITTED (groupId, label, linkType, sourceRestriction, targetRestriction, linkageRule, filterThreshold, filterLimit) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.OI_RULES_GROUPS WHERE label = n'test group'),
    n'supplier', 
	n'owl:sameAs', 
	n'?x <http://purl.org/procurement#supplier> ?a .', 
	n'?y <http://purl.org/procurement#supplier> ?b .',
	n'<LinkageRule>
  <Compare weight="1" threshold="0.0" required="true" metric="equality" id="unnamed_3">
    <Input path="?a/&lt;http://purl.org/procurement#title&gt;" id="unnamed_1"></Input>
    <Input path="?b/&lt;http://purl.org/procurement#title&gt;" id="unnamed_2"></Input>
  </Compare>
</LinkageRule>',
	null,
	null
);

INSERT INTO DB.ODCLEANSTORE.OI_OUTPUTS_UNCOMMITTED (ruleId, outputTypeId, minConfidence, maxConfidence, fileName, fileFormatId) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.OI_RULES_UNCOMMITTED WHERE label = n'supplier'),
	(SELECT id FROM DB.ODCLEANSTORE.OI_OUTPUT_TYPES WHERE label = 'DB'), 
	0.95,
	null, 
	null,
	null);

DELETE FROM DB.ODCLEANSTORE.OI_RULES;
INSERT INTO DB.ODCLEANSTORE.OI_RULES SELECT * FROM DB.ODCLEANSTORE.OI_RULES_UNCOMMITTED;
DELETE FROM DB.ODCLEANSTORE.OI_OUTPUTS;
INSERT INTO DB.ODCLEANSTORE.OI_OUTPUTS SELECT * FROM DB.ODCLEANSTORE.OI_OUTPUTS_UNCOMMITTED;
