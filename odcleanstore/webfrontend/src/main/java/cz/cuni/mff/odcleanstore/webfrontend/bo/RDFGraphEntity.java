package cz.cuni.mff.odcleanstore.webfrontend.bo;

public abstract class RDFGraphEntity extends EntityWithSurrogateKey {

	private static final long serialVersionUID = 1L;
	
	protected String graphName;
	protected String rdfData;

	public RDFGraphEntity(Long id, String graphName)
	{
		super(id);
		this.graphName = graphName;
	}
	
	public RDFGraphEntity()
	{
	}

	public String getGraphName()
	{
		return graphName;
	}

	public void setGraphName(String graphName)
	{
		this.graphName = graphName;
	}

	public String getRdfData() 
	{
		return rdfData;
	}

	public void setRdfData(String rdfData) 
	{
		this.rdfData = rdfData;
	}
}
