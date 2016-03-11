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
         System.out.println("Done");
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
            System.out.println("\tMAIN MENU");
            System.out.println("\t---------");
            System.out.println("\t1. Create user");
            System.out.println("\t2. Log in");
            System.out.println("\t9. < EXIT");
            String authorisedUser = null;

            switch (readChoice())
            {
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            } //end switch

            if (authorisedUser != null) 
            {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("\n\t\tMAIN MENU");
                System.out.println("\t------------------------");
                System.out.println("\t1. Add a New Contact");
                System.out.println("\t2. Browse Contact List");
                System.out.println("\t3. Write a New Message");
                System.out.println("\t4. Browse Chat List");
                System.out.println("\t5. Block a User");
                System.out.println("\t6. Show Blocked Users");
                System.out.println("\t.........................");
                System.out.println("\t9. Log out");
                switch (readChoice()){
                   case 1: AddToContact(esql,authorisedUser); break;
                   case 2: ListContacts(esql,authorisedUser); break;
                   case 3: NewMessage(esql,authorisedUser); break;
                   case 4: ListChats(esql, authorisedUser); break;
                   case 5: AddToBlock(esql, authorisedUser); break;
                   case 6: ListBlocks(esql, authorisedUser); break;
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
               System.out.println("Done\n\nBye !");
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
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
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
   }
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try
      {
        System.out.print("\tEnter user login: ");
        String login = in.readLine();
        System.out.print("\tEnter user password: ");
        String password = in.readLine();

        String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
        int userNum = esql.executeQuery(query);

        if (userNum > 0){
          return login;
      }else{
        System.out.print("Username or Password Incorrect");
          return null;
        }
      }

      catch(Exception e)
      {
         System.err.println (e.getMessage ());
         return null;
      }
   } 

   public static void AddToContact(Messenger esql, String authorisedUser){
      try{
        System.out.print("\tEnter the contact's login: ");
        String contact = in.readLine();
         
        String query1 = "SELECT * FROM USR WHERE login = '" + contact + "';";
        int userNum = esql.executeQuery(query1);
        if(userNum==0){
          System.out.println("Sorry no one under the name of "+contact);
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
        }
      }
      catch(Exception e){ 
          //If User already in contact list it will tell the User so
      if(e.getMessage().contains("ERROR:  duplicate key violates")){
        System.out.println("User alreadin in contact list");
          }else{
        System.err.println (e.getMessage ());
      }
        }
   }//end
   public static void AddToBlock(Messenger esql,String authorisedUser){
      try{
        System.out.print("\tEnter to block user's login: ");
          String blocker = in.readLine();

          String query1 = "SELECT * FROM USR WHERE login = '" + blocker + "';";
          int userNum = esql.executeQuery(query1);
          if(userNum==0){
            System.out.println("Sorry no one under the name of "+blocker);
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
          }

      }catch(Exception e){
        if(e.getMessage().contains("ERROR:  duplicate key violates")){
        System.out.println("User alreadin in contact list");
          }else{
        System.err.println (e.getMessage ());
      }
      }

   }

   //Try to get this to alphabetical order
   public static void ListContacts(Messenger esql,String authorisedUser){
        try{
          String query = 
          "SELECT ULC.list_member " +
          "FROM USER_LIST_CONTAINS ULC, USR U " + 
          "WHERE U.contact_list = ULC.list_id AND U.login = '" + authorisedUser + "';";

          System.out.print("\n");

          //Returns # of fitting results
          //HAVE TO USE executeQueryAndReturnResult, no not use executeQuery
          List<List<String>> result = esql.executeQueryAndReturnResult(query);
          if(result.size() == 0){
        System.out.println("Sorry you have no friends :(");
          }else{
            String output = "";
            System.out.println("======Contact List=====");
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
    }

    public static void ListBlocks(Messenger esql,String authorisedUser){
      try
      {
        String query = 
        "SELECT ULC.list_member " +
        "FROM USER_LIST_CONTAINS ULC, USR U " + 
        "WHERE U.block_list = ULC.list_id AND U.login = '" + authorisedUser + "';";

        System.out.print("\n");

        //Returns # of fitting results
        //HAVE TO USE executeQueryAndReturnResult, no not use executeQuery
        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        if(result.size() == 0){
      System.out.println("No one blocked");
        }else{
          String output = "";
          System.out.println("======Blocked List=====");
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
      
    }
   public static void ListChats(Messenger esql, String authorisedUser)
   {
      try{
        // For display chats, check if person is member (currentUser) of chat_id.
        // then display all chats according chat_id
        String query = 
        "SELECT C.chat_id, C.chat_type, C.init_sender " + 
        "FROM CHAT C, CHAT_LIST CL " +
        "WHERE C.chat_id = CL.chat_id AND CL.member = '" + authorisedUser + "'";

        List<List<String>> result = esql.executeQueryAndReturnResult(query);
          if(result.size() == 0){
        System.out.println("Sorry you have no chats");
          }else{
            String output = "";
            System.out.println("======Message List=====");
            int count = 0;
            for(List<String> list : result)
            {
              ++count;
              output +="\t"+count+". ";

          for(int i=0;i<list.size();++i){
            if(i==list.size()-1)
              output+=list.get(i).trim();
            else
              output+=list.get(i).trim() + ", ";
          }
          System.out.println(output);
          output="";
        }
            
        }
        System.out.print("\n");
      }

      catch(Exception e)
      {
        System.err.println (e.getMessage ());
      }

   }

        //CHAT INTERFACE MADE BY KOALA
	public static void chatInterface(Messenger esql, String authorisedUser){
		try{
			boolean chatInterfacing = true;

			while(chatInterfacing){
				System.out.println("\tWelcome to the Chat Interface!");
				System.out.println("\t=================================");
				System.out.println("\t1. Create New Chat");
				System.out.println("\t2. Enter Chat");
				System.out.println("\t=================================");
				System.out.println("\t9. Leave Chat Interface");

				switch(readChoice()){
					case 1: CreateChat(esql, authorisedUser); break;
					case 2: EnterChat(esql, authorisedUser); break;
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
                try{
                        System.out.println("Here lies the Create Chat function");
                }
                catch(Exception e){
                        System.err.println(e.getMessage());
                }
        }

        //ENTER CHAT MADE BY KOALA
        public static void EnterChat(Messenger esql, String authorisedUser){
                try{
                        System.out.println("\tHere's a list of your chats");
                        System.out.println("\t=================================");
                        ListChats(esql, authorisedUser);

                        boolean pickingChatID = true;
                        int chatID = -1;

                        while(pickingChatID){
                                System.out.println("\tPlease pick a chat ID: ");
                                String chatIDChoice = in.readLine();

                                String checkChatIDExistsQuery = "SELECT chat_id FROM chat_list WHERE member = '" + authorisedUser + "' AND chat_id = '" + chatIDChoice + "'";
                                int chatCount = esql.executeQuery(checkChatIDExistsQuery);

                                if(chatCount == 1){
                                        chatID = Integer.parseInt(chatIDChoice);
                                        pickingChatID = false;
                                }
                                else{
                                        System.out.println("\tInvalid ID, please pick another!\n");
                                }
                        }

			int showNumMessages = 10;

			boolean InChat = true;

			while(InChat){
				showChatMessages(esql, authorisedUser, chatID, showNumMessages);

	                        System.out.println("");
        	                System.out.println("\tChat Options");
				System.out.println("\t=======================");
                	        System.out.println("\t1. Write New Message");
                		System.out.println("\t2. Remove A Message");
                       		System.out.println("\t3. Change A Message");
                        	System.out.println("\t4. Load Messages");
                        	System.out.println("\t=======================");
                        	System.out.println("\t9. Exit Chat");

                        	switch(readChoice()){
                                	case 1: writeNewMessage(esql, authorisedUser, chatID); break;
                                	case 2: deleteMessage(esql, authorisedUser, chatID); break;
                                	case 3: editMessage(esql, authorisedUser, chatID); break;
                                	case 4: showNumMessages = loadMessages(showNumMessages); break;
                                	case 9: InChat = false; break;
                                	
					default : System.out.println("Unrecognized choice!"); break;
                        	}

			}

                }
                catch(Exception e){
                        System.err.println(e.getMessage());
                }
        }
	
	//SHOW CHAT MESSAGES MADE BY KOALA (this one shows all messages in a given chat)
	public static void showChatMessages(Messenger esql, String authorisedUser, int chatID, int showNumMessages){
		String messageDisplayQuery = "SELECT M.msg_id, M.msg_text, M.msg_timestamp, M.sender_login FROM message M WHERE M.chat_id = '" + chatID + "' ORDER BY M.msg_timestamp DESC LIMIT " + showNumMessages;

	//executeQueryAndReturnResult
	//and then format as you like :D

	}

	//WRITE NEW MESSAGE MADE BY KOALA (writes a new message)
	public static void writeNewMessage(Messenger esql, String authorisedUser, int chatID){

	}

	//DELETE MESSAGE MADE BY KOALA (deletes a given message)
	public static void deleteMessage(Messenger esql, String authorisedUser, int chatID){

	}

	//EDIT MESSAGE MADE BY KOALA (edits a given message)
	public static void editMessage(Messenger esql, String authorisedUser, int chatID){

	}

	//LOAD MESSAGES MADE BY KOALA (increments external variable by 10 so outside function will print more messages)
	public static int loadMessages(int showNumMessages){
		newNumMessages = showNumMessages + 10;
		return NewNumMessages;
	}

}//end Messenger


//EXTRA NOTES:
//If you want to get the very last entry of a certain table (using a unique field like chat_id in the chat table for example)
//You can use the following query:
//
//SELECT chat_id FROM chat ORDER BY chat_id DESC LIMIT 1;
//
//In which you can use this in a executeQueryAndReturnResult function call and use the result to get the next ID for a new chat
//Just an example :D

//TIPS PART 2!!!!!!!!!!!!!!!!!!!!!!!!!!!

//THIS IS THE ALGORITHM FOR DELETE USER FUNCTION
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//Have a flag that contains external boolean value of usermenu
        //Have a flag that checks if any of the two conditions below have failed

        //1. Check if user is an owner of any chat (check init_sender in chat)
                //Set flag to true if yes

        //2. Check if user has written any messages (check sender_login in message)
                //Set flag to true if yes

        //Check flag
                //If true, then return out of function

                //If false, delete in the following order:

                        //Make sure you get block list and contact list IDs

                        //1. chat_list (delete the user from chat rooms)
                        //2. user_list_contains (delete the user from any contact/block lists using list_member)
                        //3. user_list_contains (delete the user's contact/block list using list_id)
                        //4. user_list (delete the user's contact/block list IDs using list_id)
                        //5. usr (delete the user using login)

                //Set usermenu variable to false and return it
