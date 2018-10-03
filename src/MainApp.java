import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;

public class MainApp{

    public static void main(String[] args){

        Scanner scan = new Scanner(System.in);
        Boolean runApp = true;
        while(runApp) {

            Boolean loggedIn = false;
            int attempts = 0;
            String un = "";
            int adminRights = -1;
            int branchNo = -1;

            while(!loggedIn) {

                System.out.println("Please enter your username:");
                un = scan.nextLine();

                System.out.println("Please enter your password:");
                String pw = scan.nextLine();

                try {
                    Connection conn = Connect.getConnection();
                    adminRights = DBOps.logIn(conn, un, pw);
                    if(adminRights > -1) {
                        loggedIn = true;
                        branchNo = DBOps.getBranchNo(conn, un);
                    }
                    conn.close();
                }catch (SQLException e) {
                    e.printStackTrace();
                }    

                if(!loggedIn) {
                    System.out.println("Could not find match.");
                    System.out.println("Enter 0 to exit or any key to retry.");
                    String strInput = scan.nextLine();
                    if(isParsable(strInput)) {
                        int input = Integer.parseInt(strInput);
                        if(input == 0 || attempts > 5) {
                            scan.close();
                            System.exit(0);
                        }
                    }
                }
                attempts++;
            }

            while(loggedIn) {
                displayOptions(adminRights);
                String strInput = scan.nextLine();
                if(isParsable(strInput)) {
                    int input = Integer.parseInt(strInput);
                    switch(input) {
                        case 0: scan.close();
                                System.exit(0);
                                break;
                        case 1: userAddItem(scan, branchNo);
                                break;
                        case 2: userDisplayInventory(scan, branchNo);
                                break;
                        case 3: userAdjustQuantity(scan, branchNo);
                                break;
                        case 4: userSearchByArtist(scan, branchNo);
                                break;
                        case 5: userCreateTransaction(scan, branchNo);
                                break;
                        case 6: userRecentTransactions(scan, branchNo);
                                break;
                        case 7: loggedIn = false;
                                break;
                        case 8: if(adminRights > 0) {
                                    userAddUser(scan);         
                                }
                                break;
                        default: break;
                    }
                }
                else {
                    System.out.println("Did not recognize that command. Please enter number.");
                }
            }
        }
        scan.close();
    }   


    public static void displayOptions(int adminRights) {
    
        System.out.println();
        System.out.println("Enter one of the following numbers, or 0 to exit");
        System.out.println("1 : Add New Item");
        System.out.println("2 : Display Inventory");
        System.out.println("3 : Adjust Quantity of SKU");
        System.out.println("4 : Search by Artist");
        System.out.println("5 : Create Transaction");
        System.out.println("6 : View Recent Transactions");
        System.out.println("7 : Log out");
        if (adminRights == 1) {
            System.out.println("8 : Create new user");
        }
    }

