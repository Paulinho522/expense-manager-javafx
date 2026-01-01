package org.example.util;

import org.example.model.Expense;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {

    private static final String DB_PATH = "db/expenses.db"; // caminho persistente

    public static Connection getConnection() {
        try {
            File dbFile = new File(DB_PATH);
            dbFile.getParentFile().mkdirs(); // garante que a pasta exista
            String url = "jdbc:sqlite:" + DB_PATH;
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ligar à base de dados", e);
        }
    }

    public static void initDatabase() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS expenses (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        description TEXT NOT NULL,
                        category TEXT NOT NULL,
                        amount REAL NOT NULL,
                        date TEXT NOT NULL
                    );
                """;


        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql); // só cria se não existir
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar a base de dados", e);
        }
    }

    public static List<Expense> loadExpensesFromDB() {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Expense exp = new Expense(
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("date")) // se guardaste como TEXT
                );
                list.add(exp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
