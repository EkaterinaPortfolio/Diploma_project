package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLGenerator {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLGenerator() {
    }

    private static Connection getConn() throws SQLException {  
        return DriverManager.getConnection(System.getProperty("db.url"), "app", "pass"); 
    }

    @SneakyThrows
    public static String getStatusPayment() {
        var statusSQL = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        return QUERY_RUNNER.query(conn, statusSQL, new ScalarHandler<String>());
    }

    @SneakyThrows
    public static String getStatusPaymentCredit() {
        var statusSQL = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        return QUERY_RUNNER.query(conn, statusSQL, new ScalarHandler<String>());
    }

    @SneakyThrows
    public static String getStatusOrder_entityCredit() {
        var id = "SELECT credit_id FROM order_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        return QUERY_RUNNER.query(conn, id, new ScalarHandler<String>());

    }

    @SneakyThrows
    public static String getStatusOrder_entityPayment() {
        var id = "SELECT payment_id FROM order_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        return QUERY_RUNNER.query(conn, id, new ScalarHandler<String>());
    }

    @SneakyThrows
    public static void cleanDatabase() {
        var connection = getConn();
        QUERY_RUNNER.execute(connection, "DELETE FROM credit_request_entity"); 
        QUERY_RUNNER.execute(connection, "DELETE FROM payment_entity");
        QUERY_RUNNER.execute(connection, "DELETE FROM order_entity");
    }
}
