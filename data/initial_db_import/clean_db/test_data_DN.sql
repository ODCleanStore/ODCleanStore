-- GROUPS
INSERT INTO DB.ODCLEANSTORE.DN_RULES_GROUPS (id, label, description) VALUES (0, 'test group', 'this is a group for testing purposes');

-- RULES
INSERT INTO DB.ODCLEANSTORE.DN_RULES (id, groupId, description) VALUES (0, 0, '');
INSERT INTO DB.ODCLEANSTORE.DN_RULES (id, groupId, description) VALUES (1, 0, '');

-- COMPONENTS
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS (id, ruleId, typeId, modification, description) VALUES (0, 0, 0, '{?a ?b ?y} WHERE {GRAPH $$$$graph$$$$ {SELECT ?a ?b fn:replace(str(?c), ".", "*") AS ?y WHERE {?a ?b ?c}}}', '');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS (id, ruleId, typeId, modification, description) VALUES (1, 0, 1, '{?a ?b ?c} WHERE {GRAPH $$$$graph$$$$ {?a ?b ?c} FILTER (contains(str(?c), "*") = false)}', '');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS (id, ruleId, typeId, modification, description) VALUES (2, 1, 0, '{?a <http://example.com/#test> ?b} WHERE {GRAPH $$$$graph$$$$ {?a ?b ?c} FILTER (contains(str(?c), "*******"))}', '');
