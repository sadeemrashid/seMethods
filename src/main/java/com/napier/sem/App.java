package com.napier.sem;
import java.sql.*;
import java.util.ArrayList;

public class App {

    private Connection connection = null;  // Initialise connection parameter

    public static void main(String[] args) {
        App app = new App(); // Create a new app instance
        app.connect(); // Call the connect method to connect to the MySQL DB

        ArrayList<Employee> employees = app.getSalaries();
        System.out.println(employees.size());

        app.printSalaries(employees);
        app.disconnect();
    }

    private void connect() {
        try {

            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

            System.out.println("Could not load  driver");
            System.exit(-1);
        }

        int retries = 10;

        for (int i = 0; i < retries; ++i) { // Loop over

            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(30000); // Sleep for some time
                // Connect to database
                connection = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;

            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());

            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    private void disconnect() {
        if (connection != null) {
            try {
                // Close connection
                connection.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID) { // Method to get the specific employee
        try {
            // Create an SQL statement
            Statement stmt = connection.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect); // Execute the query by creating a result set
            // Return new employee if valid.
            // Check one is returned
            if (rset.next()) {

                Employee emp = new Employee(); // Create instance of employee
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");

                emp.last_name = rset.getString("last_name");
                return emp;  // Return the employee

            } else

                return null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public ArrayList<Employee> getSalaries() { // Routine to get the salaries of employees
        boolean isAdded = false;
        try {
            Statement statement = connection.createStatement(); // Create the SQL statement

            String selectQuery =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC"; // SQL Query to retrieve the salaries

            ResultSet set = statement.executeQuery(selectQuery); // Execute the query
            ArrayList<Employee> employeesList = new ArrayList<Employee>();

            while (set.next()) {  // Loop over the result set
                Employee employee = new Employee();
                employee.emp_no = set.getInt("employees.emp_no");
                employee.first_name = set.getString("employees.first_name");
                employee.last_name = set.getString("employees.last_name");

                employee.salary = set.getInt("salaries.salary");

                employeesList.add(employee);
                isAdded = true;
            }
            return employeesList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public void printSalaries(ArrayList<Employee> employees) { // Routine that prints the salaries of the Employees
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));

        for (Employee emp : employees) { // Loop over the array list

            String employee_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(employee_string);
        }
    }
}