package pbl4server.api.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Card {
	
	public static final String REGISTER_CARD = "INSERT INTO registros VALUES (?, NOW(), ?);";
	public static final String CHECK_PERMISSION = "SELECT * FROM permisos WHERE sala_id = ? AND user_id = (SELECT usuario_id FROM tarjetas WHERE tarjeta_id = ? );";
	
	public static void registerCard(String uid, Boolean accepted) {
		try {
			Connection conn =  Connector.getConnection();
			PreparedStatement pStatement = conn.prepareStatement(REGISTER_CARD);
			pStatement.setString(0, uid);
			pStatement.setBoolean(1, accepted);
			pStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static Boolean checkCard(String uid, Integer sala_id) {
		try {
			Connection conn =  Connector.getConnection();
			PreparedStatement pStatement = conn.prepareStatement(CHECK_PERMISSION);
			pStatement.setInt(0,  sala_id);
			pStatement.setString(1, uid);
			ResultSet rSet = pStatement.executeQuery();
			if (rSet.next()) {
				return true;
			}
			return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
