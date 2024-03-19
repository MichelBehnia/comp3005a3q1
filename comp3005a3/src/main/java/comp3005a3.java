import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class comp3005a3 {
    //JDBC postgres set up
    static String url = "jdbc:postgresql://localhost:5432/comp3005a3q1";
    static String user = "postgres";
    static String password = "admin";
    static Connection connection;
    static Scanner myObj = new Scanner(System.in);
    static {
        try {
            connection = DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //menu helper function displays the functions the program offers to manipulate the students table within the connected database with proper error handling to ensure a valid selection is being made
    static int menu(){
        System.out.println("What would you like to do with your local postgresql database?");
        System.out.println("1: Display all students");
        System.out.println("2: Add a new student");
        System.out.println("3: Update a students email");
        System.out.println("4: Delete a student");

        int userSelection = Integer.parseInt(myObj.nextLine());
        while((int) userSelection > 4 || (int) userSelection < 1){
            System.out.println("Please make a selection between 1-4");
            userSelection = Integer.parseInt(myObj.nextLine());
        }
        return userSelection;
    }


    //getAllStudents function prints all students to the console
    static void getAllStudents() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeQuery("SELECT * FROM students");
        ResultSet resultSet = statement.getResultSet();
        while(resultSet.next()){
            System.out.print(resultSet.getString("student_id") + " \t");
            System.out.print(resultSet.getString("first_name") + " \t");
            System.out.print(resultSet.getString("last_name") + " \t");
            System.out.print(resultSet.getString("email") + " \t");
            System.out.println(resultSet.getString("enrollment_date"));
        }
    }

    //addStudent function adds a new student to the students table with the respective attributes being passed in through its parameters
    static void addStudent(String first_name, String last_name, String email, Date enrollment_date) throws SQLException {

        PreparedStatement statement = connection.prepareStatement("INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES ( ?, ?, ?, ?)");
        statement.setString(1, first_name);
        statement.setString(2, last_name);
        statement.setString(3, email);
        statement.setDate(4, enrollment_date);
        statement.execute();
    }

    //updateStudentEmail function updates a students email with a new email within the students table based on their student_id attribute which is being passed in through its parameters
    static void updateStudentEmail(int student_id, String new_email) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeQuery("UPDATE students SET email = '" + new_email + "' WHERE student_id = " + student_id);
    }

    //deleteStudent function deletes a student tuple based on the corresponding given student_id attribute which is being passed in through its parameters
    static void deleteStudent(int student_id) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeQuery("DELETE FROM students WHERE student_id = " + student_id);
    }


    //handleSelection helper function takes in an integer through its parameters and handles all user inputs along with calling the corresponding function that the user has selected
    static void handleSelection(int userSelection) throws SQLException {
        if(userSelection == 1){
            getAllStudents();
        } else if(userSelection == 2){
            System.out.println("Enter first name of new student:");
            String first_name = myObj.nextLine();

            System.out.println("Enter last name of new student:");
            String last_name = myObj.nextLine();

            System.out.println("Enter the new student's email:");
            String email = myObj.nextLine();

            long millis = System.currentTimeMillis();
            java.sql.Date enrollment_date = new java.sql.Date(millis);

            addStudent(first_name, last_name, email, enrollment_date);
        } else if(userSelection == 3){
            System.out.println("Enter the student_id of the student whose email you're wanting to update:");
            int student_id = Integer.parseInt(myObj.nextLine());
            System.out.println("Enter the student's new email:");
            String new_email = myObj.nextLine();

            updateStudentEmail(student_id,new_email);
        } else if(userSelection == 4){
            System.out.println("Enter the student_id of the student you would like to delete");
            int student_id = Integer.parseInt(myObj.nextLine());

            deleteStudent(student_id);
        }
    }

    //main function runs the program
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

            Class.forName("org.postgresql.Driver");
            System.out.println("Connected to the database\n");
            if(connection != null){
                while(true){
                    int userSelection = menu();

                    try {
                        handleSelection(userSelection);
                    } catch (SQLException e) {
                        int newUserSelection = menu();
                        handleSelection(newUserSelection);
                    }
                }

            } else{
                System.out.println("Failed to connect to the database");
            }

    }
}
