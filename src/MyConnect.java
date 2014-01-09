package hbu.conn;

import java.sql.Connection;

public class MyConnect {
	public static final int Status_NotUsed=0;
	public static final int Status_Using=1;
	public static final int Status_Bad=2;
	
	public MyConnect(Connection connection) {
		super();
		this.connection = connection;
		this.status = Status_NotUsed;
	}
	
	private int clientId;
	private Connection connection;
	private int status;
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	
}
