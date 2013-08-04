package cz.cuni.mff.odcleanstore.webfrontend.validators;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.validation.IValidatable;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionFactory;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.datanormalization.rules.DataNormalizationRule;
import cz.cuni.mff.odcleanstore.shared.UUIDUniqueURIGenerator;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;

/**
 * Validator that attempts to run a rule component to verify
 * its syntactical correctness
 *
 * WARNING: it is still possible for the validated components
 *          to fail due to errors in functions based on input
 *          data as it will be performed over non-existent
 *          (empty) graph (In case that UUID generator does
 *          not create a conflict, graph names are prefixed
 *          with a special prefix to avoid harm due to any
 *          possible collision)
 * 
 * @author Jakub Daniel
 */
public class DNComponentValidator extends CustomValidator {

	private static final long serialVersionUID = 1L;
	
	private final JDBCConnectionCredentials credentials;
	private final DropDownChoice<DNRuleComponentType> type;
	
	private static Logger logger = Logger.getLogger(DNComponentValidator.class);
	
	public DNComponentValidator(JDBCConnectionCredentials credentials, DropDownChoice<DNRuleComponentType> type) {
		this.credentials = credentials;
		this.type = type;
	}

	@Override
	public void validate(IValidatable<String> validatable) {
		String modificationValue = validatable.getValue();

		VirtuosoConnectionWrapper connection = null;

		try {
			DataNormalizationRule rule = new DataNormalizationRule
			(
					null, /* Rule ID */
					null, /* Rule Group ID */
					"validation-rule",
					null, /* Rule Description */
					type.getConvertedInput().getLabel(),
					modificationValue,
					null  /* Component Description */
			);
			
			String component = rule.getComponents(new UUIDUniqueURIGenerator(ODCSInternal.DEBUG_TEMP_GRAPH_URI_PREFIX).nextURI())[0];
			
			connection = VirtuosoConnectionFactory.createJDBCConnection(credentials);
			connection.adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL);

			connection.execute(component);
		} catch (Exception e) {
			String message = e.getMessage();

			handleError(validatable, "invalid-dn-component", message.replaceFirst(".*(SQ[0-9]{3}): Line ([0-9]+): (SP[0-9]{3}): SPARQL compiler, line ([0-9]+): (.*)", "$5"));
			
			logger.info("Validation failed due to: " + message);
		} finally {
			if (connection != null) {
				try {
					connection.rollback();
					connection.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
