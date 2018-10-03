import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
* Make sure to replace your database name and user and password!!!!!!!!
*/
public class Connect {
 /**
 * This method returns a connection to your database
 */
 public static Connection getConnection() throws SQLException {
Connection conn = null;
try{
 String url = "jdbc:mysql://localhost:3306/db_artifact?serverTimezone=UTC&useSSL=false";
 
 // Insert user name and password here
 String user = "";
 String password = "";
 conn = DriverManager.getConnection(url, user, password);
}catch(SQLException e){
 e.printStackTrace();
}
 return conn;
 }
 public static void test(){
try {
 Connection conn = getConnection();
 if (conn!=null)
 System.out.println("Connection successful");
 conn.close();
}catch(SQLException e) {
 e.printStackTrace();
}
 }
 public static void main(String[] args) {
test();
 }
}
