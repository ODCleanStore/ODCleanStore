DELETE FROM DB.ODCLEANSTORE.EN_GRAPHS_IN_ERROR;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS 
	SET stateId = (SELECT id FROM DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES WHERE label = 'FINISHED')
	WHERE stateId = (SELECT id FROM DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES WHERE label = 'WRONG');
  
  
  
