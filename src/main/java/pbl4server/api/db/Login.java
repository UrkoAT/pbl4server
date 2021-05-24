package pbl4server.api.db;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONObject;

import spark.Request;
import spark.Response;

public class Login {

	private static final String LOGIN_STATEMENT = "SELECT trabajador_id FROM trabajadores WHERE usuario = ? AND password = ? LIMIT 1";
	private static final String NEW_SESSION_STATEMENT = "INSERT INTO sessions (session_id, user_id, timestamp) VALUES (?, ?, NOW()) RETURNING session_id";
	private static final String CHECK_SESSION = "SELECT * FROM sessions WHERE session_id = ? AND user_id = ?";
	private static final Integer HASH_LENGTH = 32;	

	public static String checkLogin(Request req, Response res) {
		Boolean success = false;
		JSONObject resJSON = new JSONObject();
		try {
			JSONObject reqJSON = new JSONObject(req.body());
			String username = reqJSON.getString("username");
			String password = reqJSON.getString("password");
			Integer login_id = loginDB(username, password);

			if (login_id != -1) {
				String session = newSession(login_id);
				if (session != null) {
					success = true;
					resJSON.put("session", session);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resJSON.put("success", success);
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

}
