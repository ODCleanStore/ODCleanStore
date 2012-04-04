package cz.cuni.mff.odcleanstore.qualityassessment;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Container for graphs to be passed to Engine
 * (if we decide to make Engine responsible for handling
 * graphs affected by rule change)
 * 
 * otherwise this would be just an internal container for QA
 * needs
 */
public class GraphSet
{
	private HashSet<VirtGraph> graphs = new HashSet<VirtGraph>();
	
	public Iterator<VirtGraph> iterator ()
	{
		return graphs.iterator();
	}
}
