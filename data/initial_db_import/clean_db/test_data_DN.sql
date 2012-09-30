-- GROUPS
INSERT INTO DB.ODCLEANSTORE.DN_RULES_GROUPS (label, description, authorId) VALUES ('test group', 'this is a group for testing purposes', NULL);

-- RULES
INSERT INTO DB.ODCLEANSTORE.DN_RULES (id, groupId, description) VALUES (0, (SELECT id FROM DB.ODCLEANSTORE.DN_RULES_GROUPS WHERE label = 'test group'), '');
INSERT INTO DB.ODCLEANSTORE.DN_RULES (id, groupId, description) VALUES (1, (SELECT id FROM DB.ODCLEANSTORE.DN_RULES_GROUPS WHERE label = 'test group'), '');

-- COMPONENTS
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS (id, ruleId, typeId, modification, description) VALUES (
	0,
	0,
	(SELECT id FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = 'INSERT'),
	'{?a ?b ?y} WHERE {GRAPH $$$$graph$$$$ {SELECT ?a ?b fn:replace(str(?c), ".", "*") AS ?y WHERE {?a ?b ?c}}}', 
	'');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS (id, ruleId, typeId, modification, description) VALUES (
	1,
	0,
	(SELECT id FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = 'DELETE'),
	'{?a ?b ?c} WHERE {GRAPH $$$$graph$$$$ {?a ?b ?c} FILTER (contains(str(?c), "*") = false)}',
	'');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS (id, ruleId, typeId, modification, description) VALUES (
	2,
	1,
	(SELECT id FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = 'INSERT'),
	'{?a <http://example.com/#test> ?b} WHERE {GRAPH $$$$graph$$$$ {?a ?b ?c} FILTER (contains(str(?c), "*******"))}', 
	'');



