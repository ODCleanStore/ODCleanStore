INSERT INTO DB.ODCLEANSTORE.QA_RULES_GROUPS (label, description) VALUES (n'sample group', n'short description of this group');

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, label, filter, coefficient, description) 
values (
	(SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'sample group'), 
	n'invalid procurement reference', 
	n'{{?s <http://purl.org/procurement#referenceNumber> ?o} FILTER (bif:regexp_like(?o, \'[a-zA-Z]\'))}', 
	0.9, 
	n'PROCUREMENT REFERENCE NUMBER CONSISTS OF UNANTICIPATED CHARACTERS'
);

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, label, filter, coefficient, description) 
values (
	(SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'sample group'), 
	n'invalid procedure type', 
	n'{{?s <http://purl.org/procurement#procedureType> ?o}} GROUP BY ?g ?s HAVING count(?o) > 1', 
	0.75, 
	n'PROCEDURE TYPE AMBIGUOUS'
);

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, label, filter, coefficient, description) 
values (
	(SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'sample group'), 
	n'invalid tender completion date', 
	n'{{?s <http://purl.org/procurement#tenderDeadline> ?d; <http://purl.org/procurement#endDate> ?e} FILTER (?e > ?d)}', 
	0.9, 
	n'TENDER COMPLETION DATE EXCEEDED ITS DEADLINE'
);

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, label, filter, coefficient, description) 
values (
	(SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'sample group'), 
	n'invalid list of tenders', 
	n'{{?s <http://purl.org/procurement#numberOfTenders> ?n. ?s <http://purl.org/procurement#tender> ?t}} GROUP BY ?g ?s ?n HAVING count(?t) != ?n', 
	0.9, 
	n'LIST OF TENDERS HAS DIFFERENT SIZE FROM WHAT WAS EXPECTED BY \'numberOfTenders\' PROPERTY'
);

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, label, filter, coefficient, description) 
values (
	(SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'sample group'), 
	n'invalid procurement contant person', 
	n'{{?s <http://purl.org/procurement#contactPerson> ?c}} GROUP BY ?g HAVING count(?c) != 1', 
	0.8, 
	n'PROCUREMENT CONTACT PERSON MISSING'
);

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, label, filter, coefficient, description) 
values (
	(SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'sample group'), 
	n'invalid procurement', 
	n'{{?s <http://purl.org/procurement#lot> ?c; <http://purl.org/procurement#tender> ?t}}', 
	0.8, 
	n'PROCUREMENT BROKEN INTO SEVERAL CONTRACTS CANNOT HAVE DIRECT TENDERS'
);

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, label, filter, coefficient, description) 
values (
	(SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'sample group'), 
	n'invalid procurement costs', 
	n'{{?s <http://purl.org/procurement#estimatedPrice> ?p1; <http://purl.org/procurement#actualPrice> ?p2. ?p1 <http://purl.org/goodrelations/v1#hasCurrencyValue> ?v1. ?p2 <http://purl.org/goodrelations/v1#hasCurrencyValue> ?v2} FILTER (2 * ?v1 < ?v2)}', 
	0.8, 
	n'PROCUREMENT ACTUAL COSTS ARE ABOVE TWICE THE ESTIMATE'
);

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, label, filter, coefficient, description) 
values (
	(SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'sample group'), 
	n'invalid procedure type', 
	n'{{?s <http://purl.org/procurement#procedureType> <http://purl.org/procurement#Open>; <http://purl.org/procurement#estimatedPrice> ?p. ?p <http://purl.org/goodrelations/v1#hasCurrencyValue> ?v.} FILTER (?v < 50000 OR ?v > 3000000)}', 
	0.8, 
	n'PROCEDURE TYPE IS INCOMPATIBLE WITH THE ESTIMATED PRICE'
);

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, label, filter, coefficient, description) 
values (
	(SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'sample group'), 
	n'invalid tender', 
	n'{{?s <http://purl.org/procurement#awardDate> ?a; <http://purl.org/procurement#tenderDeadline> ?d.} FILTER (?d > ?a)}', 
	0.8, 
	n'TENDER AWARDED BEFORE APPLICATION DEADLINE'
);

DELETE FROM DB.ODCLEANSTORE.QA_RULES;
INSERT INTO DB.ODCLEANSTORE.QA_RULES SELECT * FROM DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED;

