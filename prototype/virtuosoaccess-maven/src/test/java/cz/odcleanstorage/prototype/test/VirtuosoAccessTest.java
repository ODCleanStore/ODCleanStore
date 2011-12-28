package cz.odcleanstorage.prototype.test;

import cz.odcleanstorage.prototype.utility.*;
import cz.odcleanstorage.prototype.virtuosoaccess.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Date;

/**
 * Tests the configuration validation and parsing.
 * 
 * @author Petr Jerman (petr.jerman@centrum.cz)
 */
public class VirtuosoAccessTest {
	
	private String uri;
	
	VirtuosoConnection con = null;
	
	@Before
	public void createInitialVirtuosoDbaConnection() throws ClassNotFoundException, SQLException
	{
		con = VirtuosoConnection.CreateInitialVirtuosoDbaConnection();
		assertNotNull(con);
	}
	
	@After
	public void close() throws ClassNotFoundException, SQLException
	{
		if(con != null) {
			con.close();
			con = null;
		}
	}
	
	@Test
	public void testDataFormat_getW3CDTF() {
		String stringDate = DataFormat.getW3CDTF(new Date());
		System.out.printf("\n---testDataFormat_getW3CDTF---\n");
		System.out.printf("current date in W3CDFT format is %s\n", stringDate);
	}
	
	@Test
	public void testDataFormat_generateOpenDataTripleGroupsRandomUuidUri()
	{
		String uri = DataFormat.generateOpenDataTripleGroupsRandomUuidUri();
		System.out.printf("\n---testDataFormat_generateOpenDataTripleGroupsRandomUuidUri---\n");
		System.out.printf("generated uri is %s\n", uri);
	}
	
	@Test
	public void testbasicReadQuads() throws ClassNotFoundException, SQLException
	{
		long count = con.readQuads("<>", new RowListener() { public void processRow(VirtuosoConnection con, String[] quad) {}});
		assertTrue(count == 0);
		
		con.readQuads("<graph>", new RowListener() { public void processRow(VirtuosoConnection con, String[] quad) {}});
	}
	
	@Test
	public void testInsertAndReadQuad() throws ClassNotFoundException, SQLException
	{
		uri = DataFormat.generateOpenDataTripleGroupsRandomUuidUri();
		
		String currentDate = DataFormat.getW3CDFTCurrent();
		con.insertQuad(uri, uri, currentDate, uri);
		long count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 1);
		
		// duplicate insert is ok
		con.insertQuad(uri, uri, currentDate, uri);
		count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 1);

		con.insertQuad("_:A", uri, DataFormat.getW3CDFTCurrent(), uri);
		count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 2);
		
		con.insertQuad("_:A", uri, "\"24-12-21^^xsd:Date\"", uri);
		
		try {
			con.insertQuad("", "", "", "");
			assertTrue(false);
		}
		catch(SQLException e) {
		}
		
		System.out.printf("\n--- testInsertAndReadQuad---\n");
		count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad)
		{
			assertTrue(quad.length == 4);
			for(int i=0; i<quad.length; i++) {
					System.out.printf(quad[i]);
					System.out.printf(" ");
				}
				System.out.printf("\n");
		}});
	}
	
	@Test
	public void testInsertAndDeleteQuad() throws ClassNotFoundException, SQLException
	{
		uri = DataFormat.generateOpenDataTripleGroupsRandomUuidUri();
		con.insertQuad(uri, uri, uri, uri);
		long count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 1);
		
		con.deleteQuads(uri, uri, uri, uri);
		count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 0);
	}
	
	@Test
	public void testRevertAndCommitQuad() throws ClassNotFoundException, SQLException
	{
		uri = DataFormat.generateOpenDataTripleGroupsRandomUuidUri();
		
		con.insertQuad(uri, uri, uri, uri);
		con.revert();
		long count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 0);
		
		con.insertQuad(uri, uri, uri, uri);
		count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 1);
		
		con.commit();
		con.revert();
		count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 1);
		
		con.deleteQuads(uri, uri, uri, uri);
		con.revert();
		count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 1);
		
		con.deleteQuads(uri, uri, uri, uri);
		con.commit();
		con.revert();
		count = con.readQuads(uri, new RowListener() { public void processRow(VirtuosoConnection con, String[] quad){}});
		assertTrue(count == 0);
	}
}