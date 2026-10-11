package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Automates Database Schema & Sample Data initialization on startup.
 * Member 3: Database & SQL/PLSQL Setup.
 */
public class DatabaseInitializer {

    public static void initializeDatabase() {
        System.out.println("Checking database schema and connectivity...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean tablesExist = checkIfTablesExist(conn);
            if (!tablesExist) {
                System.out.println("Initializing database tables from schema.sql...");
                executeSqlScript(conn, "sql/schema.sql");
                System.out.println("Populating sample data from sample_data.sql...");
                executeSqlScript(conn, "sql/sample_data.sql");
                System.out.println("Database successfully initialized with 30+ books and realistic records!");
            } else {
                System.out.println("Database already initialized. Ready for operations.");
            }
        } catch (Exception e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean checkIfTablesExist(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM USERS");
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            // Table doesn't exist yet
            return false;
        }
        return false;
    }

    public static void executeSqlScript(Connection conn, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("SQL file not found: " + filePath);
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Skip empty lines and SQL comment lines
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("//")) {
                    continue;
                }
                sb.append(line).append("\n");
            }

            // Split into statements by semicolon
            String[] statements = sb.toString().split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String rawSql : statements) {
                    String sql = rawSql.trim();
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException ex) {
                            // Print warning for non-fatal errors (e.g. drop table if not exists)
                            if (!sql.toUpperCase().startsWith("DROP")) {
                                System.err.println("Warning executing SQL: " + ex.getMessage() + "\nStatement: " + sql);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed reading SQL script " + filePath + ": " + e.getMessage());
        }
    }
}
