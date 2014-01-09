package hbu.conn;

import java.sql.Statement;

public class ConnectionManager {
	public static void main(String[] args) {
		String drivername="net.sourceforge.jtds.jdbc.Driver";
		String connurl="jdbc:jtds:sqlserver://192.168.200.244:1433/superway";
		String username="sa";
		String password="123";
		
		final ConnectionPool pool=ConnectionPool.getInstance(drivername, connurl, username, password);
		
		for(int i=0;i<100;i++){
			final int a=i;
			new Thread(new Runnable(){
				public void run() {
					try {
						Thread.sleep(a*100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					MyConnect connection=pool.newConnection(2);
					if(connection==null){
						return;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					try {
						
						Statement stmt=connection.getConnection().createStatement();
						System.out.println("---------------");
						stmt.executeUpdate("insert into stu values(2,'王冬兴','男','河北省石家庄市')");
						pool.freeConnection(connection);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
				}
			}).start();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pool.stop();

		
	}
}
