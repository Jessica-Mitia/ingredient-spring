package org.example.ingredientspring.repository;

import org.example.ingredientspring.entity.MovementTypeEnum;
import org.example.ingredientspring.entity.StockMovement;
import org.example.ingredientspring.entity.StockValue;
import org.example.ingredientspring.entity.UnitTypeEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StockMovementRepository {

    private final DataSource dataSource;

    public StockMovementRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<StockMovement> findAll() {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM stock_movement";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movements.add(mapResultSetToStockMovement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all stock movements", e);
        }
        return movements;
    }

    public List<StockMovement> findByIngredientId(Integer ingredientId) {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM stock_movement WHERE id_ingredient = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movements.add(mapResultSetToStockMovement(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding stock movements for ingredient: " + ingredientId, e);
        }
        return movements;
    }

    public StockMovement save(StockMovement movement, Integer ingredientId) {
        if (movement.getId() == null) {
            return insert(movement, ingredientId);
        } else {
            return update(movement, ingredientId);
        }
    }

    private StockMovement insert(StockMovement movement, Integer ingredientId) {
        String sql = "INSERT INTO stock_movement (id_ingredient, quantity, unit, type, creation_date_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ingredientId);
            ps.setDouble(2, movement.getValue() != null ? movement.getValue().getQuantity() : 0.0);
            ps.setString(3, movement.getValue() != null && movement.getValue().getUnit() != null ? movement.getValue().getUnit().name() : null);
            ps.setString(4, movement.getType() != null ? movement.getType().name() : null);
            ps.setTimestamp(5, movement.getCreationDateTime() != null ? Timestamp.from(movement.getCreationDateTime()) : Timestamp.from(Instant.now()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    movement.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting stock movement", e);
        }
        return movement;
    }

    private StockMovement update(StockMovement movement, Integer ingredientId) {
        String sql = "UPDATE stock_movement SET id_ingredient = ?, quantity = ?, unit = ?, type = ?, creation_date_time = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ps.setDouble(2, movement.getValue() != null ? movement.getValue().getQuantity() : 0.0);
            ps.setString(3, movement.getValue() != null && movement.getValue().getUnit() != null ? movement.getValue().getUnit().name() : null);
            ps.setString(4, movement.getType() != null ? movement.getType().name() : null);
            ps.setTimestamp(5, movement.getCreationDateTime() != null ? Timestamp.from(movement.getCreationDateTime()) : Timestamp.from(Instant.now()));
            ps.setInt(6, movement.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating stock movement", e);
        }
        return movement;
    }

    private StockMovement mapResultSetToStockMovement(ResultSet rs) throws SQLException {
        StockMovement movement = new StockMovement();
        movement.setId(rs.getInt("id"));
        StockValue value = new StockValue();
        value.setQuantity(rs.getDouble("quantity"));
        String unitStr = rs.getString("unit");
        if (unitStr != null) {
            value.setUnit(UnitTypeEnum.valueOf(unitStr));
        }
        movement.setValue(value);
        String typeStr = rs.getString("type");
        if (typeStr != null) {
            movement.setType(MovementTypeEnum.valueOf(typeStr));
        }
        Timestamp ts = rs.getTimestamp("creation_date_time");
        if (ts != null) {
            movement.setCreationDateTime(ts.toInstant());
        }
        return movement;
    }
}
