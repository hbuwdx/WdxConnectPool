package hbu.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionPool{
	public int minNum=10;
	public int maxNum=20;
	
	private boolean isStarted=false;
	
	private List<MyConnect> connections=new ArrayList<MyConnect>();
	private List<MyConnect> usingConnections=new ArrayList<MyConnect>();
	
	
	private Timer timer;
	
	private String drivername;
	private String connurl;
	private String username;
	private String password;
	
	public static ConnectionPool instance=null;
	public static ConnectionPool getInstance(String drivername, String connurl, String username,
			String password){
		if(null==instance){
			instance=new ConnectionPool(drivername,connurl,username,password);
		}
		return instance;
	}
	private ConnectionPool(String drivername, String connurl, String username,
			String password) {
		super();
		this.drivername = drivername;
		this.connurl = connurl;
		this.username = username;
		this.password = password;
		start();
	}

	public void start(){
		System.out.println("pool start...");
		Connection connection;
		for(int i=0;i<minNum;i++){
			createConnection();
		}
		isStarted=true;
		timer=new Timer();
		timer.schedule(new MyTimeTask(), 5*1000);
	}
	class MyTimeTask extends TimerTask{
		@Override
		public void run() {
			while(isStarted){
				synchronized (connections) {
					int duoyu=getConnectionsCount()-minNum;
					System.out.println(duoyu+"个conn清除");
					for(int i=0;i<duoyu;i++){
						connections.remove(0);
					}
				}
				try {
					Thread.sleep(5*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	public MyConnect newConnection(int clientId){
		MyConnect connectionn=null;
		if(isStarted){
			synchronized (connections) {
				if(getAvaliableCount()==0){
					if(getConnectionsCount()>maxNum){
						System.out.println("连接池已满请等待....");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						connectionn=newConnection(clientId);
						return connectionn;
					}
					createConnection();
				}
				connectionn=getANotUsedConnection();
				connectionn.setStatus(MyConnect.Status_Using);
				connectionn.setClientId(clientId);
				synchronized (usingConnections) {
					usingConnections.add(connectionn);
				}
			}
		}else{
			System.out.println("连接池已经关闭...");
		}
		return connectionn;
	}
	
	public void freeConnection(MyConnect connect){
		connect.setClientId(0);
		connect.setStatus(MyConnect.Status_NotUsed);
		synchronized (usingConnections) {
			usingConnections.remove(connect);
		}
	}
	
	
	public void stop(){
		for(MyConnect connect:connections){
			try {
				connect.getConnection().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		connections.removeAll(connections);
		usingConnections.removeAll(usingConnections);
		connections=null;
		usingConnections=null;
		isStarted=false;
		System.out.println("pool stop...");
	}
	
	public void  createConnection(){
		try {
			Class.forName(drivername);
			Connection connection=DriverManager.getConnection(connurl,username,password);
			
			if(null!=connection){
				System.out.println("new connection add ...");
				connections.add(new MyConnect(connection));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getConnectionsCount(){
		return connections.size();
	}
	public int getAvaliableCount(){
		int i=0;
		for(MyConnect connect:connections){
			if(connect.getStatus()==MyConnect.Status_NotUsed){
				i++;
			}
		}
		return i;
	}
	public int getUsingCount(){
		return usingConnections.size();
	}
	public MyConnect getANotUsedConnection(){
		MyConnect connection=null;
		for(MyConnect connect:connections){
			if(connect.getStatus()==MyConnect.Status_NotUsed){
				connection=connect;
				break;
			}
		}
		return connection;
	}
}
