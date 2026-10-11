package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager using standard JDBC.
 * Member 3: Database & JDBC Architecture.
 * Supports embedded zero-configuration pure Java H2 engine (default)
 * as well as MySQL and Oracle by switching configuration constants.
 */
public class DatabaseConnection {

    private static String getEnv(String key, String def) {
        String val = System.getenv(key);
        return (val != null && !val.trim().isEmpty()) ? val.trim() : def;
    }

    // Database Configuration:
    // Mode 1 (Default): Pure-Java Embedded H2 (Zero external server needed, fully ACID compliant)
    private static final String DB_TYPE = getEnv("DB_TYPE", "H2");
    
    // H2 Parameters
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = getEnv("H2_URL", "jdbc:h2:./library_db;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=YEAR,MONTH");
    private static final String H2_USER = getEnv("H2_USER", "sa");
    private static final String H2_PASSWORD = getEnv("H2_PASSWORD", "");

    // MySQL Parameters (Optional for external MySQL server)
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = getEnv("MYSQL_URL", "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String MYSQL_USER = getEnv("MYSQL_USER", "root");
    private static final String MYSQL_PASSWORD = getEnv("MYSQL_PASSWORD", "password");

    // Oracle Parameters (Optional for external Oracle XE server)
    private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String ORACLE_URL = getEnv("ORACLE_URL", "jdbc:oracle:thin:@localhost:1521:xe");
    private static final String ORACLE_USER = getEnv("ORACLE_USER", "system");
    private static final String ORACLE_PASSWORD = getEnv("ORACLE_PASSWORD", "oracle");

    static {
        try {
            if ("MYSQL".equalsIgnoreCase(DB_TYPE)) {
                Class.forName(MYSQL_DRIVER);
            } else if ("ORACLE".equalsIgnoreCase(DB_TYPE)) {
                Class.forName(ORACLE_DRIVER);
            } else {
                Class.forName(H2_DRIVER);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver class not found: " + e.getMessage());
        }
    }

    /**
     * Obtains a new JDBC Connection instance.
     */
    public static Connection getConnection() throws SQLException {
        if ("MYSQL".equalsIgnoreCase(DB_TYPE)) {
            return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        } else if ("ORACLE".equalsIgnoreCase(DB_TYPE)) {
            return DriverManager.getConnection(ORACLE_URL, ORACLE_USER, ORACLE_PASSWORD);
        } else {
            return DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
        }
    }

    /**
     * Safely closes a JDBC connection.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }
}
