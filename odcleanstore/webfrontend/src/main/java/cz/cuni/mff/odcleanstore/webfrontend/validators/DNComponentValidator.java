package cz.cuni.mff.odcleanstore.webfrontend.validators;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.validation.IValidatable;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.datanormalization.rules.DataNormalizationRule;
import cz.cuni.mff.odcleanstore.shared.UUIDUniqueURIGenerator;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;

public class DNComponentValidator extends CustomValidator {

	private static final long serialVersionUID = 1L;
	
	private JDBCConnectionCredentials credentials;
	private DropDownChoice<DNRuleComponentType> type;
	
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
					null, /* Rule Description */
					type.getConvertedInput().getLabel(),
					modificationValue,
					null  /* Component Description */
			);
			
			String component = rule.getComponents(new UUIDUniqueURIGenerator(ODCSInternal.debugTempGraphUriPrefix).nextURI())[0];
			
			connection = VirtuosoConnectionWrapper.createConnection(credentials);
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
