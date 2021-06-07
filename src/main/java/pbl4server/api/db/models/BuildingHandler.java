package pbl4server.api.db.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

public class BuildingHandler {
	
	
	public static JSONObject parseBuilding(ResultSet rSet) throws SQLException {
		JSONObject building = new JSONObject();
		building.put("bulding_id", rSet.getInt("edificio_id"));
		building.put("postal_code", rSet.getString("codigo_postal"));
		building.put("name", rSet.getString("nombre"));
		return building;
	}

}
