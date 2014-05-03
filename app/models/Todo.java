package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
public class Todo extends Model
{
	@Id
	public Long id;

	@Column(length = 1024, nullable = false)
	@Constraints.MaxLength(1024)
	@Constraints.Required
	public String value;

	@ManyToOne
	@JsonIgnore
	public User user;

	public Todo(User user, String value)
	{
		this.user = user;
		this.value = value;
	}

	// region Finder
	public static Finder<Long, Todo> finder = new Finder<Long, Todo>(
			Long.class, Todo.class);

	public static List<Todo> findByUser(User user)
	{
		return finder.where().eq("user", user).findList();
	}
	// endregion
}
