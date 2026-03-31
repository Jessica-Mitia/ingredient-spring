package org.example.ingredientspring.repository;

import org.example.ingredientspring.entity.DishIngredient;
import org.example.ingredientspring.entity.UnitTypeEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishIngredientRepository {

    private final DataSource dataSource;

    public DishIngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<DishIngredient> findByDishId(Integer dishId) {
        List<DishIngredient> associations = new ArrayList<>();
        String sql = "SELECT * FROM dish_ingredient WHERE id_dish = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    associations.add(mapResultSetToDishIngredient(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding dish ingredients for dish: " + dishId, e);
        }
        return associations;
    }

    public void deleteByDishId(Integer dishId) {
        String sql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting dish ingredients for dish: " + dishId, e);
        }
    }

    public DishIngredient save(DishIngredient di, Integer dishId) {
        String sql = "INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity, unit_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, dishId);
            ps.setInt(2, di.getIngredient().getId());
            ps.setDouble(3, di.getQuantity() != null ? di.getQuantity() : 0.0);
            ps.setString(4, di.getUnitType() != null ? di.getUnitType().name() : null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    di.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving dish ingredient association", e);
        }
        return di;
    }

    private DishIngredient mapResultSetToDishIngredient(ResultSet rs) throws SQLException {
        DishIngredient di = new DishIngredient();
        di.setId(rs.getInt("id"));
        di.setQuantity(rs.getDouble("quantity"));
        String unitStr = rs.getString("unit_type");
        if (unitStr != null) {
            di.setUnitType(UnitTypeEnum.valueOf(unitStr));
        }

        return di;
    }
}
