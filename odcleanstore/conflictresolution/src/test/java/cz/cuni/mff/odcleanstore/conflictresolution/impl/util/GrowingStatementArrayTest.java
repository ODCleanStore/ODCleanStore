package cz.cuni.mff.odcleanstore.conflictresolution.impl.util;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import org.junit.Test;
import org.openrdf.model.Statement;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class GrowingStatementArrayTest {
    @Test
    public void growsWhenInitialSizeExceeded() throws Exception {
        // Arrange
        final int count = 5;
        GrowingStatementArray growingArray = new GrowingStatementArray(2);
        ArrayList<Statement> statements = new ArrayList<Statement>();
        for (int i = 0; i < count; i++) {
            statements.add(CRTestUtils.createStatement());
        }

        // Act
        for (int i = 0; i < count; i++) {
            growingArray.add(statements.get(i));
        }

        // Assert
        assertThat(growingArray.size(), equalTo(5));
        for (int i = 0; i < 5; i++) {
            assertThat(growingArray.getArray()[i], equalTo(statements.get(i)));
        }
    }
}