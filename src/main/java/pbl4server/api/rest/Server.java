package pbl4server.api.rest;

import static spark.Spark.before;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pbl4server.api.db.Login;

public class Server {

	public static void main(String[] args) {
		port(8844);
		path("/api", () -> {

			before("/*", (req, res) -> {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date date = new Date();
			System.out.println("[" + dateFormat.format(date) + "] INFO Received API call" + req.pathInfo());
			});

			post("/login", (req, res) ->  Login.checkLogin(req, res));
		});
	}
}
