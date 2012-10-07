package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.repeater.data.DataView;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessEntity;

/**
 * A generic button to sort a table by one of its columns.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 * @param <BO> the BO related with the sorted table
 */
public class SortTableButton<BO extends BusinessEntity> extends OrderByBorder
{
	private static final long serialVersionUID = 1L;

	/** the data view of the table */
	private DataView<BO> dataView;
	
	/**
	 * 
	 * @param compName
	 * @param columnName
	 * @param dataProvider
	 * @param dataView
	 */
	public SortTableButton(String compName, String columnName, ISortStateLocator dataProvider, DataView<BO> dataView) 
	{
		super(compName, columnName, dataProvider);
		
		this.dataView = dataView;
	}

	@Override
    protected void onSortChanged()
    {
        dataView.setCurrentPage(0);
    }
}
