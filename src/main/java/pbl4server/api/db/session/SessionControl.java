package pbl4server.api.db.session;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.Timer;

import pbl4server.api.db.connection.Connector;

public class SessionControl implements ActionListener {

	private Timer timer;
	private static final int DELAY = 5000;
	private static final String SQL_SENTENCE = "DELETE FROM sessions WHERE timestamp < (NOW() - interval '30 m')";
	public static PreparedStatement statement;

	public SessionControl() {
		timer = new Timer(DELAY, this);
		statement = prepareStatement();
	}

	private PreparedStatement prepareStatement() {
		PreparedStatement pStatement = null;
		try {
			Connection conn = Connector.getConnection();
			 pStatement = conn.prepareStatement(SQL_SENTENCE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pStatement;
	}

	public void startTimer() {
		if (timer != null) {
			timer.start();
		} else {
			timer = new Timer(DELAY, this);
			timer.start();
		}
	}

	public void stopTimer() {
		timer.stop();
		timer = null;
	}

	public void restartTimer() {
		stopTimer();
		startTimer();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			statement.execute();
		} catch (SQLException e1) {
			statement  = prepareStatement();
			e1.printStackTrace();
		}
	}
}
