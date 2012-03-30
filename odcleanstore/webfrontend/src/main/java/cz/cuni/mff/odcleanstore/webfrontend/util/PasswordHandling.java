package cz.cuni.mff.odcleanstore.webfrontend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordHandling 
{
	public static final String DEFAULT_CHARSET = 
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!?:";
	
	public static final int DEFAULT_LENGTH = 12;
	
	public static String generateRandomPassword(String charset, int length)
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
	
	public static String calculateMD5Hash(String password) throws NoSuchAlgorithmException
	{
		MessageDigest algorithm = MessageDigest.getInstance("MD5");
		
		algorithm.reset();
		algorithm.update(password.getBytes());
		
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
