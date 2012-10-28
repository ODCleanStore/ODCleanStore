package cz.cuni.mff.odcleanstore.webfrontend.validators;

import cz.cuni.mff.odcleanstore.webfrontend.util.PasswordHandling;

import java.security.NoSuchAlgorithmException;

import org.apache.wicket.validation.IValidatable;

/**
 * Ensures that the a component only accepts the correct password
 * of the currently logged-in user.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OldPasswordValidator extends CustomValidator
{
	private static final long serialVersionUID = 1L;
	
	private String passwordHash;
	private String salt;
	
	/**
	 * 
	 * @param userPassword
	 */
	public OldPasswordValidator(String passwordHash, String salt)
	{
		this.passwordHash = passwordHash;
		this.salt = salt;
	}
	
	@Override
	public void validate(IValidatable<String> validatable) 
	{
		String oldPasswordValue = validatable.getValue();
		
		if (!passwordIsValid(oldPasswordValue, salt, passwordHash))
			handleError(validatable, "invalid-old-password");
	}
	
	/**
	 * Returns true if and only if the given password equals the given original
	 * password.
	 * 
	 * @param password
	 * @param salt
	 * @param originalHash
	 * @return
	 */
	private boolean passwordIsValid(String password, String salt, String originalHash)
	{
		String passwordHash;
		
		try 
		{
			passwordHash = PasswordHandling.calculatePasswordHash(password, salt);
		}
		catch (NoSuchAlgorithmException ex) 
		{
			return false;
		}
		
		return passwordHash.equals(originalHash);
	}
}
