-- GROUPS
INSERT INTO DB.ODCLEANSTORE.DN_RULES_GROUPS (label, description, authorId) VALUES (n'test group', n'this is a group for testing purposes', NULL);

-- RULES
INSERT INTO DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED (id, groupId, label, description) 
VALUES (0, (SELECT id FROM DB.ODCLEANSTORE.DN_RULES_GROUPS WHERE label = 'test group'), n'test rule A', n'');

INSERT INTO DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED (id, groupId, label, description) 
VALUES (1, (SELECT id FROM DB.ODCLEANSTORE.DN_RULES_GROUPS WHERE label = 'test group'), n'test rule B', n'');

-- COMPONENTS
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS_UNCOMMITTED (ruleId, typeId, modification, description) VALUES (
	0,
	(SELECT id FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = 'INSERT'),
	'{?a ?b ?y} WHERE {GRAPH $$$$graph$$$$ {SELECT ?a ?b fn:replace(str(?c), ".", "*") AS ?y WHERE {?a ?b ?c}}}', 
	'');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS_UNCOMMITTED (ruleId, typeId, modification, description) VALUES (
	0,
	(SELECT id FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = 'DELETE'),
	'{?a ?b ?c} WHERE {?a ?b ?c FILTER (contains(str(?c), "*") = false)}',
	'');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS_UNCOMMITTED (ruleId, typeId, modification, description) VALUES (
	1,
	(SELECT id FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = 'INSERT'),
	'{?a <http://example.com/#test> ?b} WHERE {?a ?b ?c FILTER (contains(str(?c), "*******"))}', 
	'');

DELETE FROM DB.ODCLEANSTORE.DN_RULES;
INSERT INTO DB.ODCLEANSTORE.DN_RULES SELECT * FROM DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED;
DELETE FROM DB.ODCLEANSTORE.DN_RULE_COMPONENTS;
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS SELECT * FROM DB.ODCLEANSTORE.DN_RULE_COMPONENTS_UNCOMMITTED;