    public static void userAddItem(Scanner scan, int branchNo) {

        Boolean isNumber = false;
        int failCount = 0;
        int SKU = -1;
        while(!isNumber && failCount < 5) {
            System.out.println("Enter SKU: (must be number)");
            String strSKU = scan.nextLine();
            if(isParsable(strSKU)) {
                isNumber = true;
                SKU = Integer.parseInt(strSKU);
            }
            else {
                failCount++;
                System.out.println(strSKU+" is not a number. Try again.");
            }
        }
        if(failCount >= 5) {
            return;
        }

        System.out.println("Enter artist name:");
        String artist = scan.nextLine();
        System.out.println("Enter title of record:");
        String title = scan.nextLine();
        System.out.println("Enter genre");
        String genre = scan.nextLine();
        System.out.println("Enter price; exclude $; two decimal points: e.g.  40.00 or 20.50");
        BigDecimal price = scan.nextBigDecimal();
        scan.nextLine();

        isNumber = false;
        failCount = 0;
        int quantity = 0;
        while(!isNumber && failCount < 5) {
            System.out.println("Enter quantity:");
            String strQuantity = scan.nextLine();
            if(isParsable(strQuantity)) {
                isNumber = true;
                quantity = Integer.parseInt(strQuantity);
            }
            else {
                failCount++;
                System.out.println(strQuantity+" is not a number. Try again.");
            }
        }
        if(failCount >= 5) {
            return;
        }

        try {
            Connection conn = Connect.getConnection();
            DBOps.addItem(conn, SKU, artist, title, genre, price, quantity, branchNo);
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void userDisplayInventory(Scanner scan, int branchNo) {
        System.out.println("Enter a genre to display, or any key for all:");
        String genre = scan.nextLine();

        try {
            Connection conn = Connect.getConnection();
            DBOps.displayInventory(conn, genre, branchNo);
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void userAdjustQuantity(Scanner scan, int branchNo) {
        Boolean isNumber = false;
        int failCount = 0;
        int SKU = -1;
        while(!isNumber && failCount < 5) {
            System.out.println("Enter SKU: (must be number)");
            String strSKU = scan.nextLine();
            if(isParsable(strSKU)) {
                isNumber = true;
                SKU = Integer.parseInt(strSKU);
            }
            else {
                failCount++;
                System.out.println(strSKU+" is not a number. Try again.");
            }
        }
        if(failCount >= 5)
            return;

        isNumber = false;
        failCount = 0;
        int quantity = 0;
        while(!isNumber && failCount < 5) {
            System.out.println("Enter NEW quantity:");
            String strQuantity = scan.nextLine();
            if(isParsable(strQuantity)) {
                isNumber = true;
                quantity = Integer.parseInt(strQuantity);
            }
            else {
                failCount++;
                System.out.println(strQuantity+" is not a number. Try again.");
            }
        }
        if(failCount >= 5)
            return;

        try {
            Connection conn = Connect.getConnection();
            DBOps.adjustQuantity(conn, SKU, quantity, branchNo);
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        
    }

    public static void userSearchByArtist(Scanner scan, int branchNo) {
        System.out.println("Enter the artist name:");
        String artist = scan.nextLine();

        try {
            Connection conn = Connect.getConnection();
            DBOps.searchByArtist(conn, artist, branchNo);
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void userCreateTransaction(Scanner scan, int branchNo) {

        int transNum = 0;
        try {
            Connection conn = Connect.getConnection();
            transNum = DBOps.getTransactionNumber(conn, branchNo);
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        Boolean moreItems = true;

        while(moreItems){
            Boolean isNumber = false;
            int failCount = 0;
            int SKU = -1;
            while(!isNumber && failCount < 5) {
                System.out.println("Enter SKU: (must be number)");
                String strSKU = scan.nextLine();
                if(isParsable(strSKU)) {
                    isNumber = true;
                    SKU = Integer.parseInt(strSKU);
                }
                else {
                    failCount++;
                    System.out.println(strSKU+" is not a number. Try again.");
                }
            }
            if(failCount >= 5)
                return;

            isNumber = false;
            failCount = 0;
            int quantity = 0;
            while(!isNumber && failCount < 5) {
                System.out.println("Enter quantity:");
                String strQuantity = scan.nextLine();
                if(isParsable(strQuantity)) {
                    isNumber = true;
                    quantity = Integer.parseInt(strQuantity);
                }
                else {
                    failCount++;
                    System.out.println(strQuantity+" is not a number. Try again.");
                }
            }
            if(failCount >= 5)
                return;

            try {
                Connection conn = Connect.getConnection();
                DBOps.createTransaction(conn, transNum, SKU, quantity, branchNo);
                conn.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println("Enter 0 to complete transaction or any key to continue.");
            String strInput = scan.nextLine();
            if(isParsable(strInput)) {
                int input = Integer.parseInt(strInput);
                if(input == 0) {
                    moreItems = false;
                }
            }
        }
    }

    public static void userRecentTransactions(Scanner scan, int branchNo) {
        Boolean isNumber = false;
            int failCount = 0;
            int limit = 0;
            while(!isNumber && failCount < 5) {
                System.out.println("Display last 'x' transactions. Choose number.");
                String strInput = scan.nextLine();
                if(isParsable(strInput)) {
                    limit = Integer.parseInt(strInput);
                    if(limit > 0)
                        isNumber = true;
                    else
                        System.out.println("Number must be greater than 0.");
                }
                else {
                    failCount++;
                    System.out.println(strInput+" is not a number. Try again.");
                }
            }

            try {
                Connection conn = Connect.getConnection();
                DBOps.recentTransactions(conn, limit, branchNo);
                conn.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static void userAddUser(Scanner scan) {
        Boolean isNumber = false;
        int failCount = 0;
        int branchNo = -1;
        while(!isNumber && failCount < 5) {
            System.out.println("Enter branch number: (currently just 1 or 2)");
            String strBranchNo = scan.nextLine();
            if(isParsable(strBranchNo)) {
                isNumber = true;
                branchNo = Integer.parseInt(strBranchNo);
            }
            else {
                failCount++;
                System.out.println(strBranchNo+" is not a number. Try again.");
            }
        }
        if(failCount >= 5) {
            return;
        }

        System.out.println("Enter username:");
        String username = scan.nextLine();
        System.out.println("Enter password:");
        String password = scan.nextLine();
        System.out.println("Enter first name");
        String firstName = scan.nextLine();
        System.out.println("Enter last name");
        String lastName = scan.nextLine();

        isNumber = false;
        failCount = 0;
        int adminRights = 0;
        while(!isNumber && failCount < 5) {
            System.out.println("If admin, enter 1. Otherwise, enter 0:");
            String strAdmin = scan.nextLine();
            if(isParsable(strAdmin)) {
                isNumber = true;
                adminRights = Integer.parseInt(strAdmin);
            }
            else {
                failCount++;
                System.out.println(strAdmin+" is not a number. Try again.");
            }
        }

        try {
            Connection conn = Connect.getConnection();
            DBOps.addUser(conn, branchNo, username, password, firstName, lastName, adminRights);
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean isParsable(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }
}