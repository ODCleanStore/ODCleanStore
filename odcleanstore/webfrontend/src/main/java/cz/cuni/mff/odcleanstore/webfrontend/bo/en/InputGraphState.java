package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * 
 * @author Jakub Daniel
 *
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
