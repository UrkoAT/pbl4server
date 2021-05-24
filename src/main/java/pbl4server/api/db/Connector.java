package pbl4server.api.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connector {
	private static final String URL = "servkolay.ddns.net";
	private static final String PORT = "5432";
	private static final String DB_NAME = "PBL4";
	private static final String DB_PROTOCOL = "postgresql";
	private static final String USER = "admin1";
	private static final String DB_ENGINE = "jdbc:"+DB_PROTOCOL+"://"+URL+":"+PORT+"/"+DB_NAME;
	private static final String TEST_STATEMENT = "SELECT * FROM public.users WHERE 1 = 0";
	private static final String CREDENTIALS_FILE = "credentials.pem";

	static Connection connection = null;
	static String password = null;
	
	private static String leerCredenciales() {
		String password = null;
		try (BufferedReader lector = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
			String linea;
			while ((linea = lector.readLine()) != null) {
				password = linea; 
			}
		}catch (Exception e) {
			e.printStackTrace();
			}
		return password;
	}
	
	public static Connection getConnection() throws SQLException {
		if (password == null) {
			password =  leerCredenciales();
		}
		if (connection == null) {
			connection = DriverManager.getConnection(DB_ENGINE, USER, password);
		}
		try {
			Statement statement = connection.createStatement();
			statement.executeQuery(TEST_STATEMENT);
		} catch (Exception e) {
			connection = DriverManager.getConnection(DB_ENGINE, USER, password);
		}
		return connection;
	}
	
}
