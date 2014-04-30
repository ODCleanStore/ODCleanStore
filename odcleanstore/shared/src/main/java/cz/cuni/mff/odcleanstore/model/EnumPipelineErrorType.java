package cz.cuni.mff.odcleanstore.model;

import cz.cuni.mff.odcleanstore.connection.exceptions.ModelException;

/**
 * Types of pipeline errors.
 * @author Petr Jerman
 */
public enum EnumPipelineErrorType {

    TRANSFORMER_FAILURE,
    DATA_LOADING_FAILURE,
    COPY_TO_CLEAN_DB_FAILURE;

    private static final String ERROR_DECODE_PIPELINE_ERROR_TYPE = "Error during decoding id to PipelineErrorType";

    public int toId() {
        return this.ordinal() + 1;
    }

    public static EnumPipelineErrorType fromId(int id) throws ModelException {
        EnumPipelineErrorType[] values = EnumPipelineErrorType.values();
        if (id < 1 || id > values.length) {
            throw new ModelException(ERROR_DECODE_PIPELINE_ERROR_TYPE);
        }
        return EnumPipelineErrorType.values()[id - 1];
    }
}
