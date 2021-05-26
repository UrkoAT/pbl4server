package pbl4server.api.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import pbl4server.api.GlobalUtils;

public class Connector {
	private static final String PROPERTIES_FILE = "config/connector.properties";
	private static final String URL;
	private static final String PORT;
	private static final String DB_NAME;
	private static final String DB_PROTOCOL;
	private static final String USER;
	private static final String DB_ENGINE;
	private static final String TEST_STATEMENT;
	private static final String CREDENTIALS_FILE;
	private static final String DB_CONNECTOR;
	
	static {
		Properties props = GlobalUtils.loadPropertiesFile(PROPERTIES_FILE);
		URL = props.getProperty("url");
		PORT = props.getProperty("port");
		DB_NAME = props.getProperty("db_name");
		DB_PROTOCOL = props.getProperty("db_protocol");
		USER = props.getProperty("user");
		DB_ENGINE = props.getProperty("db_engine");
		TEST_STATEMENT = props.getProperty("test_statement");
		CREDENTIALS_FILE = props.getProperty("credentials_file");
		DB_CONNECTOR = DB_ENGINE+":"+DB_PROTOCOL+"://"+URL+":"+PORT+"/"+DB_NAME;
	}

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
			connection = DriverManager.getConnection(DB_CONNECTOR, USER, password);
		}
		try {
			Statement statement = connection.createStatement();
			statement.executeQuery(TEST_STATEMENT);
		} catch (Exception e) {
			connection = DriverManager.getConnection(DB_CONNECTOR, USER, password);
		}
		return connection;
	}
	
}
