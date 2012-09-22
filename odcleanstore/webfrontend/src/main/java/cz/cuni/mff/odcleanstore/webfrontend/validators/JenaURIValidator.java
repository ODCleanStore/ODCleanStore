package cz.cuni.mff.odcleanstore.webfrontend.validators;

import org.apache.wicket.validation.IValidatable;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;

public class JenaURIValidator extends CustomValidator 
{
	private static final long serialVersionUID = 1L;
	
	static IRIFactory factory = IRIFactory.jenaImplementation();

	@Override
	public void validate(IValidatable<String> validatable) 
	{
		 IRI iri = factory.create( validatable.getValue() );
         
         if (iri.hasViolation(false))
         {
        	 handleError(validatable, "invalid-uri-jena");
         }
	}
}
