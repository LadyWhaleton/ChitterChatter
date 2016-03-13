/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Messenger (String dbname, String dbport, String user, String passwd) throws SQLException {

      //System.out.print("\tConnecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("\t\t\tDone\n\n");
      }catch (Exception e){
         System.err.println("\t\tError - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("\t\tMake sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Messenger

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
   if(outputHeader){
      for(int i = 1; i <= numCol; i++){
    System.out.print(rsmd.getColumnName(i) + "\t");
      }
      System.out.println();
      outputHeader = false;
   }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
      // creates a statement object 
      Statement stmt = this._connection.createStatement (); 
 
      // issues the query instruction 
      ResultSet rs = stmt.executeQuery (query); 
 
      /* 
       ** obtains the metadata object for the returned result set.  The metadata 
       ** contains row and column info. 
       */ 
      ResultSetMetaData rsmd = rs.getMetaData (); 
      int numCol = rsmd.getColumnCount (); 
      int rowCount = 0; 
 
      // iterates through the result set and saves the data returned by the query. 
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>(); 
      while (rs.next()){
          List<String> record = new ArrayList<String>(); 
         for (int i=1; i<=numCol; ++i) 
            record.add(rs.getString (i)); 
         result.add(record); 
      }//end while 
      stmt.close (); 
      return result; 
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
  Statement stmt = this._connection.createStatement ();
  
  ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
  if (rs.next())
    return rs.getInt(1);
  return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup


  public static void printLogo()
  {
    String backslash = "\\";
    String backslash4 = backslash + backslash + backslash + backslash;
    System.out.println("\n-------|--------------------------------------------------|---------");
    System.out.println("    ___|___                                            ___|___");
    System.out.println("   ////////\\   _                                  _   /" + backslash4 + backslash4);
    System.out.println("  ////////  \\ ('<        Chitter Chatter         >') /  " + backslash4 + backslash4);
    System.out.println("  | (_)  |  | (^)   David Ding, Stephanie Tong   (^) |  | (_)  |");
    System.out.println("  |______|./==''==                              ==''===.|______|");
    System.out.println("--------------------------------------------------------------------");
  }
   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Messenger.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      Messenger esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Messenger (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            printLogo();
            System.out.println("\n\n\t===================================");
            System.out.println("\t\tMAIN MENU");
            System.out.println("\t===================================");
            System.out.println("\t1. Login");
            System.out.println("\t2. Create a New Account");
            System.out.println("\t===================================");
            System.out.println("\t9. < EXIT");
            String authorisedUser = null;

            switch (readChoice())
            {
               case 1: authorisedUser = LogIn(esql); break;
               case 2: CreateUser(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            } //end switch

            if (authorisedUser != null) 
            {
              boolean usermenu = true;
              while(usermenu) {
                printLogo();
                System.out.println("\n\n\tYou are logged in as " + authorisedUser + ".");
                System.out.println("\t===================================");
                System.out.println("\t\tMAIN MENU");
                System.out.println("\t===================================");
                System.out.println("\t1. Show Chat Interface");
                System.out.println("\t2. Show Contacts");
                System.out.println("\t3. Show Blocked List");
                System.out.println("\t4. Add a New Contact");
                System.out.println("\t5. Remove a Contact");
                System.out.println("\t6. Block a User");
                System.out.println("\t7. Unblock a User");
                System.out.println("\t-----------------------------------");
                System.out.println("\t8. Delete Account");
                System.out.println("\t===================================");
                System.out.println("\t9. Log out");
                switch (readChoice()){
                   case 1: ShowChatInterface(esql, authorisedUser); break;
                   case 2: ListContacts(esql,authorisedUser); break;
                   case 3: ListBlocks(esql, authorisedUser); break;
                   case 4: AddToContact(esql,authorisedUser); break;
                   case 5: RemoveContact(esql,authorisedUser); break;
                   case 6: AddToBlock(esql, authorisedUser); break;
                   case 7: UnblockUser(esql, authorisedUser); break;
                   case 8: usermenu = DeleteAccount(esql, authorisedUser); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Invalid selection!"); break;
                }
              }
            }
         }//end while

      }

      catch(Exception e) 
      {
         System.err.println (e.getMessage ());
      }

      finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("\n\tDisconnecting from database...");
               esql.cleanup ();
               System.out.println("\tDone.\n\n\tBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
  
   public static void Greeting(){
      System.out.println(
         "\n\n\t********************************************\n" +
         "\t\t  Connecting to Database...\n" +
         "\t********************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("\nPlease make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("\nYour input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

  // Steph's Note: At the start of each Menu Option function, call this function and pass in the name of the Menu Option.
  // For example: DisplayMmenuTitle("Your Chats")
  public static void DisplayMenuTitle(String title)
  {
    String formattedTitle = "\n\n======================" + title + "======================\n\n";
    System.out.print(formattedTitle);
  }

  // Steph's Note: At the end of each Menu Option function, call this function and pass in the name of the Menu Option.
  // This function, then takes the lenth of the title and appends '=' accordingly. Don't worry too much about the logic,
  // just use it. :)
  public static void DisplayEndTitle(String title)
  {
    int sz = title.length();
    String border = "\n======================" + "======================";

    for (int i = 0; i < sz; ++i )
      border += "=";

    border += "\n\n";

    System.out.print(border);
  }

  public static String checkValidLogin(String login)
  {
    if (login.equals(""))
      return "\n\tError: Login cannot be empty!";

    else if (login.equals("q"))
      return "\n\tError: 'q' cannot be used as a login!";

    else if (login.equals('Q'))
      return "\n\tError: 'Q' cannot be used as a login!";

    else if (login.equals("done"))
      return "\n\tError: 'done' cannot be used as a login!";

    else if (Character.isDigit(login.charAt(0)))
      return "\n\tError: Login cannot start with a digit!";

    else 
      return "ok";
  }

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
    String menuTitle = "Create a New Account";
    DisplayMenuTitle(menuTitle);
    String login = "";

      try
      {
        System.out.print("\tEnter user login: ");
        login = in.readLine();

        String errorMessage = checkValidLogin(login);

        if ( !(errorMessage.equals("ok")) )
        {
          System.out.println(errorMessage);
          DisplayMenuTitle(menuTitle);
          return;
        }


        System.out.print("\tEnter user password: ");
        String password = in.readLine();
        System.out.print("\tEnter user phone: ");
        String phone = in.readLine();

        //Creating empty contact\block lists for a user
        esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
        int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
        esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
        int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
         
        String query = String.format(
          "INSERT INTO USR (phoneNum, login, password, block_list, contact_list)"+
          " VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);

        esql.executeUpdate(query);
        System.out.println ("\n\tUser successfully created!");
      }

      catch(Exception e)
      {
        if (e.getMessage().contains("ERROR:  duplicate key violates"))
          System.out.println("\n\tError: User " + login + " already exists!");

        else
          System.out.println ("\n\t" + e.getMessage ());
      }

      DisplayEndTitle(menuTitle);
   }

  public static void DeletePublications(Messenger esql, String authorisedUser)
  {
    // delete your chat groups. for each chat group you own, delete chat.
  }

  public static boolean DeleteAccountHelper(Messenger esql, String authorisedUser)
  {
    try
    {
      // check if user is owner of any chats
      String query1 = String.format("SELECT * FROM CHAT WHERE init_sender = '%s'", authorisedUser);
      int result1 = esql.executeQuery(query1);

      if (result1 > 0)
      {
        System.out.println("\tYou cannot delete your account because you are a group owner of one or more chats!");
        return true;
      }

      // check user sent any messages
      String query2 = String.format("SELECT * FROM MESSAGE WHERE sender_login = '%s'", authorisedUser);
      int result2 = esql.executeQuery(query2);

      if (result2 > 0)
      {
        System.out.println("\tYou cannot delete your account because you posted a message!");
        return true;
      }

      // if you're here, that means the previous two conditions weren't true

      String query3 = String.format("DELETE FROM CHAT_LIST WHERE member = '%s'", authorisedUser);
      esql.executeUpdate(query3);

      System.out.println("\tYou were removed from chats.");

      // delete yourself from the userlist
      String query4 = String.format("DELETE FROM USR WHERE login = '%s'", authorisedUser);
      esql.executeUpdate(query4);

      System.out.println("\tYour existence was erased.");

      return false;
    }

    catch (Exception e)
    {
      System.out.println ("\n\tError:" + e.getMessage ());
    }

    return true;
  } 

  public static boolean DeleteAccount(Messenger esql, String authorisedUser)
  {
    String title = "Delete Account";
    DisplayMenuTitle(title);
    boolean ret = true;

    try
    {
      System.out.print("\tAre you sure you want to delete your account, " + authorisedUser + "? (y/n): ");
      String answer = in.readLine();

      if (answer.equals("y") || answer.equals("Y") || answer.equals ("yes") || answer.equals ("YES")){
        System.out.print("\tPlease enter user password: ");
        String password = in.readLine();
        String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", authorisedUser, password);
        int userNum = esql.executeQuery(query);

        if (userNum > 0){
          ret = DeleteAccountHelper(esql, authorisedUser);
        }else{
          System.out.println("\tWrong password, account not deleted.");
          ret = false;
        }
      }
    }

    catch (Exception e)
    {
      System.out.println ("\n\tError:" + e.getMessage ());
    }

    DisplayEndTitle(title);
    return ret;
  }
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
    String menuTitle = "Login";
    DisplayMenuTitle(menuTitle);

      try
      {
        System.out.print("\tEnter user login: ");
        String login = in.readLine();

        String password = "INVALD";

        // TODO: REMOVE
        if (login.equals("Norma"))
          password = "8c0bb848dc6691e9e8580f1b5eff110880d3";
        else if (login.equals("Lonny"))
          password = "9dc5f6ce96c92ad928a3b011fe0005cedd27";
        else if (login.equals("Reba"))
          password = "5ef67e114d92c17906de96a53458a22a045e";
        else
        {
          System.out.print("\tEnter user password: ");
          password = in.readLine();
        }

        String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
        int userNum = esql.executeQuery(query);

        if (userNum > 0){
          DisplayEndTitle(menuTitle);
          return login;
      }else{
        System.out.print("\n\tIncorrect username or password!");
        DisplayEndTitle(menuTitle);
          return null;
        }
      }

      catch(Exception e)
      {
         System.err.println (e.getMessage ());
         DisplayEndTitle(menuTitle);
         return null;
      }
   } 

   public static void AddToContact(Messenger esql, String authorisedUser){
      String menuTitle = "Add a New Contact";
      DisplayMenuTitle(menuTitle);

      String contact = "INVALID";

      try{
        System.out.print("\tEnter the contact's login: ");
        contact = in.readLine();
        String query1 = "SELECT * FROM USR WHERE login = '" + contact + "';";
        int userNum = esql.executeQuery(query1);
        if(userNum==0){
          System.out.println("\n\t" + contact + " doesn't exist!");
        }else{
      //Remove from blocked
          String removeFrom = String.format(
            "DELETE FROM USER_LIST_CONTAINS "+
          "WHERE (select block_list from USR where login='%s')=list_id "+
          "AND list_member = '%s';",authorisedUser,contact);
          esql.executeUpdate(removeFrom);

          String addTo = String.format(
              "INSERT INTO USER_LIST_CONTAINS " + 
              "VALUES ((SELECT contact_list FROM USR WHERE login = '"+ authorisedUser +"'),'" 
              + contact + "');" );
            esql.executeUpdate(addTo);

            System.out.println("\n\t" + contact + " has been added to your contacts.");
        }
      }
      catch(Exception e){ 
          //If User already in contact list it will tell the User so
      if(e.getMessage().contains("ERROR:  duplicate key violates")){
        String msg = "\n\t" + contact + " is already in your contact list!";
        System.out.println(msg);
        }else{
        System.err.println (e.getMessage ());
      }
    }

    DisplayEndTitle(menuTitle);

  }//end

   public static void AddToBlock(Messenger esql, String authorisedUser){
      String menuTitle = "Block a User";
      DisplayMenuTitle(menuTitle);
      String blocker = "INVALID";

      try{
          System.out.print("\tEnter the user's login: ");
          blocker = in.readLine();

          String query1 = "SELECT * FROM USR WHERE login = '" + blocker + "';";
          int userNum = esql.executeQuery(query1);
          if(userNum==0){
            System.out.println("\n\t" + blocker + " doesn't exist!");
          }else{
            //Remove from contact
            String removeFrom = String.format(
              "DELETE FROM USER_LIST_CONTAINS "+
            "WHERE (select contact_list from USR where login='%s')=list_id "+
            "AND list_member = '%s';",authorisedUser,blocker);
            esql.executeUpdate(removeFrom);

            //Add to Blocked
            String addTo = String.format(
                "INSERT INTO USER_LIST_CONTAINS " + 
                "VALUES ((SELECT block_list FROM USR WHERE login = '%s'),'%s');"
                ,authorisedUser,blocker);
              esql.executeUpdate(addTo);

              System.out.println("\n\t" + blocker + " is now blocked.");
          }

      }catch(Exception e){
        if(e.getMessage().contains("ERROR:  duplicate key violates")){
        System.out.println("\n\t" + blocker + " is already blocked!");
          }else{
        System.err.println (e.getMessage ());
      }
      }
      DisplayEndTitle(menuTitle);
   }

   public static void DisplayContacts(Messenger esql, String authorisedUser, boolean flag)
   {
      try
      {
        String query = 
        "SELECT ULC.list_member " +
        "FROM USER_LIST_CONTAINS ULC, USR U " + 
        "WHERE U.contact_list = ULC.list_id AND U.login = '" + authorisedUser + "';";

        //Returns # of fitting results
        //HAVE TO USE executeQueryAndReturnResult, no not use executeQuery
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        if(result.size() == 0)
          System.out.println("\tYou have no friends. :(");
        else
        {
          if (flag)
            System.out.println("\tYou have " + result.size() + " friends.\n");

          String output = "";
          int count = 0;
          for(List<String> list : result)
          {
            ++count;
            for(String word : list)
              // output+="\t"+count +". "+ word.trim() + "\n";
              output += "\t" + word.trim() + "\n";
          }
            System.out.println(output);
        }
      }
      
      catch(Exception e)
      {
        System.err.println (e.getMessage ());
      }
   }

   public static void DisplayBlocked(Messenger esql, String authorisedUser, boolean flag)
   {
      try
      {
        String query = 
        "SELECT ULC.list_member " +
        "FROM USER_LIST_CONTAINS ULC, USR U " + 
        "WHERE U.block_list = ULC.list_id AND U.login = '" + authorisedUser + "';";

        //Returns # of fitting results
        //HAVE TO USE executeQueryAndReturnResult, no not use executeQuery
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        if(result.size() == 0)
          System.out.println("\tYou haven't blocked anyone yet.");
        else
        {
          if (flag)
            System.out.println("\tYou blocked " + result.size() + " users.\n");

          String output = "";
          int count = 0;
          for(List<String> list : result)
          {
            ++count;
            for(String word : list)
              output+="\t" + word.trim() + "\n";
          }

          System.out.println(output);
        }
      } // end of try

      catch(Exception e)
      {
         System.err.println (e.getMessage ());
      }
   }

  public static void VerifyContactBlock(Messenger esql, String authorisedUser, String listType)
  {

    boolean isValidUser = false;
    String userToRemove = "";

    try 
    {
      String userQuery = "";

      // obtain the users in the list.
      if (listType.equals("block"))
        userQuery = String.format("SELECT ULC.list_member FROM USER_LIST_CONTAINS ULC, USR U WHERE U.block_list = ULC.list_id AND U.login = '%s'", authorisedUser);
      else
        userQuery = String.format("SELECT ULC.list_member FROM USER_LIST_CONTAINS ULC, USR U WHERE U.contact_list = ULC.list_id AND U.login = '%s'", authorisedUser);

      List<List<String>> result = esql.executeQueryAndReturnResult(userQuery);

      if(result.size() == 0)
        return;

      // check if the input is a valid user in the list
      while (!isValidUser)
      {
        System.out.print("\tWho do you want to remove? (Type 'q' to go back): ");
        userToRemove = in.readLine();

        if (userToRemove.equals("q") || userToRemove.equals ("Q"))
        {
          System.out.println("\n\t No users were removed.");
          return;
        }

        // check if user entered a valid member
        for (List<String> login : result)
        {
          if ( (login.get(0).trim()).equals(userToRemove))
          {
            isValidUser = true;
            break;
          }
        }

        if (!isValidUser)
          System.out.println("\t" + "User " + userToRemove + " doesn't belong to this list!");

      } // end of while loop isValidUser

      // valid user, so remove them from the list
      if (listType.equals("block"))
      {
          String removeFrom = String.format(
          "DELETE FROM USER_LIST_CONTAINS "+
          "WHERE (select block_list from USR where login='%s')=list_id "+
          "AND list_member = '%s'",authorisedUser, userToRemove);
          esql.executeUpdate(removeFrom);

          System.out.println("\n\t" + userToRemove + " is no longer blocked.");
      }

      else
      {
        String removeFrom = String.format(
        "DELETE FROM USER_LIST_CONTAINS "+
        "WHERE (select contact_list from USR where login='%s')=list_id "+
        "AND list_member = '%s'", authorisedUser, userToRemove);
        esql.executeUpdate(removeFrom);

        System.out.println("\n\t" + userToRemove + " has been removed from contacts.");
      }


    } // end of try

    catch (Exception e)
    {
      System.err.println (e.getMessage ());
    }
  }

   //Try to get this to alphabetical order. Use Indexes.
   public static void ListContacts(Messenger esql,String authorisedUser){
        String menuTitle = "Your Contacts";
        DisplayMenuTitle(menuTitle);
        DisplayContacts(esql, authorisedUser, true);
        DisplayEndTitle(menuTitle);     
    }

  public static void ListBlocks(Messenger esql,String authorisedUser){
      String menuTitle = "Blocked Users";
      DisplayMenuTitle(menuTitle);
      DisplayBlocked(esql, authorisedUser, true);
      DisplayEndTitle(menuTitle);    
    }

  public static void RemoveContact(Messenger esql, String authorisedUser)
  {
    String title = "Remove a Contact";
    DisplayMenuTitle(title);
    DisplayContacts(esql, authorisedUser, false);
    VerifyContactBlock(esql, authorisedUser, "contact");
    DisplayEndTitle(title);
  }

  public static void UnblockUser (Messenger esql, String authorisedUser)
  {
    String title = "Unblock a User";
    DisplayMenuTitle(title);
    DisplayBlocked(esql, authorisedUser, false);
    VerifyContactBlock(esql, authorisedUser, "block");
    DisplayEndTitle(title);
  }

  // Steph's Note: This function attempts to place the table divider, "|", at the right location
  public static String FormatChatTableRow(String menuOption, String listItem, int column)
  {
    int actualSpaces = 0;
    String formattedString = "";

    // Chat ID column: ten spaces, then | for column 1
    if (column == 0)
    {
      String columnName = "   Chat ID  ";
      int numSpaces = columnName.length();
      actualSpaces = numSpaces - menuOption.length() - listItem.length();
    }

    else if (column == 1)
    {
      String columnName = "  Chat Type  ";
      int numSpaces = columnName.length();
      actualSpaces = numSpaces - listItem.length() - 1;
    }
    else if (column == 2)
    {
      String columnName = "  Initial Sender   ";
      actualSpaces = columnName.length() - listItem.length();
    }

    else if (column == 3)
    {
      String columnName = "  Recent Message ";
      actualSpaces = columnName.length() - listItem.length();
    }

    for (int i = 0; i < actualSpaces; i++)
      formattedString += " ";

    return formattedString + "|";

  }

  // Steph's Note: This function is only used when listing all of the chats.
  public static void DisplayChatTable(boolean flag)
  {
    if (flag)
    {
      System.out.println("\n\t=========================================================================================");
      System.out.println("\t   Chat ID  |  Chat Type  |   Initial Sender   |  Recent Message  |       Timestamp  ");
      System.out.println("\t============|=============|====================|==================|======================");
    }

    else
    {
      System.out.println("\n\t==============================================");
      System.out.println("\t   Chat ID  |  Chat Type  |   Initial Sender");
      System.out.println("\t============|=============|===================");
    }
  }

  /* Steph's: Note: The only thing I changed for ListChats was how I formatted the display.
   */
   public static boolean ListChats(Messenger esql, String authorisedUser, boolean flag)
   {
      String menuTitle = "Your Chats";
      DisplayMenuTitle(menuTitle);
      try{
        // For display chats, check if person is member (currentUser) of chat_id.
        // then display all chats according chat_id
        String query = String.format("SELECT C.*, M.msg_text, M.msg_timestamp FROM CHAT C, MESSAGE M WHERE M.msg_timestamp IN (SELECT MAX(M1.msg_timestamp) AS ts FROM MESSAGE M1 WHERE M1.chat_id = C.chat_id) AND  C.chat_id = M.chat_id AND C.chat_id IN  (SELECT CL.chat_id FROM CHAT_LIST CL WHERE CL.member='%s' ) ORDER BY M.msg_timestamp DESC", authorisedUser);

        System.out.println("\tOne moment... loading chats...");

        List<List<String>> result = esql.executeQueryAndReturnResult(query);
          if(result.size() == 0){
            System.out.println("\tYou have no chats. :(");
            DisplayEndTitle(menuTitle);   
            return false;
          }else{

            DisplayChatTable(flag);

            String output = "";
            int count = 0;

            // rows
            for(List<String> list : result)
            {
              ++count;
              //String rowString = count + ". ";
              String rowString = "";
              output +="\t" + rowString;

          // columns
          for(int i=0;i<list.size();++i){
            if(i==list.size()-1)
              output+=list.get(i).trim();
            else
            {
                String listItem = list.get(i).trim();
                if (listItem.length() > 15 && i == 3)
                  listItem = listItem.substring(0,12) + "...";
                output +=  listItem + FormatChatTableRow (rowString, listItem, i) + " ";

              }
            }
            output += "\n";
          }
          System.out.print(output);
          output="";
        }
      }

      catch(Exception e)
      {
        System.err.println (e.getMessage ());
      }

      DisplayEndTitle(menuTitle);   
      return true;

   }

  public static void ShowChatInterface(Messenger esql, String authorisedUser){
    try{
      boolean chatInterfacing = true;

      while(chatInterfacing){
        System.out.println("\n\n\t===================================");
        System.out.println("\t\tCHAT INTERFACE");
        System.out.println("\t===================================");
        System.out.println("\t1. Enter a Chat");
        System.out.println("\t2. Create a New Chat");
        System.out.println("\t3. Delete a Chat");
        System.out.println("\t===================================");
        System.out.println("\t9. Leave Chat Interface");

        switch(readChoice()){
          case 1: EnterChat(esql, authorisedUser); break;
          case 2: CreateChat(esql, authorisedUser); break;
          case 3: DeleteChat(esql, authorisedUser); break;
          case 9: chatInterfacing = false; break;
          default : System.out.println("Unrecognized Choice!"); break;
        }
      }
    }
    catch(Exception e){
      System.err.println(e.getMessage());
    }
  }

  public static void EnterChat(Messenger esql, String authorisedUser){
    try
    {

      if (!ListChats(esql, authorisedUser, true))
        return;

      boolean invalidChatID = true;
      int chatID = -1;
      String chatIDChoice = "";

      while(invalidChatID)
      {

        System.out.print("\tSelect a chat ID (Type 'q' to go back): ");
        chatIDChoice = in.readLine();

        if (chatIDChoice.equals("q") || chatIDChoice.equals("Q") || chatIDChoice.equals("quit") || chatIDChoice.equals("QUIT"))
          return;

        String checkChatIDExistsQuery = "SELECT chat_id FROM chat_list WHERE member = '" + authorisedUser + "' AND chat_id = '" + chatIDChoice + "'";
        int chatCount = esql.executeQuery(checkChatIDExistsQuery);

        if(chatCount == 1)
        {
          chatID = Integer.parseInt(chatIDChoice);
          invalidChatID = false;
        }
        else
          System.out.println("\tInvalid ID, please pick another!\n");

      }

      int showNumMessages = 10;

      boolean inChat = true;
      boolean isGroupOwner = false;
      boolean messagesLoaded = false;
      String retMsg = "";

      // check if user is group owner of the chats
      String groupOwnerQuery = String.format("SELECT init_sender from CHAT WHERE chat_id = %s AND init_sender = '%s'", chatIDChoice, authorisedUser);
      int groupOwnerVal = esql.executeQuery(groupOwnerQuery);

      String getOwnerQuery = String.format("SELECT init_sender from CHAT WHERE chat_id = %s", chatIDChoice);
      List<List<String>> groupOwnerResult = esql.executeQueryAndReturnResult(getOwnerQuery);

      if (groupOwnerVal != 0)
        isGroupOwner = true;

      while(inChat)
      {
        ShowChatMessages(esql, authorisedUser, chatID, chatIDChoice, showNumMessages);

        if (messagesLoaded)
        {
          System.out.println ("\tPast 10 messages have been loaded. Scroll up to view them.");
          messagesLoaded = false;
        }

        System.out.println(retMsg);

        System.out.println("");
        System.out.println("\tChat #" + chatIDChoice + " Options");
        System.out.println("\tGroup Owner: " + groupOwnerResult.get(0).get(0));
        System.out.println("\t=======================");
        System.out.println("\t1. Show Chat Members");
        System.out.println("\t2. Write a New Message");
        System.out.println("\t3. Delete a Message");
        System.out.println("\t4. Edit a Message");
        System.out.println("\t5. Load Messages");

        if (isGroupOwner) 
        {
          System.out.println("\t6. Add a User to Chat");
          System.out.println("\t7. Remove a User From Chat");
        }

        System.out.println("\t=======================");
        System.out.println("\t9. Exit Chat");

        if (isGroupOwner)
        {
          switch(readChoice())
          {
            case 1: ShowChatMembers(esql, authorisedUser, chatID); break;
            case 2: retMsg = WriteNewMessage(esql, authorisedUser, chatID); break;
            case 3: retMsg = DeleteMessage(esql, authorisedUser, chatID); break;
            case 4: retMsg = EditMessage(esql, authorisedUser, chatID); break;
            case 5: showNumMessages = LoadMessages(showNumMessages); messagesLoaded = true; break;
            case 6: retMsg = AddUserToChat(esql, authorisedUser, chatID); break;
            case 7: retMsg = RemoveUserFromChat(esql, authorisedUser, chatID); break;
            case 9: inChat = false; break;
                                    
            default : System.out.println("\tInvalid choice!"); break;
          } // end Switch for Group Owner
        }

        else
        {
          switch(readChoice())
          {
            case 1: ShowChatMembers(esql, authorisedUser, chatID); break;
            case 2: retMsg = WriteNewMessage(esql, authorisedUser, chatID); break;
            case 3: retMsg = DeleteMessage(esql, authorisedUser, chatID); break;
            case 4: retMsg = EditMessage(esql, authorisedUser, chatID); break;
            case 5: showNumMessages = LoadMessages(showNumMessages); messagesLoaded = true; break;
            case 9: inChat = false; break;
                                    
            default : System.out.println("\tInvalid choice!"); break;
          } // end Switch for non-Group Owner
        }

      } // end while (InChat)

    } // end try

    catch(Exception e)
    {
      System.out.print("\tError: ");
      System.err.println(e.getMessage());
    }
  } // end EnterChat

  public static void CreateChat(Messenger esql, String authorisedUser){
    String title = "Create a New Chat";
    DisplayMenuTitle(title);

    try
    {
      String nextChatIDquery = "SELECT chat_id FROM chat ORDER BY chat_id DESC LIMIT 1";
      List<List<String>> result = esql.executeQueryAndReturnResult(nextChatIDquery);

      int newChatID = Integer.parseInt(result.get(0).get(0)) + 1;

      String query = String.format("INSERT INTO CHAT (chat_id, chat_type, init_sender) VALUES (%d, 'private', '%s')", newChatID, authorisedUser);
      esql.executeUpdate(query);

      String query1 = String.format("INSERT INTO chat_list (chat_id, member) VALUES (%d, '%s')", newChatID, authorisedUser);
      esql.executeUpdate(query1);

      // ask user to write an initial message
      String messageIDQuery = "SELECT msg_id FROM message ORDER BY msg_id DESC LIMIT 1";
      String timestampQuery = "SELECT LOCALTIMESTAMP(0)";

      List<List<String>> result_msgID = esql.executeQueryAndReturnResult(messageIDQuery);
      List<List<String>> result_timestamp = esql.executeQueryAndReturnResult(timestampQuery);

      int msgID = Integer.parseInt(result_msgID.get(0).get(0)) + 1;
      String timestamp = result_timestamp.get(0).get(0);

      System.out.print("\tEnter an initial message: ");
      String message = in.readLine();
      String initMessageQuery = String.format("INSERT INTO message (msg_id, msg_text, msg_timestamp, sender_login, chat_id) VALUES (%d, '%s', '%s', '%s', %d)",
                                     msgID, message, timestamp, authorisedUser, newChatID);
      esql.executeUpdate(initMessageQuery);

      // Create a dummy message which contains the dummy message's timestamp.
      // This timestamp is used to determine when the chat was created.
      // This dummy message will never be displayed and can never be deleted because:
      // You will check if message timestamp == message text.
      // The user cannot 'hack' this because their message will have different timestamp.
      // This means I need to take care of these special cases in Edit, Delete, Display message.

      // display contact list and prompt
      DisplayContacts(esql,authorisedUser,false);
      System.out.println("Who do you want to add to the chat from your contact? \nEnter each contact one at a time. Type 'done' when finished adding.");

      boolean doneAdding = false;
      int groupSize = 0;

      while(!doneAdding)
      {
        System.out.print("\t");
        String newUser = in.readLine();

        if (newUser.equals("done"))
          break;
 
        String checkUserQuery = String.format(
          "SELECT ULC.list_member FROM USER_LIST_CONTAINS ULC, USR U " + 
          "WHERE U.contact_list = ULC.list_id AND U.login = '%s' AND ULC.list_member = '%s'", authorisedUser, newUser);
        int result1 = esql.executeQuery(checkUserQuery);

        if (result1 == 0)
          System.out.println("\t" + newUser + " is an invalid user!\n");

        // add the user to chat
        else
        {
          String query2 = String.format("INSERT INTO chat_list (chat_id, member) VALUES (%d, '%s')", newChatID, newUser);
          esql.executeUpdate(query2);
          System.out.println("\t" + newUser + " has been added to chat #" + newChatID + "\n");
          groupSize++;
        }

      }

      if (groupSize > 1)
      {
        String query3 = String.format ("UPDATE chat SET chat_type = 'group' WHERE chat_id = %s", newChatID);
        esql.executeUpdate(query3);
      }
      System.out.println("\tChat #" + newChatID + " created.");
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
    }

    DisplayEndTitle(title);
  }

  public static void DeleteChat(Messenger esql, String authorisedUser)
  {
    String title = "Delete a Chat";
    DisplayMenuTitle(title);

    try
    {
      // first, find all of the chats that they own using init sender. List those chats.
      String query1 = String.format("SELECT * FROM CHAT WHERE init_sender = '%s'", authorisedUser);
      List<List<String>> chatList = esql.executeQueryAndReturnResult(query1);

      if(chatList.size() == 0)
      {
        System.out.println("\tYou have no chats. :(");
        DisplayEndTitle(title);   
        return;
      }
      else
      {
        DisplayChatTable(false);

        String output = "";
        int count = 0;

        // rows
        for(List<String> list : chatList)
        {
          ++count;
          //String rowString = count + ". ";
          String rowString = "";
          output +="\t" + rowString;

          // columns
          for(int i=0;i<list.size();++i)
          {
            if(i==list.size()-1)
              output+=list.get(i).trim();
            else
            {
                String listItem = list.get(i).trim();

                //output+=list.get(i).trim() + ", ";
                output +=  listItem + FormatChatTableRow (rowString, listItem, i) + " ";

            }
          } // end of for loop columns

          output += "\n";

        } // end of for loop rows

        System.out.println(output);
      } // end of else

      // then, ask them to pick a chat. then check if the chat exists with the current user and chat id from input
      System.out.print("\tSelect a chat to delete (Type 'q' to go back): ");
      String chatID = in.readLine();

      if (chatID.equals("q") || chatID.equals("Q"))
      {
        System.out.println("\tNo chats deleted.");
        DisplayEndTitle(title);
        return;
      }

      String query2 = String.format("SELECT * FROM CHAT WHERE chat_id = %s AND init_sender = '%s'", chatID, authorisedUser);

      // then execute query, check if that chat exists. if true, delete all messages first where chat_id = input_chatID
      int count = esql.executeQuery(query2);
      if (count == 0)
        System.out.println("\tInvalid chat #!");

      else
      {
        // then delete from message -> chat_list -> chat
        String query3 = String.format("DELETE FROM MESSAGE WHERE chat_id = %s", chatID);
        esql.executeUpdate(query3);
        System.out.println("\tMessages from chat #" + chatID + " deleted.");

        // then delete the entries in chat_list
        String query4 = String.format("DELETE FROM CHAT_LIST WHERE chat_id = %s", chatID);
        esql.executeUpdate(query4);
        System.out.println("\tRemoving members from chat #" + chatID);

        // then delete the chat
        String query5 = String.format("DELETE FROM CHAT WHERE chat_id = %s", chatID);
        esql.executeUpdate(query5);
        System.out.println("\tChat #" + chatID + " deleted.");
      }
    }
    catch (Exception e)
    {
      System.err.println(e.getMessage());
    }

    DisplayEndTitle(title);
  }

  public static void ShowChatMembers(Messenger esql,String authorisedUser,int chatID){
    String menuTitle = "Chat Members";
    try{
      String query = "SELECT DISTINCT CL.member FROM CHAT C, CHAT_LIST CL WHERE "+chatID+" = CL.chat_id";
        List<List<String> > result = esql.executeQueryAndReturnResult(query);
          if(result.size() == 0){
            System.out.println("\n\tNo members in chat");
            DisplayEndTitle(menuTitle);   
          }else{
              System.out.println("======================"+menuTitle+"======================\n");
              System.out.println("\tYou have " + result.size() + " members in chat.");

            String output = "";
            int count = 0;
            for(List<String> list : result)
            {
              ++count;
              for(String word : list)
                // output+="\t"+count +". "+ word.trim() + "\n";
                output += "\t" + word.trim() + "\n";
            }
            System.out.println(output);
          }
      }
      catch(Exception e)
      {
        System.err.println (e.getMessage ());
      }

      DisplayEndTitle(menuTitle);   
  }

  /* Call this function to format the messages.
   * [MSG_ID]                                          
   *  _____________________________________
   * | Sender | timestamp                  |
   * |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|
   * | Body                                |
   * |                                     |
   * |  ___________________________________|
   * |/
   * '
   *
   * 
   *  _____________________________________
   * | Sender | timestamp                  |
   * |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|
   * | Body                                |
   * |                                     |
   * |____________________________________ |
   *                                      \|
   *                                       '
   */
  public static void DisplayChatMessages(String msg_id, String body, String timestamp, String sender, String authorisedUser)
  {
    String bottom1;
    String bottom2;
    String bottom3;

    boolean addTabs = false;

    // check if sender == authorizedUSer
    if (sender.equals(authorisedUser))
    {
      addTabs = true;
      bottom1 = "|____________________________________";
      bottom2 = "                                     ";
      bottom3 = "                                      ";
    }

    else
    {
      bottom1 = "| ___________________________________";
      bottom2 = "|/";
      bottom3 = "'";
    }

    String topLine = " ____________________________________";
    String squiggles = "|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
    String bubbleSpace = "                                   ";

    int extraChar = 0;
    String fillSpace = "";

    // if this is true, you need to extend the chat bubble
    // determine how many more underscores and spaces you need
    if (bubbleSpace.length() < body.length())
    {
      extraChar = body.length() - bubbleSpace.length();


      for (int i = 0; i < extraChar; i++)
      {
          topLine += "_";
          squiggles += "~";
          bubbleSpace += " ";

          if (sender.equals(authorisedUser))
          {
            bottom1 += "_";
            bottom2 += " ";
            bottom3 += " ";
          }

          else
            bottom1 += "_";

      }
    }

    else
    {
      int numSpaces = bubbleSpace.length() - body.length();
      for (int i = 0; i < numSpaces; i++)
        fillSpace += " ";
    }

      String sendertimeLine = "| " + sender + " | " + timestamp;
      int numSpacesBorder = bubbleSpace.length() - sender.length() - 3 - timestamp.length(); // num of spaces needed til "|"

      // concatenate spaces for Sender | timestamp;
      for (int j = 0; j < numSpacesBorder; j++)
        sendertimeLine += " ";

      String tab = "";

      if (addTabs)
        tab = "\t";

      System.out.println(tab + "[ " + msg_id + " ]");
      System.out.println(tab + topLine + "_");
      System.out.println(tab + sendertimeLine + " |");
      System.out.println(tab + squiggles + "~|");
      System.out.println(tab + "| " + body + fillSpace + " |");
      System.out.println(tab + "| " + bubbleSpace + " |");

      if (sender.equals(authorisedUser))
      {
        System.out.println(tab + bottom1 + " |");
        System.out.println(tab + bottom2 + "\\|");
        System.out.println(tab + bottom3 + "'");
      }

      else
      {
        System.out.println(bottom1 + "_|");
        System.out.println(bottom2);
        System.out.println(bottom3);
      }
  }
  
  //(this one shows all messages in a given chat)
  public static void ShowChatMessages(Messenger esql, String authorisedUser, int chatID, String chatIDChoice, int showNumMessages)
  {
    String menuTitle = "Chat #" + chatIDChoice + " Messages";
    DisplayMenuTitle(menuTitle);
    String query = "SELECT M.msg_id, M.msg_text, M.msg_timestamp, M.sender_login FROM message M WHERE M.chat_id = '" + chatID + "' ORDER BY M.msg_timestamp DESC LIMIT " + showNumMessages;

    try
    {
      List<List<String>> result = esql.executeQueryAndReturnResult(query);


      if(result.size() == 0)
        System.out.println("\tThis chat has no messages.");

      else
      {
        String output = "";

        // row
        int numRows = result.size();
        for(int i = numRows-1; i >= 0; i--)
        {
          List<String> row = result.get(i);
          DisplayChatMessages(row.get(0).trim(), row.get(1).trim(), row.get(2).trim(), row.get(3).trim(), authorisedUser);

        } // end for loop rows

      } // end else

    } // end try

    catch(Exception e)
    {
      System.err.println (e.getMessage ());
    }

    DisplayEndTitle(menuTitle);

  }

  public static String WriteNewMessage(Messenger esql, String authorisedUser, int chatID){
    String menuTitle = "Write a New Message";
    DisplayMenuTitle(menuTitle);
    String ret = "";

    try
    {
      // first, obtain the msg_id.
      String query1 = "SELECT msg_id FROM message ORDER BY msg_id DESC LIMIT 1";
      String timestampQuery = "SELECT LOCALTIMESTAMP(0)";

      List<List<String>> result_msgID = esql.executeQueryAndReturnResult(query1);
      List<List<String>> result_timestamp = esql.executeQueryAndReturnResult(timestampQuery);

      int msgID = Integer.parseInt(result_msgID.get(0).get(0)) + 1;
      String timestamp = result_timestamp.get(0).get(0);

      System.out.print("\tEnter a message: ");
      String message = in.readLine();
      String query2 = String.format("INSERT INTO message (msg_id, msg_text, msg_timestamp, sender_login, chat_id) VALUES (%d, '%s', '%s', '%s', %d)",
                                     msgID, message, timestamp, authorisedUser, chatID);


      esql.executeUpdate(query2); 
      
      ret = "\n\tMessage was sent!";
    }

    catch (Exception e)
    {
      ret = e.getMessage ();
    }

    DisplayEndTitle(menuTitle);
    return ret;
  }

  //DELETE MESSAGE MADE BY KOALA (deletes a given message)
  public static String DeleteMessage(Messenger esql, String authorisedUser, int chatID){
    String menuTitle = "Delete a Message";
    DisplayMenuTitle(menuTitle);
    String ret = "";

    try
    {

      // First, user must type in a msg_id
      System.out.print("\tSelect a message to delete (Or press 'q' to go back): ");
      String msgID = in.readLine();

      if (msgID.equals("q") || msgID.equals("Q"))
      {
        DisplayEndTitle(menuTitle);
        return "\tNo messages were removed!";
      }

      // first check that the user chose a correct message.
      String verifyMSGIDquery = String.format ("SELECT msg_text FROM MESSAGE WHERE msg_id = %s AND chat_id = %d AND sender_login = '%s'", msgID, chatID, authorisedUser);
      List<List<String>> result = esql.executeQueryAndReturnResult(verifyMSGIDquery);

      if (result.size() == 0)
        ret = "\tError: You have either entered an invalid message # or tried to delete another user's message.";
      else
      {
        String oldMessage = result.get(0).get(0);
        System.out.println("\tMessage: " + oldMessage);
        System.out.print("\tAre you sure you want to delete this message? (y/n): ");
        String answer = in.readLine();

        if (answer.equals("y") || answer.equals ("Y") || answer.equals("yes") || answer.equals("Yes") )
        {
          String editMessageQuery = String.format("DELETE FROM MESSAGE WHERE msg_id = %s", msgID);
          esql.executeUpdate(editMessageQuery);
          ret = "\tMessage #" + msgID + " deleted.";
        }

        else
          ret = "\tMessage has not been deleted.";
      }
    }

    catch (Exception e) 
    {
      ret = e.getMessage ();
    }

    DisplayEndTitle(menuTitle);
    return ret;
  }

  //EDIT MESSAGE MADE BY KOALA (edits a given message)
  public static String EditMessage(Messenger esql, String authorisedUser, int chatID){
    String menuTitle = "Edit a Message";
    DisplayMenuTitle(menuTitle);
    String ret = "";

    try
    {
      // First, user must type in a msg_id
      System.out.print("\tSelect a message to edit (Type 'q' to go back): ");
      String msgID = in.readLine();

      if (msgID.equals("q") || msgID.equals("Q"))
      {
        DisplayEndTitle(menuTitle);
        return "\tNo messages were editted.";
      }

      // first check that the user chose a correct message.
      String verifyMSGIDquery = String.format ("SELECT msg_text FROM MESSAGE WHERE msg_id = %s AND chat_id = %d AND sender_login = '%s'", msgID, chatID, authorisedUser);
      List<List<String>> result = esql.executeQueryAndReturnResult(verifyMSGIDquery);

      if (result.size() == 0)
        ret = "\tError: You have either entered an invalid message # or tried to edit another user's message.";
      else
      {
        String oldMessage = result.get(0).get(0);
        System.out.println("\tOld message: " + oldMessage);
        System.out.print("\tEnter a new message: ");
        String newMessage = in.readLine();

        String editMessageQuery = String.format("UPDATE MESSAGE SET msg_text = '%s' WHERE msg_id = %s", newMessage, msgID);
        esql.executeUpdate(editMessageQuery);

        ret = "\tMessage #" + msgID + " has been editted.";
      }
    }

    catch (Exception e) 
    {
      ret = e.getMessage ();
    }

    DisplayEndTitle(menuTitle);
    return ret;
  }

  public static int LoadMessages(int showNumMessages){
    return showNumMessages + 10;
  }

  public static void ListChatMembers(Messenger esql, String authorisedUser, int chatID)
  {
    try
    {
        String query = 
        "SELECT ULC.list_member " +
        "FROM USER_LIST_CONTAINS ULC, USR U " + 
        "WHERE U.contact_list = ULC.list_id AND U.login = '" + authorisedUser + "';";

        //Returns # of fitting results
        //HAVE TO USE executeQueryAndReturnResult, no not use executeQuery
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        if(result.size() == 0)
          System.out.println("\tYou have no friends. :(");
        else
        {
            System.out.println("\tYou have " + result.size() + " friends.\n");

          String output = "";
          int count = 0;
          for(List<String> list : result)
          {
            ++count;
            for(String word : list)
              // output+="\t"+count +". "+ word.trim() + "\n";
              output += "\t" + word.trim() + "\n";
          }
            System.out.println(output);
        }
    }

    catch (Exception e)
    {
      System.err.println (e.getMessage ());
    }
  }

  public static String AddUserToChat(Messenger esql, String authorisedUser, int chatID)
  {
    String title = "Add a User to Chat";
    DisplayMenuTitle(title);
    String ret = "";

    try
    {
      // display friends who aren't in the chat yet
      String chatMembers = String.format("SELECT member FROM CHAT_LIST WHERE chat_id = %d", chatID);
      String contacts = String.format("SELECT ULC.list_member FROM USER_LIST_CONTAINS ULC, USR U WHERE U.contact_list = ULC.list_id AND U.login = '%s'", authorisedUser);
      String query = contacts + " AND ULC.list_member NOT IN (" + chatMembers + ")";

      List<List<String>> users = esql.executeQueryAndReturnResult(query);

      if (users.size() == 0)
      {
        DisplayEndTitle(title);
        return "\tAll of your friends are already in the chat!";
      }

      for (List<String> u : users)
        System.out.println("\t" + u.get(0).trim());

      boolean isValidUser = false;
      String userToAdd = "";

      // check if user entered a valid input
      while (!isValidUser)
      {
        System.out.print("\n\tWho do you want to add? (Type 'q' to cancel): ");
        userToAdd = in.readLine();

        if (userToAdd.equals("q") || userToAdd.equals("Q"))
          return "\tNo users were added to the chat.";

        for (List<String> u : users)
        {
          if (userToAdd.equals(u.get(0).trim()))
          {
            isValidUser = true;
            break;
          }
        }

        if (!isValidUser)
          System.out.println("\tUser " + userToAdd + "is either already in the chat or an invalid user.");
      } // end of while for isValidUser

      List<List<String>> numMembers = esql.executeQueryAndReturnResult(chatMembers);

      // after adding this member, check if this action will make the group more than 2 people. if so, change from private to group.
      if ( (numMembers.size() <= 2)  )
      {
          System.out.print("\tAre you sure you want to add this user? New users will see your private messages. (y/n): ");
          String answer = in.readLine();

          if (answer.equals("y") || answer.equals("Y") || answer.equals("yes") || answer.equals ("YES"))
          {
            // add the user 
            String addMemberQuery = String.format("INSERT INTO chat_list (chat_id, member) VALUES (%d, '%s')", chatID, userToAdd);
            esql.executeUpdate(addMemberQuery);

             ret = "\t" + userToAdd + " has been added to the chat.";

             // if adding a user to a chat consisting of one other person, change chat_type from private->group
             if (numMembers.size() == 2)
             {
                String privateToGroupQuery = String.format("UPDATE CHAT SET chat_type = 'group' WHERE chat_id = %d", chatID);
                esql.executeUpdate(privateToGroupQuery);
                ret += "\n\t" + "Chat #" + chatID + " is now a group chat.";
             }
          } // end of checking for yes

          else
            return "\t" + userToAdd + " was not added to the chat.";

      } // end of if that checks if user really wants to add another user

      else
      {
        // add the user 
        String addMemberQuery = String.format("INSERT INTO chat_list (chat_id, member) VALUES (%d, '%s')", chatID, userToAdd);
        esql.executeUpdate(addMemberQuery);
        ret = "\t" + userToAdd + " has been added to the chat.";
      }

    } // end of try

    catch (Exception e)
    {
      System.out.print("\t");
      System.err.println (e.getMessage ());
    }

    DisplayEndTitle(title);
    return ret;
  }

  public static String RemoveUserFromChat(Messenger esql, String authorisedUser, int chatID)
  {
    String title = "Remove a User From Chat";
    DisplayMenuTitle(title);
    String ret = "";

    try{
      // first display users in the chat
        String chatMemberQuery = String.format("SELECT CL.member FROM CHAT_LIST CL WHERE CL.chat_id = %d AND CL.member != '%s'", chatID, authorisedUser);

        //Returns # of fitting results
        //HAVE TO USE executeQueryAndReturnResult, no not use executeQuery
        List<List<String>> chatMemberList = esql.executeQueryAndReturnResult(chatMemberQuery);
        if(chatMemberList.size() == 0)
          ret = "\tYou can't remove yourself from the chat! :(";
        else
        {
          System.out.println("\tThere are " + chatMemberList.size() + " other user(s) in this chat.\n");

          String output = "";
          int count = 0;
          for(List<String> list : chatMemberList)
          {
            ++count;
            for(String word : list)
              // output+="\t"+count +". "+ word.trim() + "\n";
              output += "\t" + word.trim() + "\n";
          }

          System.out.println(output);

          boolean isValidMember = false;
          String userToRemove = "";

          while (!isValidMember)
          {
            System.out.print("\n\t" + "Who do you want to remove? (Type 'q' to go back): ");
            userToRemove = in.readLine();

            if (userToRemove.equals("q") || userToRemove.equals ("Q"))
            {
              DisplayEndTitle(title);
              return "\n\t No users were removed.";
            }

            // check if user entered a valid member
            for (List<String> login : chatMemberList)
            {
              if ( (login.get(0).trim()).equals(userToRemove))
              {
                isValidMember = true;
                break;
              }
            }

            if (!isValidMember)
              System.out.println("\t" + "User " + userToRemove + " is not a member of this chat!");

          } // end of while loop

          String removeUserQuery = String.format ("DELETE FROM CHAT_LIST WHERE chat_id = %d AND member = '%s'", chatID, userToRemove);
          esql.executeUpdate(removeUserQuery);
          ret = "\n\t" + userToRemove + " has been removed from chat #" + chatID + ".";

          // after deleting this member, check if this action will make the group less than 2 people. if so, make the group private.
          if ( (chatMemberList.size() == 2) && (chatMemberList.size() - 1) < 2)
          {
            String groupToPrivateQuery = String.format("UPDATE CHAT SET chat_type = 'private' WHERE chat_id = %d", chatID);
            esql.executeUpdate(groupToPrivateQuery);
            ret += "\n\t" + "Chat #" + chatID + " is now a private chat.";
          }

        } // end of else

    } // end of try
    catch (Exception e)
    {
      ret = e.getMessage();
    }

    DisplayEndTitle(title);
    return ret;
  }

}//end Messenger
