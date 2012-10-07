ALTER TABLE DB.ODCLEANSTORE.DN_REPLACE_TEMPLATE_INSTANCES DROP CONSTRAINT DN_REPLACE_TEMPLATE_INSTANCES_DN_RULES_rawRuleId_id;
ALTER TABLE DB.ODCLEANSTORE.DN_REPLACE_TEMPLATE_INSTANCES ADD FOREIGN KEY (rawRuleId) REFERENCES DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED(id) ON DELETE CASCADE;

ALTER TABLE DB.ODCLEANSTORE.DN_FILTER_TEMPLATE_INSTANCES DROP CONSTRAINT DN_FILTER_TEMPLATE_INSTANCES_DN_RULES_rawRuleId_id;
ALTER TABLE DB.ODCLEANSTORE.DN_FILTER_TEMPLATE_INSTANCES ADD FOREIGN KEY (rawRuleId) REFERENCES DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED(id) ON DELETE CASCADE;

ALTER TABLE DB.ODCLEANSTORE.DN_RENAME_TEMPLATE_INSTANCES DROP CONSTRAINT DN_RENAME_TEMPLATE_INSTANCES_DN_RULES_rawRuleId_id;
ALTER TABLE DB.ODCLEANSTORE.DN_RENAME_TEMPLATE_INSTANCES ADD FOREIGN KEY (rawRuleId) REFERENCES DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED(id) ON DELETE CASCADE;

