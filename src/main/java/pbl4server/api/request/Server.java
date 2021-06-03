package pbl4server.api.request;

import static spark.Spark.before;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pbl4server.api.db.session.LoginHandler;
import pbl4server.api.db.session.SessionControl;

public class Server {

	public static void main(String[] args) {
		SessionControl sessionC = new SessionControl();
		sessionC.startTimer();
		port(80);
		path("/api", () -> {

			before("/*", (req, res) -> {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date date = new Date();
			System.out.println("[" + dateFormat.format(date) + "] INFO Received API call" + req.pathInfo());
			});

			post("/login", (req, res) ->  LoginHandler.checkLogin(req, res));
		});
	}
}
