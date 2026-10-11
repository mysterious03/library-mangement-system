import controller.WebServer;
import database.DatabaseInitializer;

/**
 * Smart Library Management System - Application Entry Point
 * Designed for 4-Member College Semester Project
 * Technology Stack: Pure HTML5 + CSS3 + Java SE 17 + JDBC + SQL/PL-SQL
 */
public class Main {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   SMART LIBRARY MANAGEMENT SYSTEM (SLMS)");
        System.out.println("   College Semester Project - 4 Member Architecture");
        System.out.println("==================================================================");

        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {}
        }
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        try {
            // 1. Initialize Relational Schema, Views & 30+ Sample Books Data
            DatabaseInitializer.initializeDatabase();

            // 2. Start Pure Java Web Server
            WebServer server = new WebServer(port);
            server.start();

            System.out.println("\n>> Ready! Open your browser and navigate to:");
            System.out.println("   http://localhost:" + port);
            System.out.println("\n>> Default Demo Accounts:");
            System.out.println("   [Admin]     admin@library.edu       / password: admin123");
            System.out.println("   [Librarian] librarian@library.edu   / password: librarian123");
            System.out.println("   [Student]   aarav.patel@student.edu / password: student123");
            System.out.println("==================================================================");

            // Keep main thread running
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Fatal application error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
