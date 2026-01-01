package org.example.repository;

import org.example.model.Expense;
import org.example.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ExpenseRepository {

    private static final String INSERT_SQL = """
        INSERT INTO expenses (description, category, amount, date)
        VALUES (?, ?, ?, ?)
    """;

    public void save(Expense expense) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, expense.getDescription());
            ps.setString(2, expense.getCategory());
            ps.setDouble(3, expense.getAmount());
            ps.setString(4, expense.getDate().toString());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao guardar despesa", e);
        }
    }
}
