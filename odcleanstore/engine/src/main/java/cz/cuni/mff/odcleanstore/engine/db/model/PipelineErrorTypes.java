package cz.cuni.mff.odcleanstore.engine.db.model;

public enum PipelineErrorTypes {

	TRANSFORMER_FAILURE,
	DATA_LOADING_FAILURE;

	private static final String ERROR_DECODE_PIPELINE_ERROR_TYPE = "Error during decoding id to PipelineErrorType";

	public int toId() {
		return this.ordinal() + 1;
	}
	
	public static PipelineErrorTypes fromId(int id) throws DbOdcsException {
		PipelineErrorTypes values[] = PipelineErrorTypes.values();
		if (id < 1 || id > values.length) {
			throw new DbOdcsException(ERROR_DECODE_PIPELINE_ERROR_TYPE);
		}
		return PipelineErrorTypes.values()[id - 1];
	}
}
