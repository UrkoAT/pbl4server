package pbl4server.api.db;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.json.JSONObject;

import pbl4server.api.GlobalUtils;
import spark.Request;
import spark.Response;

public class LoginHandler {
	private static final String PROPERTIES_FILE = "config/login.properties";
	private static final String LOGIN_STATEMENT;
	private static final String NEW_SESSION_STATEMENT;
	private static final String CHECK_SESSION;
	private static final String RENEW_SESSION;
	private static final Integer HASH_LENGTH;

	static {
		Properties loginProps = GlobalUtils.loadPropertiesFile(PROPERTIES_FILE);
		LOGIN_STATEMENT = loginProps.getProperty("login_statement");
		NEW_SESSION_STATEMENT = loginProps.getProperty("new_session_statement");
		CHECK_SESSION = loginProps.getProperty("check_session");
		RENEW_SESSION = loginProps.getProperty("renew_session");
		HASH_LENGTH = Integer.valueOf(loginProps.getProperty("hash_lenght"));
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
				String session = newSession(login_id);
				if (session != null) {
					resJSON.put("user_id", login_id);
					resJSON.put("session", session);
					success = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resJSON.put("login", success?"correct":"invalid");
		return resJSON.toString();
	}

	public static String newSession(Integer id) {
		try {
			Connection connection = Connector.getConnection();
			PreparedStatement pStatement = connection.prepareStatement(NEW_SESSION_STATEMENT);
			String hash = generateHash(HASH_LENGTH);
			pStatement.setString(1, hash);
			pStatement.setInt(2, id);
			Boolean success = pStatement.execute();
			if (success) {
				return hash;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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

	public static Boolean checkSession(String session, Integer user_id) {
		Boolean validSession = false;
		try {
			Connection connection = Connector.getConnection();
			PreparedStatement pStatement = connection.prepareStatement(CHECK_SESSION);
			pStatement.setString(1, session);
			pStatement.setInt(2, user_id);
			ResultSet rSet = pStatement.executeQuery();
			validSession = rSet.next();
			return validSession;
		} catch (Exception e) {
			e.printStackTrace();
			return validSession;
		}
	}

	private static String generateHash(Integer len) {
		int leftLimit = 48; // '0'
		int rightLimit = 122; // 'z'
		SecureRandom random = new SecureRandom();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(len)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return generatedString;
	}

	public static Boolean renewSession(String session, Integer user_id) {
		Boolean renewed = false;
		try {
			Connection connection = Connector.getConnection();
			PreparedStatement pStatement = connection.prepareStatement(RENEW_SESSION);
			pStatement.setString(1, session);
			pStatement.setInt(2, user_id);
			renewed = pStatement.execute();
			return renewed;

		} catch (Exception e) {
			e.printStackTrace();
			return renewed;
		}
	}

}
