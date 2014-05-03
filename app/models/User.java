package models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;
import play.data.validation.Constraints;

@Entity
public class User extends Model
{

	/***
	 * Table id
	 */
	@Id
	public Long id;

	private String authToken;

	@Column(length = 256, unique = true, nullable = false)
	@Constraints.MaxLength(256)
	@Constraints.Required
	@Constraints.Email
	private String emailAddress;

	@Column(length = 64, nullable = false)
    private byte[] shaPassword;
	
	@Transient
	@Constraints.Required
	@Constraints.MinLength(6)
	@Constraints.MaxLength(256)
	@JsonIgnore
	private String password;

	@Column(length = 256, nullable = false)
	@Constraints.Required
	@Constraints.MinLength(2)
	@Constraints.MaxLength(256)
	public String fullName;

	@Column(nullable = false)
	public Date creationDate;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	@JsonIgnore
	public List<Todo> todos = new ArrayList<Todo>();

	/**
	 * Create a token for a logged in user
	 * 
	 * @return Token
	 * 
	 */
	public String createToken()
	{
		authToken = UUID.randomUUID().toString();
		this.save();
		return authToken;
	}

	/***
	 * delete auth token
	 */
	public void deleteAuthToken()
	{
		authToken = null;
		this.save();
	}

	public User()
	{
		this.creationDate = new Date();
	}

	public User(String emailAddress, String password, String fullName)
	{
		setEmailAddress(emailAddress);
		setPassword(password);
		this.fullName = fullName;
		this.creationDate = new Date();
	}

	// region methods
	public String getEmailAddres()
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password) {
        this.password = password;
        shaPassword = getSha512(password);
    }

	public static byte[] getSha512(String value)
	{
		try
		{
			return MessageDigest.getInstance("SHA-512").digest(
					value.getBytes("UTF-8"));
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	// endregion

	// region Finder methods
	public static Finder<Long, User> find = new Finder<Long, User>(Long.class,
			User.class);

	public static User findByAuthToken(String authToken)
	{
		System.out.println("searching user...");
		if (authToken == null)
		{
			System.out.println("null token");
			return null;
		}

		try
		{
			System.out.println("finding by token");
			return find.where().eq("authToken", authToken).findUnique();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static User findByEmailAddressAndPassword(String emailAddress,
			String password)
	{
		// todo: verify this query is correct. Does it need an "and" statement?
		return find.where().eq("emailAddress", emailAddress.toLowerCase())
				.eq("shaPassword", getSha512(password)).findUnique();
	}
	// endregion

}
