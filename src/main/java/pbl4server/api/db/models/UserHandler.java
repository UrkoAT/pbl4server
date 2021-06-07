package pbl4server.api.db.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.CORBA.OBJ_ADAPTER;

import pbl4server.api.GlobalUtils;
import pbl4server.api.db.connection.Connector;
import pbl4server.api.db.session.SessionHandler;
import spark.Request;
import spark.Response;

public class UserHandler {
	
	
	private static final String PROPERTIES_FILE = "config/user.properties";
	private static final String GET_STATEMENT;
	private static final String GET_PERMS_STATEMENT;

	static {
		Properties userProps = GlobalUtils.loadPropertiesFile(PROPERTIES_FILE);
		GET_STATEMENT = userProps.getProperty("get_user");
		GET_PERMS_STATEMENT = userProps.getProperty("get_perms");
	}

	public static String getUser(Request req, Response res) {
		Boolean success = false;
		JSONObject resJSON = new JSONObject();
		try {
			JSONObject reqJSON = new JSONObject(req.body());
			String session = reqJSON.getString("session");
			
			if (!SessionHandler.checkSession(session)) throw new Exception("Session not valid...");
			Integer user_id = reqJSON.getInt("user_id");
			resJSON.put("user", getUserDB(user_id));
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resJSON.put("status", success ? "ok" : "error");
		return resJSON.toString();
	}
	
	private static JSONObject getUserDB(Integer user_id) {
		JSONObject jsonObject = new JSONObject();
		try {
			Connection conn = Connector.getConnection();
			PreparedStatement pStatement = conn.prepareStatement(GET_STATEMENT);
			pStatement.setInt(1, user_id);
			ResultSet rSet = pStatement.executeQuery();
			if (!rSet.next()) {
				JSONObject userJSON = new JSONObject();
				userJSON.put("user_id", rSet.getInt("trabajador_id"));
				userJSON.put("name", rSet.getString("nombre"));
				userJSON.put("surname", rSet.getString("apellido"));
				userJSON.put("postal_code", rSet.getString("codigo_postal"));
				userJSON.put("address", rSet.getString("direccion"));
				userJSON.put("prefix", rSet.getString("prefijo"));
				userJSON.put("telephone", rSet.getString("telefono"));
				userJSON.put("email", rSet.getString("email"));
				userJSON.put("department", rSet.getString("email"));
				userJSON.put("is_admin", rSet.getBoolean("admin"));
				pStatement = conn.prepareStatement(GET_PERMS_STATEMENT);
				pStatement.setInt(1, user_id);
				rSet = pStatement.executeQuery();
				JSONArray array = parsePermissions(rSet);
				userJSON.put("permissions", array);
				jsonObject.put("user", userJSON);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	private static JSONArray parsePermissions(ResultSet rSet) throws SQLException{
		JSONArray array = new JSONArray();
		while (rSet.next()) {
			JSONObject obj = new JSONObject();
			obj.put("description", rSet.getString("descripcion"));
			obj.put("floor", rSet.getString("piso"));
			obj.put("building", parseBuilding(rSet));
			obj.put("enabled", rSet.getString("enabled"));
			array.put(obj);
		}
		return array;
	}
	
	private static JSONObject parseBuilding(ResultSet rSet) throws SQLException {
		JSONObject building = new JSONObject();
		building.put("bulding_id", rSet.getInt("edificio_id"));
		building.put("postal_code", rSet.getString("codigo_postal"));
		building.put("name", rSet.getString("nombre"));
		return building;
	}
	
	
	
}
