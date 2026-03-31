package org.example.ingredientspring.repository;

import org.example.ingredientspring.entity.Dish;
import org.example.ingredientspring.entity.DishTypeEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {

    private final DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Dish> findAll() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM dish";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dishes.add(mapResultSetToDish(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all dishes", e);
        }
        return dishes;
    }

    public Optional<Dish> findById(Integer id) {
        String sql = "SELECT * FROM dish WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDish(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding dish by id: " + id, e);
        }
        return Optional.empty();
    }

    public Dish save(Dish dish) {
        if (dish.getId() == null) {
            return insert(dish);
        } else {
            return update(dish);
        }
    }

    private Dish insert(Dish dish) {
        String sql = "INSERT INTO dish (name, dish_type, price) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dish.getName());
            ps.setString(2, dish.getDishType() != null ? dish.getDishType().name() : null);
            ps.setDouble(3, dish.getPrice() != null ? dish.getPrice() : 0.0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    dish.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting dish", e);
        }
        return dish;
    }

    private Dish update(Dish dish) {
        String sql = "UPDATE dish SET name = ?, dish_type = ?, price = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dish.getName());
            ps.setString(2, dish.getDishType() != null ? dish.getDishType().name() : null);
            ps.setDouble(3, dish.getPrice() != null ? dish.getPrice() : 0.0);
            ps.setInt(4, dish.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dish", e);
        }
        return dish;
    }

    private Dish mapResultSetToDish(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        String typeStr = rs.getString("dish_type");
        if (typeStr != null) {
            dish.setDishType(DishTypeEnum.valueOf(typeStr));
        }
        dish.setPrice(rs.getDouble("price"));
        return dish;
    }
}
