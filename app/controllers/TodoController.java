package controllers;

import static play.libs.Json.toJson;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import security.TokenAuth;
import security.TokenAuthAction;

@TokenAuth
public class TodoController extends Controller
{
	public static Result getAllTodos()
	{
		return ok(toJson(models.Todo.findByUser(TokenAuthAction.getUser())));
	}

	public static Result createTodo()
	{
		Form<models.Todo> form = Form.form(models.Todo.class).bindFromRequest();
		if (form.hasErrors())
		{
			return badRequest(form.errorsAsJson());
		}
		else
		{
			models.Todo todo = form.get();
			todo.user = TokenAuthAction.getUser();
			todo.save();
			return ok(toJson(todo));
		}
	}
}
