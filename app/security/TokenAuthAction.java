package security;

import models.User;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;

public class TokenAuthAction extends Action.Simple
{
	public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
	public static final String AUTH_TOKEN = "authToken";

	@Override
	public Promise<SimpleResult> call(Context ctx) throws Throwable
	{
		User user = null;
		String[] authTokenHeaderValues = ctx.request().headers()
				.get(AUTH_TOKEN_HEADER);

		if ((authTokenHeaderValues != null)
				&& (authTokenHeaderValues.length == 1)
				&& (authTokenHeaderValues[0] != null))
		{

			user = models.User.findByAuthToken(authTokenHeaderValues[0]);
			if (user != null)
			{
				ctx.args.put("user", user);
				return delegate.call(ctx);
			}
		}

		return F.Promise.pure((SimpleResult) unauthorized("Unauthorized"));
	}

	public static User getUser()
	{
		return (User) Http.Context.current().args.get("user");
	}
}
