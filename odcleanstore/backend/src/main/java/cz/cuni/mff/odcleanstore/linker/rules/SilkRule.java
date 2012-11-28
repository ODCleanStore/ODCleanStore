package cz.cuni.mff.odcleanstore.linker.rules;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business entity representing linkage rule for Silk.
 * @author Tomas Soukup
 */
public class SilkRule {
	Integer id;
	String label;
	String linkType;
	String sourceRestriction;
	String targetRestriction;
	String linkageRule;
	BigDecimal filterThreshold;
	Integer filterLimit;
	List<Output> outputs;
	
	/**
	 * @return rule ID
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id rule ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return rule label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label rule label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return link type
	 */
	public String getLinkType() {
		return linkType;
	}
	/**
	 * @param linkType link type
	 */
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	/**
	 * @return source restriction
	 */
	public String getSourceRestriction() {
		return sourceRestriction;
	}
	/**
	 * @param sourceRestriction source restriction
	 */
	public void setSourceRestriction(String sourceRestriction) {
		this.sourceRestriction = sourceRestriction;
	}
	/**
	 * @return target restriction
	 */
	public String getTargetRestriction() {
		return targetRestriction;
	}
	/**
	 * @param targetRestriction target restriction
	 */
	public void setTargetRestriction(String targetRestriction) {
		this.targetRestriction = targetRestriction;
	}
	/**
	 * @return linkage rule XML fragment
	 */
	public String getLinkageRule() {
		return linkageRule;
	}
	/**
	 * @param linkageRule linkage rule XML fragment
	 */
	public void setLinkageRule(String linkageRule) {
		this.linkageRule = linkageRule;
	}
	/**
	 * @return filter threshold
	 */
	public BigDecimal getFilterThreshold() {
		return filterThreshold;
	}
	/**
	 * @param filterThreshold filter threshold
	 */
	public void setFilterThreshold(BigDecimal filterThreshold) {
		this.filterThreshold = filterThreshold;
	}
	/**
	 * @return filter limit
	 */
	public Integer getFilterLimit() {
		return filterLimit;
	}
	/**
	 * @param filterLimit filter limit
	 */
	public void setFilterLimit(Integer filterLimit) {
		this.filterLimit = filterLimit;
	}
	/**
	 * @return list of rule outputs
	 */
	public List<Output> getOutputs() {
		return outputs;
	}
	/**
	 * @param outputs list of rule outputs
	 */
	public void setOutputs(List<Output> outputs) {
		this.outputs = outputs;
	}
	
	@Override
	public String toString() {
		return "id: " + id + " label: " + label;
	}
}
