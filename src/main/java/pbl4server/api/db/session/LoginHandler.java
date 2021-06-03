package pbl4server.api.db.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.json.JSONObject;

import pbl4server.api.GlobalUtils;
import pbl4server.api.db.connection.Connector;
import spark.Request;
import spark.Response;

public class LoginHandler {
	private static final String PROPERTIES_FILE = "config/login.properties";
	private static final String LOGIN_STATEMENT;

	static {
		Properties loginProps = GlobalUtils.loadPropertiesFile(PROPERTIES_FILE);
		LOGIN_STATEMENT = loginProps.getProperty("login_statement");

	}

	public static String checkLogin(Request req, Response res) {
		Boolean success = false;
		JSONObject resJSON = new JSONObject();
		try {
			JSONObject reqJSON = new JSONObject(req.body());
			String username = reqJSON.getString("user");
			String password = reqJSON.getString("pass");
			Integer login_id = loginDB(username, password);

			if (login_id != -1) {
				String session = SessionHandler.newSession(login_id);
				if (session != null) {
					resJSON.put("user_id", login_id);
					resJSON.put("session", session);
					success = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resJSON.put("login", success ? "correct" : "invalid");
		return resJSON.toString();
	}

	private static Integer loginDB(String username, String password) {
		Integer id = -1;
		try {
			Connection connection = Connector.getConnection();
			PreparedStatement pStatement = connection.prepareStatement(LOGIN_STATEMENT);
			pStatement.setString(1, username);
			pStatement.setString(2, password);
			ResultSet rSet = pStatement.executeQuery();
			if (rSet.next()) {
				id = rSet.getInt("trabajador_id");
			}
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

}
