package pbl4server.api.db.models;

import org.json.JSONObject;

import spark.Request;
import spark.Response;

public class UserHandler {

	public static String getUser(Request req, Response res) {
		Boolean success = false;
		JSONObject resJSON = new JSONObject();
		try {
			JSONObject reqJSON = new JSONObject(req.body());
			String session = reqJSON.getString("session");
			Integer user_id = reqJSON.getInt("user_id");
			String requested_id = reqJSON.getString("requested_id");
			
			
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resJSON.put("status", success ? "success" : "error");
		return resJSON.toString();
	}
	
}
