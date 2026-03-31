package org.example.ingredientspring.repository;

import org.example.ingredientspring.entity.Order;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final DataSource dataSource;

    public OrderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all orders", e);
        }
        return orders;
    }

    public Optional<Order> findById(Integer id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order by id: " + id, e);
        }
        return Optional.empty();
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            return insert(order);
        } else {
            return update(order);
        }
    }

    private Order insert(Order order) {
        String sql = "INSERT INTO orders (reference, creation_date_time) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getReference());
            ps.setTimestamp(2, order.getCreationDateTime() != null ? Timestamp.from(order.getCreationDateTime()) : Timestamp.from(Instant.now()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting order", e);
        }
        return order;
    }

    private Order update(Order order) {
        String sql = "UPDATE orders SET reference = ?, creation_date_time = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getReference());
            ps.setTimestamp(2, order.getCreationDateTime() != null ? Timestamp.from(order.getCreationDateTime()) : Timestamp.from(Instant.now()));
            ps.setInt(3, order.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order", e);
        }
        return order;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setReference(rs.getString("reference"));
        Timestamp ts = rs.getTimestamp("creation_date_time");
        if (ts != null) {
            order.setCreationDateTime(ts.toInstant());
        }
        return order;
    }
}
