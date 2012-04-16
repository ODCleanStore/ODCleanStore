package cz.cuni.mff.odcleanstore.data;
/**
 * RDF prefix representation
 * 
 * @author Tomas Soukup
 */
public class RDFprefix {
	// prefix id
	private String prefixId;
	// prefix namespace
	private String namespace;
	
	public String getPrefixId() {
		return prefixId;
	}
	public void setPrefixId(String prefixId) {
		this.prefixId = prefixId;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
