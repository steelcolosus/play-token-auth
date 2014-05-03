package controllers;

import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import security.TokenAuth;
import security.TokenAuthAction;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class SecurityController extends Controller /*extends Action.Simple*/
{

	

	// returns an authToken
	public static Result login()
	{
		Form<Login> loginForm = Form.form(Login.class).bindFromRequest();

		if (loginForm.hasErrors())
		{
			return badRequest(loginForm.errorsAsJson());
		}

		Login login = loginForm.get();

		User user = User.findByEmailAddressAndPassword(login.emailAddress,
				login.password);

		if (user == null)
		{
			return unauthorized();
		}
		else
		{
			String authToken = user.createToken();
			ObjectNode authTokenJson = Json.newObject();
			authTokenJson.put("user", user.fullName);
			authTokenJson.put(TokenAuthAction.AUTH_TOKEN, authToken);
			response().setCookie(TokenAuthAction.AUTH_TOKEN, authToken);
			return ok(authTokenJson);
		}
	}

	@TokenAuth
	public static Result logout()
	{
		response().discardCookie(TokenAuthAction.AUTH_TOKEN);
		TokenAuthAction.getUser().deleteAuthToken();
		return redirect("/");
	}

	public static class Login
	{

		@Constraints.Required
		@Constraints.Email
		public String emailAddress;

		@Constraints.Required
		public String password;

	}

}
