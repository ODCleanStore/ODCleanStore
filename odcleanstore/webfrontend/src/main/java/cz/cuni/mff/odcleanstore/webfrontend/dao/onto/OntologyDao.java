package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import virtuoso.jena.driver.VirtGraph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import cz.cuni.mff.odcleanstore.data.EnumDatabaseInstance;
import cz.cuni.mff.odcleanstore.data.GraphLoader;
import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.datanormalization.rules.DataNormalizationRule;
import cz.cuni.mff.odcleanstore.datanormalization.rules.DataNormalizationRulesModel;
import cz.cuni.mff.odcleanstore.qualityassessment.exceptions.QualityAssessmentException;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRulesModel;
import cz.cuni.mff.odcleanstore.shared.Utils;
import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentUncommittedDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleUncommittedDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleUncommittedDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;

public class OntologyDao extends DaoForEntityWithSurrogateKey<Ontology>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "ONTOLOGIES";
	private static final String OUTPUT_LANGUAGE = "RDF/XML-ABBREV";
	private static final String RULE_GROUP_PREFIX = "Generated from ontology: ";
	private static final String QA_MAPPING_TABLE_NAME = "QA_RULES_GROUPS_TO_ONTOLOGIES_MAP";
	private static final String DN_MAPPING_TABLE_NAME = "DN_RULES_GROUPS_TO_ONTOLOGIES_MAP";

	protected static Logger logger = Logger.getLogger(OntologyDao.class);

	private static final long serialVersionUID = 1L;

	private ParameterizedRowMapper<Ontology> rowMapper;

	public OntologyDao()
	{
		this.rowMapper = new OntologyRowMapper();
	}

	@Override
	public String getTableName()
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<Ontology> getRowMapper()
	{
		return rowMapper;
	}

	@Override
	protected String getSelectAndFromClause()
	{
		String query = "SELECT u.username, o.* " +
			" FROM " + getTableName() + " AS o LEFT JOIN " + UserDao.TABLE_NAME + " AS u ON (o.authorId = u.id)";
		return query;
	}
	
	@Override
	public Ontology load(Integer id)
	{
		return loadBy("o.id", id);
	}
	
	/** Load Ontology without retrieving its definition. */
	private Ontology loadRaw(Integer id) 
	{
		return super.loadBy("o.id", id);
	}
	
	@Override
	public Ontology loadBy(String columnName, Object value)
	{
		Ontology ontology = super.loadBy(columnName, value);

		ontology.setDefinition(loadRdfData(ontology.getGraphName()));

		return ontology;
	}

	private String loadRdfData(String graphName)
	{
		logger.debug("Loading RDF graph: " + graphName);

		VirtGraph graph = new VirtGraph(graphName, getLookupFactory().getCleanDataSource());
		Model model = ModelFactory.createModelForGraph(graph);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		model.write(stream, OUTPUT_LANGUAGE);
		String result = null;
		try
		{
			result = stream.toString(Utils.DEFAULT_ENCODING);
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error("Failed serializing ontology definition", e);
			// TODO handle
		}
		return result;
	}

	@Override
	public void save(final Ontology item) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				String query = "INSERT INTO " + TABLE_NAME + " (label, description, graphName, authorId) VALUES (?, ?, ?, ?)";

				setGraphName(item);
				Object[] params =
				{
					item.getLabel(),
					item.getDescription(),
					item.getGraphName(),
					item.getAuthorId()
				};

				logger.debug("label: " + item.getLabel());
				logger.debug("description: " + item.getDescription());
				logger.debug("graphName" + item.getGraphName());

				// to be able to drop a graph in Virtuoso, it has to be explicitly created before
				createGraph(item.getGraphName());

				GraphLoader graphLoader = new GraphLoader(EnumDatabaseInstance.CLEAN);
				graphLoader.importGraph(item.getDefinition(), item.getGraphName());

				// Call after working with RDF in case it fails
				jdbcUpdate(query, params);
				generateRules(item.getLabel());
			}
		});
	}

	private void setGraphName(Ontology item) 
	{
		try
		{
			item.setGraphName(ODCSInternal.ontologyGraphUriPrefix + URLEncoder.encode(item.getLabel(), Utils.DEFAULT_ENCODING));
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO handle
		}
	}

	private void createGraph(String graphName) throws Exception
	{
		String query = "SPARQL CREATE SILENT GRAPH ??";

		Object[] params = { graphName };

		jdbcUpdate(query, params);
	}

	public void update(final Ontology item) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{

			@Override
			public void execute() throws Exception
			{
				delete(item);
				setGraphName(item);
				save(item);
			}
		});
	}

	@Override
	protected void deleteRaw(final Integer id) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				Ontology onto = loadRaw(id);
				deleteGraph(onto.getGraphName());
				deleteMappings(id);
				OntologyDao.super.deleteRaw(id);
			}
		});
	}
	
	private void deleteGraph(String graphName) throws Exception
	{
		String query = "SPARQL DROP SILENT GRAPH ??";

		Object[] params = { graphName };

		jdbcUpdate(query, params);
	}
	
	private void generateRules(String ontologyLabel) throws Exception {
		Ontology ontology = OntologyDao.super.loadBy("label", ontologyLabel);

		Integer ontologyId = ontology.getId();

		String groupLabel = createGroupLabel(ontologyLabel);
		String ontologyGraphName = ontology.getGraphName();

		generateQualityAssessmentRules(ontologyId, groupLabel, ontologyGraphName);
		generateDataNormalizationRules(ontologyId, groupLabel, ontologyGraphName);
	}
	
	private void generateQualityAssessmentRules(final Integer ontologyId, final String groupLabel, final String ontologyGraphName) throws Exception
	{
		executeInTransaction(new CodeSnippet() {
			@Override
			public void execute() throws Exception {
				createRulesGroup(QARulesGroupDao.TABLE_NAME, groupLabel);

				Integer groupId = getGroupId(QARulesGroupDao.TABLE_NAME, groupLabel);

				createMapping(createMappingTableName(QARulesGroupDao.TABLE_NAME), groupId, ontologyId);

				QualityAssessmentRulesModel rulesModel = new QualityAssessmentRulesModel(getLookupFactory().getCleanDataSource());

				try
				{
					Collection<QualityAssessmentRule> rules = rulesModel.compileOntologyToRules(groupId, ontologyGraphName);
					
					//TODO: ABORT IF EMPTY
					
					for (QualityAssessmentRule rule : rules) {
						QARuleUncommittedDao ruleDao = OntologyDao.this.getLookupFactory().getDao(QARuleUncommittedDao.class);
						
						ruleDao.save(new QARule(rule));
					}
				}
				catch (QualityAssessmentException e)
				{
					logger.error(e.getMessage());
					throw e;
				}
			}
		});
	}
	
	private void generateDataNormalizationRules(final Integer ontologyId, final String groupLabel, final String ontologyGraphName) throws Exception
	{
		executeInTransaction(new CodeSnippet() {
			@Override
			public void execute() throws Exception {
				createRulesGroup(DNRulesGroupDao.TABLE_NAME, groupLabel);

				Integer groupId = getGroupId(DNRulesGroupDao.TABLE_NAME, groupLabel);

				createMapping(createMappingTableName(DNRulesGroupDao.TABLE_NAME), groupId, ontologyId);

				DataNormalizationRulesModel rulesModel = new DataNormalizationRulesModel(getLookupFactory().getCleanDataSource());

				try
				{
					Collection<DataNormalizationRule> rules = rulesModel.compileOntologyToRules(groupId, ontologyGraphName);
					
					//TODO: ABORT IF EMPTY
					
					for (DataNormalizationRule rule : rules) {
						DNRuleUncommittedDao ruleDao = OntologyDao.this.getLookupFactory().getDao(DNRuleUncommittedDao.class);
						DNRuleComponentUncommittedDao ruleComponentDao = OntologyDao.this.getLookupFactory().getDao(DNRuleComponentUncommittedDao.class);
						DNRuleComponentTypeDao ruleComponentTypeDao = OntologyDao.this.getLookupFactory().getDao(DNRuleComponentTypeDao.class);
						
						Integer ruleId = ruleDao.saveAndGetKey(new DNRule(rule));
						
						for (DataNormalizationRule.Component component : rule.getComponents()) {
							DNRuleComponentType type = ruleComponentTypeDao.loadAllBy("label", component.getType().toString()).get(0);
							DNRuleComponent componentWithRuleId = new DNRuleComponent(null,
									ruleId, type, component.getModification(), component.getDescription());
							
							componentWithRuleId.setRuleId(ruleId);
							
							ruleComponentDao.save(componentWithRuleId);
						}
					}
				}
				catch (DataNormalizationException e)
				{
					logger.error(e.getMessage());
					throw e;
				}
			}
		});
	}

	private String createGroupLabel(String ontologyLabel)
	{
		return RULE_GROUP_PREFIX + ontologyLabel;
	}

	private void createRulesGroup(String tableName, String groupLabel) throws Exception
	{
		String query = "INSERT INTO " + tableName + " (label) VALUES (?)";

		Object[] params = { groupLabel };

		logger.debug("groupName" + groupLabel);

		jdbcUpdate(query, params);
	}

	private String createMappingTableName(String groupTableName)
	{
		if (QARulesGroupDao.TABLE_NAME.equals(groupTableName))
		{
			return QA_MAPPING_TABLE_NAME;
		}
		else if (DNRulesGroupDao.TABLE_NAME.equals(groupTableName))
		{
			return DN_MAPPING_TABLE_NAME;
		}
		else
		{
			throw new AssertionError("Unexpected rules-group table name provided: " + groupTableName);
		}
	}

	private Integer getGroupId(String tableName, String groupLabel)
	{
		String query = "SELECT id FROM " + tableName + " WHERE label = ?";

		Object[] params = { groupLabel };

		return jdbcQueryForObject(query, params, Integer.class);
	}

	private void createMapping(String tableName, Integer groupId, Integer ontologyId) throws Exception
	{
		String query = "INSERT INTO " + TABLE_NAME_PREFIX + tableName + " (groupId, ontologyId) VALUES (?, ?)";

		Object[] params =
		{
			groupId,
			ontologyId
		};

		logger.debug("groupId" + groupId);
		logger.debug("ontologyId" + ontologyId);

		jdbcUpdate(query, params);
	}
	
	private void deleteMappings(Integer ontologyId) throws Exception
	{
		String query = "SPARQL CLEAR GRAPH ??";
		
		Object[] params = { OntologyMappingDao.createGraphName(ontologyId) };
		
		jdbcUpdate(query, params);
	}
}
