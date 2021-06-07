package pbl4server.api.db.session;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import pbl4server.api.GlobalUtils;
import pbl4server.api.db.connection.Connector;

public class SessionHandler {
	
	private static final String PROPERTIES_FILE = "config/session.properties";
	private static final String NEW_SESSION_STATEMENT;
	private static final String CHECK_SESSION;
	private static final String RENEW_SESSION;
	private static final Integer HASH_LENGTH;
	
	static {
		Properties sessionProps = GlobalUtils.loadPropertiesFile(PROPERTIES_FILE);
		NEW_SESSION_STATEMENT = sessionProps.getProperty("new_session_statement");
		CHECK_SESSION = sessionProps.getProperty("check_session");
		RENEW_SESSION = sessionProps.getProperty("renew_session");
		HASH_LENGTH = Integer.valueOf(sessionProps.getProperty("hash_lenght"));
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
	
	public static Boolean checkSession(String session) {
		Boolean validSession = false;
		try {
			Connection connection = Connector.getConnection();
			PreparedStatement pStatement = connection.prepareStatement(CHECK_SESSION);
			pStatement.setString(1, session);
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

	public static Boolean renewSession(String session) {
		Boolean renewed = false;
		try {
			Connection connection = Connector.getConnection();
			PreparedStatement pStatement = connection.prepareStatement(RENEW_SESSION);
			pStatement.setString(1, session);
			renewed = pStatement.execute();
			return renewed;

		} catch (Exception e) {
			e.printStackTrace();
			return renewed;
		}
	}

}
