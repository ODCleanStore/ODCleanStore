package cz.cuni.mff.odcleanstore.webfrontend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Utility class to ease working with user-account passwords.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class PasswordHandling 
{
	/** the standard password/salt char-set */
	public static final String DEFAULT_CHARSET = 
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!?:";
	
	/** the standard password length */
	public static final int DEFAULT_PASSWORD_LENGTH = 12;
	
	/** the standard salt length */
	public static final int DEFAULT_SALT_LENGTH = 64;
	
	/**
	 * Generates a random string value of the given length, which consists
	 * of characters of the given char-set only.
	 * 
	 * @param charset
	 * @param length
	 * @return
	 */
	public static String generateRandomString(String charset, int length)
	{
		Random rnd = new Random();
		
		char[] password = new char[length];
		for (int i = 0; i < length; i++)
		{
			int charPos = rnd.nextInt(charset.length());
			password[i] = charset.charAt(charPos);
		}
		
		return new String(password);
	}
	
	/**
	 * Generates a random password (e.g. a random string value of
	 * the standard password-length which consists only of members of the
	 * standard char-set).
	 * 
	 * @return
	 */
	public static String generatePassword()
	{
		return PasswordHandling.generateRandomString(
			PasswordHandling.DEFAULT_CHARSET,
			PasswordHandling.DEFAULT_PASSWORD_LENGTH
		);
	}
	
	/**
	 * Generates a random salt (e.g. a random string value of the
	 * standard salt-length which consists only of members of the standard
	 * char-set).
	 * 
	 * @return
	 */
	public static String generateSalt()
	{
		return PasswordHandling.generateRandomString(
			PasswordHandling.DEFAULT_CHARSET,
			PasswordHandling.DEFAULT_SALT_LENGTH
		);
	}
	
	/**
	 * Returns the has of the given plain-text password and the given salt.
	 * 
	 * @param password
	 * @param salt
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String calculatePasswordHash(String password, String salt) 
		throws NoSuchAlgorithmException
	{
		try {
			return calculateHash(password + salt);
		}
		catch (NoSuchAlgorithmException ex)
		{
			throw new NoSuchAlgorithmException("Could not calculate password hash.");
		}
	}
	
	/**
	 * Calculates an MD5 hash of the given string value.
	 * 
	 * @param pattern
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String calculateHash(String pattern) throws NoSuchAlgorithmException
	{
		MessageDigest algorithm = MessageDigest.getInstance("MD5");
		
		algorithm.reset();
		algorithm.update(pattern.getBytes());
		
		byte[] hash = algorithm.digest();

		String result = "";	
		for (int i = 0; i < hash.length; i++)
		{
			String tmp = (Integer .toHexString(0xFF & hash[i]));
			if (tmp.length() == 1)
				result += "0" + tmp;
			else
				result += tmp;
		}
		
		return result;
	}
}
