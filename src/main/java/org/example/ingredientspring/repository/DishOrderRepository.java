package org.example.ingredientspring.repository;

import org.example.ingredientspring.entity.DishOrder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishOrderRepository {

    private final DataSource dataSource;

    public DishOrderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<DishOrder> findByOrderId(Integer orderId) {
        List<DishOrder> dishOrders = new ArrayList<>();
        String sql = "SELECT * FROM dish_order WHERE id_order = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dishOrders.add(mapResultSetToDishOrder(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding dish orders for order: " + orderId, e);
        }
        return dishOrders;
    }

    public void deleteByOrderId(Integer orderId) {
        String sql = "DELETE FROM dish_order WHERE id_order = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting dish orders for order: " + orderId, e);
        }
    }

    public DishOrder save(DishOrder do_ent, Integer orderId) {
        String sql = "INSERT INTO dish_order (id_order, id_dish, quantity) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orderId);
            ps.setInt(2, do_ent.getDish().getId());
            ps.setInt(3, do_ent.getQuantity() != null ? do_ent.getQuantity() : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    do_ent.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving dish order association", e);
        }
        return do_ent;
    }

    private DishOrder mapResultSetToDishOrder(ResultSet rs) throws SQLException {
        DishOrder do_ent = new DishOrder();
        do_ent.setId(rs.getInt("id"));
        do_ent.setQuantity(rs.getInt("quantity"));
        // Dish and Order objects will be partially populated or populated by service
        return do_ent;
    }
}
