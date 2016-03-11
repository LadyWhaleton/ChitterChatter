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

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done\n\n");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
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
                System.out.println("\n\n\t===================================");
                System.out.println("\t\tMAIN MENU");
                System.out.println("\t===================================");
                System.out.println("\t1. Show Contacts");
                System.out.println("\t2. Show Blocked List");
                System.out.println("\t3. Show Chat Interface");
                System.out.println("\t4. Add a New Contact");
                System.out.println("\t5. Block a User");
                System.out.println("\t===================================");
                System.out.println("\t9. Log out");
                switch (readChoice()){
                   case 1: ListContacts(esql,authorisedUser); break;
                   case 2: ListBlocks(esql, authorisedUser); break;
                   case 3: ShowChatInterface(esql, authorisedUser); break;
                   case 4: AddToContact(esql,authorisedUser); break;
                   case 5: AddToBlock(esql, authorisedUser); break;
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
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done.\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
  
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface                       \n" +
         "*******************************************************\n");
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

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
    String menuTitle = "Create a New Account";
    DisplayMenuTitle(menuTitle);

      try
      {
        System.out.print("\tEnter user login: ");
        String login = in.readLine();
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
        System.out.println ("User successfully created!");
      }

      catch(Exception e)
      {
         System.err.println (e.getMessage ());
      }

      DisplayEndTitle(menuTitle);
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
        System.out.print("Username or Password Incorrect");
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

   public static void AddToBlock(Messenger esql,String authorisedUser){
      String menuTitle = "Block a Contact";
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
        System.out.println(blocker + " is already blocked!");
          }else{
        System.err.println (e.getMessage ());
      }
      }
      DisplayEndTitle(menuTitle);
   }

   //Try to get this to alphabetical order. Use Indexes.
   public static void ListContacts(Messenger esql,String authorisedUser){
        String menuTitle = "Your Contacts";
        DisplayMenuTitle(menuTitle);

        try{
          String query = 
          "SELECT ULC.list_member " +
          "FROM USER_LIST_CONTAINS ULC, USR U " + 
          "WHERE U.contact_list = ULC.list_id AND U.login = '" + authorisedUser + "';";

          //Returns # of fitting results
          //HAVE TO USE executeQueryAndReturnResult, no not use executeQuery
          List<List<String>> result = esql.executeQueryAndReturnResult(query);
          if(result.size() == 0){
        System.out.println("\tYou have no friends. :(");
          }else{
            String output = "";
            int count = 0;
            for(List<String> list : result)
            {
              ++count;
          for(String word : list)
          output+="\t"+count +". "+ word.trim() + "\n";
        }
            System.out.println(output);
        }
        }catch(Exception e){
          System.err.println (e.getMessage ());
        }

        DisplayEndTitle(menuTitle);     
    }

  public static void ListBlocks(Messenger esql,String authorisedUser){
      String menuTitle = "Blocked Users";
      DisplayMenuTitle(menuTitle);

      try
      {
        String query = 
        "SELECT ULC.list_member " +
        "FROM USER_LIST_CONTAINS ULC, USR U " + 
        "WHERE U.block_list = ULC.list_id AND U.login = '" + authorisedUser + "';";

        //Returns # of fitting results
        //HAVE TO USE executeQueryAndReturnResult, no not use executeQuery
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        if(result.size() == 0){
      System.out.println("\tYou haven't blocked anyone yet.");
        }else{
          String output = "";
          int count = 0;
          for(List<String> list : result)
          {
            ++count;
        for(String word : list)
        output+="\t"+count +". "+ word.trim() + "\n";
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

    for (int i = 0; i < actualSpaces; i++)
      formattedString += " ";

    return formattedString + "|";

  }

  // Steph's Note: This function is only used when listing all of the chats.
  public static void DisplayChatTable()
  {
    System.out.println("\n\t===============================================");
    System.out.println("\t   Chat ID  |  Chat Type  |  Initial Sender");
    System.out.println("\t============|=============|====================");
  }

  /* Steph's: Note: The only thing I changed for ListChats was how I formatted the display.
   */
   public static void ListChats(Messenger esql, String authorisedUser)
   {
      String menuTitle = "Your Chats";
      DisplayMenuTitle(menuTitle);
      try{
        // For display chats, check if person is member (currentUser) of chat_id.
        // then display all chats according chat_id
        String query = 
        "SELECT C.chat_id, C.chat_type, C.init_sender " + 
        "FROM CHAT C, CHAT_LIST CL " +
        "WHERE C.chat_id = CL.chat_id AND CL.member = '" + authorisedUser + "'";

        List<List<String>> result = esql.executeQueryAndReturnResult(query);
          if(result.size() == 0){
            System.out.println("You have no chats. :(");
          }else{

            DisplayChatTable();

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

                //output+=list.get(i).trim() + ", ";
                output +=  listItem + FormatChatTableRow (rowString, listItem, i) + " ";

              }
            }

            output += "\n";
          }
          System.out.print(output);
          output="";
        }
           System.out.println("\t===============================================\n");

        }


      catch(Exception e)
      {
        System.err.println (e.getMessage ());
      }

      DisplayEndTitle(menuTitle);   

   }

  //CHAT INTERFACE MADE BY KOALA
  public static void ShowChatInterface(Messenger esql, String authorisedUser){
    try{
      boolean chatInterfacing = true;

      while(chatInterfacing){
        System.out.println("\n\n\t===================================");
        System.out.println("\t\tCHAT INTERFACE");
        System.out.println("\t===================================");
        System.out.println("\t1. Enter a Chat");
        System.out.println("\t2. Create a New Chat");
        System.out.println("\t===================================");
        System.out.println("\t9. Leave Chat Interface");

        switch(readChoice()){
          case 1: EnterChat(esql, authorisedUser); break;
          case 2: CreateChat(esql, authorisedUser); break;
          case 9: chatInterfacing = false; break;
          default : System.out.println("Unrecognized Choice!"); break;
        }
      }
    }
    catch(Exception e){
      System.err.println(e.getMessage());
    }
  }

 
  //CREATE CHAT MADE BY KOALA
  public static void CreateChat(Messenger esql, String authorisedUser){
    try
    {
      System.out.println("Here lies the Create Chat function");
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
    }
  }

  //ENTER CHAT MADE BY KOALA
  public static void EnterChat(Messenger esql, String authorisedUser){
    try
    {

      ListChats(esql, authorisedUser);

      boolean invalidChatID = true;
      int chatID = -1;
      String chatIDChoice = "";

      while(invalidChatID)
      {
        System.out.print("\tPlease pick a chat ID: ");
        chatIDChoice = in.readLine();

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

      while(inChat)
      {
        ShowChatMessages(esql, authorisedUser, chatID, chatIDChoice, showNumMessages);

        System.out.println("");
        System.out.println("\tChat #" + chatIDChoice + " Options");
        System.out.println("\t=======================");
        System.out.println("\t1. Write a New Message");
        System.out.println("\t2. Delete a Message");
        System.out.println("\t3. Edit a Message");
        System.out.println("\t4. Load Messages");
        System.out.println("\t=======================");
        System.out.println("\t9. Exit Chat");

        switch(readChoice())
        {
          case 1: WriteNewMessage(esql, authorisedUser, chatID); break;
          case 2: DeleteMessage(esql, authorisedUser, chatID); break;
          case 3: EditMessage(esql, authorisedUser, chatID); break;
          case 4: showNumMessages = LoadMessages(showNumMessages); break;
          case 9: inChat = false; break;
                                  
          default : System.out.println("Unrecognized choice!"); break;
        } // end Switch

      } // end while (InChat)

    } // end try

    catch(Exception e)
    {
      System.err.println(e.getMessage());
    }
  } // end EnterChat

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
  
  //SHOW CHAT MESSAGES MADE BY KOALA (this one shows all messages in a given chat)
  public static void ShowChatMessages(Messenger esql, String authorisedUser, int chatID, String chatIDChoice, int showNumMessages)
  {
    String menuTitle = "Chat #" + chatIDChoice + " Messages";
    DisplayMenuTitle(menuTitle);
    String query = "SELECT M.msg_id, M.msg_text, M.msg_timestamp, M.sender_login FROM message M WHERE M.chat_id = '" + chatID + "' ORDER BY M.msg_timestamp DESC LIMIT " + showNumMessages;

    try
    {
      List<List<String>> result = esql.executeQueryAndReturnResult(query);


      if(result.size() == 0)
        System.out.println("This chat has no messages.");

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

  //executeQueryAndReturnResult
  //and then format as you like :D

  }

  //WRITE NEW MESSAGE MADE BY KOALA (writes a new message)
  public static void WriteNewMessage(Messenger esql, String authorisedUser, int chatID){
    String menuTitle = "Write a New Message";
    DisplayMenuTitle(menuTitle);

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

      System.out.println(query2);
      esql.executeUpdate(query2); 
      
      System.out.println("\n\tMessage was sent!");
    }

    catch (Exception e)
    {
      System.err.println (e.getMessage ());
    }

    DisplayEndTitle(menuTitle);
  }

  //DELETE MESSAGE MADE BY KOALA (deletes a given message)
  public static void DeleteMessage(Messenger esql, String authorisedUser, int chatID){

  }

  //EDIT MESSAGE MADE BY KOALA (edits a given message)
  public static void EditMessage(Messenger esql, String authorisedUser, int chatID){

  }

  //LOAD MESSAGES MADE BY KOALA (increments external variable by 10 so outside function will print more messages)
  public static int LoadMessages(int showNumMessages){
    return showNumMessages + 10;
  }


//EXTRA NOTES:
//If you want to get the very last entry of a certain table (using a unique field like chat_id in the chat table for example)
//You can use the following query:
//
//SELECT chat_id FROM chat ORDER BY chat_id DESC LIMIT 1;
//
//In which you can use this in a executeQueryAndReturnResult function call and use the result to get the next ID for a new chat
//Just an example :D


}//end Messenger
