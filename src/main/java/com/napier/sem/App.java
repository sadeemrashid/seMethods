package com.napier.sem;


import java.sql.*;

public class App {

    private Connection connection = null;

    public static void main(String[] args) {
        App app = new App();
        app.connect();
        Employee emp = app.getEmployee(255530);
        app.displayEmployee(emp);
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
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(30000);
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

    public Employee getEmployee(int ID) {
        try {
            Statement statement = connection.createStatement();

            String selectString = "SELECT  emp_no, first_name," +
                    "last_name" + "FROM employees"
                    + "WHERE emp_no = " + ID;

            ResultSet set = statement.executeQuery(selectString);

            if (set.next()) {
                Employee emp = new Employee();
                emp.emp_no = set.getInt("emp_no");
                emp.first_name = set.getString("first_name");
                emp.last_name = set.getString("last_name");

                return emp;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(emp.emp_no + " "
                    + emp.first_name);
        }
    }
}
