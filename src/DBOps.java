import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.math.BigDecimal;
import java.sql.ResultSet;

public class DBOps{


    public static int logIn(Connection conn, String un, String pw) throws SQLException {
        String sql = "SELECT * FROM STAFF WHERE username=? AND password=SHA(?)";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, un);
        statement.setString(2, pw);
        ResultSet result = statement.executeQuery();

        if(result.first()) {
            String fn = result.getString(5);
            System.out.println();
            System.out.println("Welcome, "+fn+"!");
            return result.getInt(7);
        }
        return -1;

    }


    public static int getBranchNo(Connection conn, String un) throws SQLException {
        String sql = "SELECT * FROM STAFF WHERE username=?";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, un);
        ResultSet result = statement.executeQuery();

        if(result.first()) {
            return result.getInt(2);
        }
        return -1;
    }


    public static void addItem(Connection conn, int SKU, String artist, String title, String genre, BigDecimal price, int quantity, int branchNo) throws SQLException {
        String sql = "INSERT INTO Item Values(?,?,?,?,?)";
        String sql2 = " INSERT INTO Inventory VALUES(?,?,?)";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, SKU);
        statement.setString(2, artist);
        statement.setString(3, title);
        statement.setString(4, genre);
        statement.setBigDecimal(5, price);

        PreparedStatement statement2 = conn.prepareStatement(sql2);
        statement2.setInt(1, branchNo);
        statement2.setInt(2, SKU);
        statement2.setInt(3, quantity);


        int rowsAffected = statement.executeUpdate();
        statement2.executeUpdate();
        if (rowsAffected > 0) 
            System.out.println("Item was inserted successfully!");
        else
            System.out.println("No items added. Either SKU already exists, or input error.");
    }


    public static void displayInventory(Connection conn, String genre, int branchNo) throws SQLException {
        String sql = "SELECT * FROM inventoryQuantity WHERE genre LIKE? AND branchNo=?";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, "%" + genre + "%");
        statement.setInt(2, branchNo);

        ResultSet result = statement.executeQuery();
        if (!result.first()) { // no match for genre, display all
            sql = "SELECT * FROM inventoryQuantity WHERE branchNo=?";
            statement = conn.prepareStatement(sql);
            statement.setInt(1, branchNo);
            result = statement.executeQuery();
        }
        
        result.previous();
        while (result.next()){
            String SKU = result.getString(1);
            String artist = result.getString(2);
            String title = result.getString(3);
            genre = result.getString(4); // already defined
            BigDecimal price = result.getBigDecimal(5);
            int quantity = result.getInt(6);

            System.out.println("SKU: "+SKU+" - "+artist+" - "
            +title+" - "+genre+" -  $"+price+" - ("+quantity+")");
        }
    }


    public static void adjustQuantity(Connection conn, int SKU, int quantity, int branchNo) throws SQLException {
        String sql = "UPDATE Inventory SET quantity=? WHERE SKU=? AND branchNo=?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, quantity);
        statement.setInt(2, SKU);
        statement.setInt(3, branchNo);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) 
            System.out.println("Successfully updated quantity of " + SKU + " to " + quantity);
        else
            System.out.println("Error when adjusting quantity of " + SKU + ". Try again.");
    }


    public static void searchByArtist(Connection conn, String artist, int branchNo) throws SQLException {
        String sql = "SELECT * FROM inventoryQuantity WHERE artist LIKE ? AND branchNo=?";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1,"%" + artist +"%");
        statement.setInt(2,branchNo);
        ResultSet result = statement.executeQuery();
        if (result.first()) {
            result.previous();
            while (result.next()){
                String SKU = result.getString(1);
                artist = result.getString(2); // already defined
                String title = result.getString(3);
                String genre = result.getString(4);
                BigDecimal price = result.getBigDecimal(5);
                int quantity = result.getInt(6);
    
                System.out.println("SKU: "+SKU+" - "+artist+" - "
                +title+" - "+genre+" -  $"+price+" - ("+quantity+")");
            }
        }
        else {
            System.out.println("No matches found for " + artist);
        }
    }


    public static int getTransactionNumber(Connection conn, int branchNo) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM Transaction WHERE branchNo=?");
        statement.setInt(1, branchNo);
        ResultSet result = statement.executeQuery();
        if(result.last()) {
            return result.getInt(1) + 1;
        }
        else {
            return 1;
        }
    }


    public static void createTransaction(Connection conn, int transNum, int SKU, int quantity, int branchNo) throws SQLException {
        String sql = "INSERT INTO Transaction Values(?,?,?,?, NOW())";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, transNum);
        statement.setInt(2, branchNo);
        statement.setInt(3, SKU);
        statement.setInt(4, quantity);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) 
            System.out.println("(" + quantity + ") " + SKU + " added to transaction " + transNum);
        else
            System.out.println("Error when adding " + SKU + ". Try again.");
    }


    public static void recentTransactions(Connection conn, int limit, int branchNo) throws SQLException {
        String sql = "SELECT * FROM fullTransaction WHERE branchNo=?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, branchNo);
        ResultSet result = statement.executeQuery();
        result.last();
        int trans = result.getInt(1);
        result.next();
        BigDecimal total = new BigDecimal("0.00");
        System.out.println();
        while(limit > 0 && result.previous()) {
            if(trans != result.getInt(1)) {
                System.out.println("TRANSACTION " + trans);
                System.out.println("Total: $" + total);
                System.out.println(result.getTimestamp(7));
                System.out.println();
                System.out.println();
                trans = result.getInt(1);
                total = new BigDecimal("0.00");
                limit--;
            }

            total = total.add(result.getBigDecimal(6));
            System.out.print(result.getString(3) + " - ");
            System.out.print(result.getString(5) + " $");
            System.out.print(result.getBigDecimal(6) + " (");
            System.out.println(result.getInt(4) + ")");
        }
        if(!result.previous())
            result.next();

        System.out.println("TRANSACTION " + trans);
        System.out.println("Total: $" + total);
        System.out.println(result.getTimestamp(7));
        System.out.println();
        System.out.println();
    }

    public static void addUser(Connection conn, int branchNo, String username, String password, String firstName, String lastName, int adminRights) throws SQLException {
        String sql = "INSERT Staff (branchNo, username, password, firstName, lastName, adminRights) Values(?,?,?,?,?,?)";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, branchNo);
        statement.setString(2, username);
        statement.setString(3, password);
        statement.setString(4, firstName);
        statement.setString(5, lastName);
        statement.setInt(6, adminRights);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) 
            System.out.println(firstName + " was added successfully!");
        else
            System.out.println("Error when adding " + firstName);
    }
}