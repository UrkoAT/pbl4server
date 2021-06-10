package pbl4server.api.db.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import pbl4server.api.db.connection.Connector;
import pbl4server.api.db.session.SessionHandler;
import pbl4server.api.utils.GlobalUtils;
import spark.Request;
import spark.Response;

public class BuildingHandler {

	private static final String PROPERTIES_FILE = "config/buildings.properties";
	private static final String INSERT_STATEMENT;
	private static final String GET_STATEMENT;
	private static final String UPDATE_STATEMENT;

	static {
		Properties userProps = GlobalUtils.loadPropertiesFile(PROPERTIES_FILE);
		GET_STATEMENT = userProps.getProperty("get_buildings");
		INSERT_STATEMENT = userProps.getProperty("insert_building");
		UPDATE_STATEMENT = userProps.getProperty("update_building");
	}

	public static String getBuildings(Request req, Response res) {
		Boolean success = false;
		JSONObject resJSON = new JSONObject();
		try {
			String session = req.queryParams("session");
			if (!SessionHandler.checkSession(session))
				throw new Exception("Session not valid...");
			resJSON.put("buildings", getBuildingsDB());
			SessionHandler.renewSession(session);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resJSON.put("status", success ? "ok" : "error");
		return resJSON.toString();
	}

	public static String insertBuilding(Request req, Response res) {
		Boolean success = false;
		Integer id = -1;
		JSONObject resJSON = new JSONObject();
		JSONObject reqJSON = new JSONObject(req.body());
		try {
			String session = reqJSON.getString("session");
			if (!SessionHandler.checkSession(session))
				throw new Exception("Session not valid...");
			id = insertBuildingDB(reqJSON.getJSONObject("building"));
			SessionHandler.renewSession(session);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resJSON.put("building_id", id);
		resJSON.put("status", success ? "ok" : "error");
		return resJSON.toString();
	}
	
	public static String updateBuilding(Request req, Response res) {
		Boolean success = false;
		Boolean updated = true;
		JSONObject resJSON = new JSONObject();
		JSONObject reqJSON = new JSONObject(req.body());
		try {
			String session = reqJSON.getString("session");
			if (!SessionHandler.checkSession(session))
				throw new Exception("Session not valid...");
			updated = updateBuildingDB(reqJSON.getJSONObject("building"), reqJSON.getBoolean("disable"));
			SessionHandler.renewSession(session);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resJSON.put("updated", updated);
		resJSON.put("status", success ? "ok" : "error");
		return resJSON.toString();
	}
	

	private static Integer insertBuildingDB(JSONObject jsonObject) {
		Integer id = -1;
		try {
			Connection conn = Connector.getConnection();
			PreparedStatement pStatement = conn.prepareStatement(INSERT_STATEMENT);
			pStatement.setString(1, jsonObject.getString("name"));
			pStatement.setString(2, jsonObject.getString("postal_code"));
			ResultSet rSet = pStatement.executeQuery();
			if (rSet.next())
				id = rSet.getInt("edificio_id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	
	private static Boolean updateBuildingDB(JSONObject jsonObject, Boolean disable) {
		Boolean correct = false;
		try {
			Connection conn = Connector.getConnection();
			PreparedStatement pStatement = conn.prepareStatement(UPDATE_STATEMENT);
			pStatement.setString(1, jsonObject.getString("name"));
			pStatement.setString(2, jsonObject.getString("postal_code"));
			pStatement.setBoolean(3, !disable);
			pStatement.setInt(4, jsonObject.getInt("id"));
			correct = pStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return correct;
	}

	private static JSONArray getBuildingsDB() {
		JSONArray array = new JSONArray();
		try {
			Connection conn = Connector.getConnection();
			PreparedStatement pStatement = conn.prepareStatement(GET_STATEMENT);
			ResultSet rSet = pStatement.executeQuery();
			while (rSet.next()) {
				array.put(parseBuilding(rSet));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}

	public static JSONObject parseBuilding(ResultSet rSet) throws SQLException {
		JSONObject building = new JSONObject();
		building.put("building_id", rSet.getInt("edificio_id"));
		building.put("postal_code", rSet.getString("codigo_postal"));
		building.put("name", rSet.getString("nombre"));
		return building;
	}
}
