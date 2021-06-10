package pbl4server.api.request;

import static spark.Spark.before;
import static spark.Spark.after;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pbl4server.api.db.handlers.BuildingHandler;
import pbl4server.api.db.handlers.CardHandler;
import pbl4server.api.db.handlers.UserHandler;
import pbl4server.api.db.session.LoginHandler;
import pbl4server.api.db.session.SessionControl;

public class Server {

	public static void main(String[] args) {
		SessionControl sessionC = new SessionControl();
		sessionC.startTimer();
		port(8844);
		path("/api", () -> {

			before("/*", (req, res) -> {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date date = new Date();
				System.out.println("[" + dateFormat.format(date) + "] INFO Received API call" + req.pathInfo());
			});

			post("/login", (req, res) -> LoginHandler.checkLogin(req, res));
			get("/userById", (req, res) -> UserHandler.getUser(req, res));
			get("/buildings", (req, res) -> BuildingHandler.getBuildings(req, res));
			post("/insertBuilding", (req, res) -> BuildingHandler.insertBuilding(req, res));
			post("/updateBuilding", (req, res) -> BuildingHandler.updateBuilding(req, res));
			post("/checkPermission", (req, res) -> CardHandler.checkCard(req, res));
		});
	}
}
