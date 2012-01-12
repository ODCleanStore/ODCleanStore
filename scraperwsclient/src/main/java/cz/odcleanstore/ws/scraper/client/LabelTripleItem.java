package cz.odcleanstore.ws.scraper.client;

public class LabelTripleItem extends TripleItem
{
	private String type;
	private String value;
	
	public LabelTripleItem(String type, String value)
	{
		this.type = type;
		this.value = value;
	}
}
