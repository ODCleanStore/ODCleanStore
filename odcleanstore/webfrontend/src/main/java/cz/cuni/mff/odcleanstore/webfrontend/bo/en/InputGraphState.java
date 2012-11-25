package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * BO for a state of an input graph
 * 
 * This is an enumeration stored in database
 * 
 * @author Jakub Daniel
 */
public class InputGraphState extends EntityWithSurrogateKey {

	private static final long serialVersionUID = 1L;
	
	public int id;
	public String label;
	
	public InputGraphState (int id, String label) {
		this.id = id;
		this.label = label;
	}
}
