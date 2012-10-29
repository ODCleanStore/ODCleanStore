package cz.cuni.mff.odcleanstore.webfrontend.validators;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.shared.UUIDUniqueURIGenerator;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

public class QARuleValidator extends CustomValidator {

	private static final long serialVersionUID = 1L;
	
	private JDBCConnectionCredentials credentials;
	
	private static Logger logger = Logger.getLogger(QARuleValidator.class);
	
	public QARuleValidator(JDBCConnectionCredentials credentials) {
		this.credentials = credentials;
	}

	@Override
	public void validate(IValidatable<String> validatable) {
		String filterValue = validatable.getValue();

		VirtuosoConnectionWrapper connection = null;

		try {
			String rule = new QualityAssessmentRule
			(
					null, /* Rule ID */
					null, /* Rule Group ID */
					filterValue,
					1.0,
					null  /* Rule Description */
			).toString(new UUIDUniqueURIGenerator(ODCSInternal.debugTempGraphUriPrefix).nextURI());
			
			connection = VirtuosoConnectionWrapper.createConnection(credentials);
			connection.adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL);

			connection.execute(rule);
		} catch (Exception e) {
			String message = e.getMessage();

			handleError(validatable, "invalid-qa-rule", message.replaceFirst(".*(SQ[0-9]{3}): Line ([0-9]+): (SP[0-9]{3}): SPARQL compiler, line ([0-9]+): (.*)", "$5"));
			
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