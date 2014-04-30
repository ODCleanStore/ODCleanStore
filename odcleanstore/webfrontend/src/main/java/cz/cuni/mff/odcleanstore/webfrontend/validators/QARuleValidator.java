package cz.cuni.mff.odcleanstore.webfrontend.validators;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionFactory;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.core.UUIDUniqueURIGenerator;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

/**
 * Validator that attempts to run a rule to verify
 * its syntactical correctness
 *
 * WARNING: it is still possible for the validated rules
 *          to fail due to errors in functions based on input
 *          data as it will be performed over non-existent
 *          (empty) graph
 * 
 * @author Jakub Daniel
 */
public class QARuleValidator extends CustomValidator {

	private static final long serialVersionUID = 1L;
	
	private final JDBCConnectionCredentials credentials;
	
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
					"validation-rule",
					null  /* Rule Description */
			).toString(new UUIDUniqueURIGenerator(ODCSInternal.DEBUG_TEMP_GRAPH_URI_PREFIX).nextURI());
			
			connection = VirtuosoConnectionFactory.createJDBCConnection(credentials);
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