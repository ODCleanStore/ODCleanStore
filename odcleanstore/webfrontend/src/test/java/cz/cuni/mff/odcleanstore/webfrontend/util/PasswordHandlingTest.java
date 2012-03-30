package cz.cuni.mff.odcleanstore.webfrontend.util;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class PasswordHandlingTest 
{
	@Test
	public void testMD5HashCalculation() throws NoSuchAlgorithmException
	{
		String[][] samples = 
		{
			{ "Alea iacta est", "95acbef5b1336566c600586708bb18b9" },
			{ "Scio me nihil scire", "85a6be35bdd35c093c6b1b16b638d331" },
			{ "Noli turbare circulos meos!", "ce8167d6ed649008af34591616491166" },
			{ "Memento mori", "158544a4e591677882683271bc0a292b" },
			{ "Spes non confunditur", "3ad6243309f324aae3a4c9af1fd71021" },
		};
		
		for (String[] sample : samples)
		{
			assertEquals(
				sample[1],
				PasswordHandling.calculateMD5Hash(sample[0])
			);
		}
	}
	
	@Test
	public void testPasswordGeneration() 
	{
		int numOfAttempts = 100;
		for (int attempt = 0; attempt < numOfAttempts; attempt++)
		{
			String password = PasswordHandling.generateRandomPassword(
				PasswordHandling.DEFAULT_CHARSET, 10
			);
			
			checkPasswordProperties(password, PasswordHandling.DEFAULT_CHARSET, 10);
		}
	}
	
	private void checkPasswordProperties(String password, String charset, int length)
	{
		// assert that the password is exactly length characters long
		assertEquals(length, password.length());
		
		// assert that the password consists of characters from charset only
		for (int i = 0; i < password.length(); i++)
		{
			char ch = password.charAt(i);
			assertTrue(charset.contains(ch + ""));
		}
	}
}
