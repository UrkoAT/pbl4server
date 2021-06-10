package pbl4server.api.db.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import pbl4server.api.db.connection.Connector;
import spark.Request;
import spark.Response;

public class CardHandler {
	
	public static final String REGISTER_CARD = "INSERT INTO registros VALUES (?, NOW(), ?);";
	public static final String CHECK_PERMISSION = "SELECT * FROM permisos WHERE sala_id = ? AND user_id = (SELECT usuario_id FROM tarjetas WHERE tarjeta_id = ? );";
	
	public static void registerCardDB(String uid, Boolean accepted) {
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
	public static Boolean checkCardDB(String uid, Integer sala_id) {
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
	
	public static String checkCard(Request req, Response res) {
		Boolean success = false;
		Boolean ok = false;
		JSONObject reqJSON = new JSONObject(req.body());
		JSONObject resJSON = new JSONObject();
		try {
			String uid = reqJSON.getString("uid");
			Integer room = reqJSON.getInt("room");
			ok = checkCardDB(uid, room);
			registerCardDB(uid, ok);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resJSON.put("permission", ok?1:0);
		resJSON.put("status", success ? "ok" : "error");
		return resJSON.toString();
	}
}
