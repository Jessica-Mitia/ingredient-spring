package org.example.ingredientspring.repository;

import org.example.ingredientspring.entity.CategoryEnum;
import org.example.ingredientspring.entity.Ingredient;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM ingredient";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ingredients.add(mapResultSetToIngredient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all ingredients", e);
        }
        return ingredients;
    }

    public Optional<Ingredient> findById(Integer id) {
        String sql = "SELECT * FROM ingredient WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToIngredient(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding ingredient by id: " + id, e);
        }
        return Optional.empty();
    }

    public Ingredient save(Ingredient ingredient) {
        if (ingredient.getId() == null) {
            return insert(ingredient);
        } else {
            return update(ingredient);
        }
    }

    private Ingredient insert(Ingredient ingredient) {
        String sql = "INSERT INTO ingredient (name, price, category) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getPrice());
            ps.setString(3, ingredient.getCategory() != null ? ingredient.getCategory().name() : null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ingredient.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting ingredient", e);
        }
        return ingredient;
    }

    private Ingredient update(Ingredient ingredient) {
        String sql = "UPDATE ingredient SET name = ?, price = ?, category = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getPrice());
            ps.setString(3, ingredient.getCategory() != null ? ingredient.getCategory().name() : null);
            ps.setInt(4, ingredient.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating ingredient", e);
        }
        return ingredient;
    }

    private Ingredient mapResultSetToIngredient(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("id"));
        ingredient.setName(rs.getString("name"));
        ingredient.setPrice(rs.getDouble("price"));
        String categoryStr = rs.getString("category");
        if (categoryStr != null) {
            ingredient.setCategory(CategoryEnum.valueOf(categoryStr));
        }
        return ingredient;
    }
}
