package com.example.proyecto_pokeapi_joseavn.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppDao {
    //USUARIOS
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User checkUserExists(String username);

    //POKEMONS
    @Insert
    void insertPokemon(PokemonEntity pokemon);

    @Query("SELECT * FROM pokemons WHERE userId = :userId")
    LiveData<List<PokemonEntity>> getUserPokemons(int userId);

    @Query("SELECT SUM(power) FROM pokemons WHERE userId = :userId")
    int getUserTotalPower(int userId);

    @Query("SELECT COUNT(*) FROM pokemons WHERE userId = :userId")
    int getUserPokemonCount(int userId);

    @Query("SELECT COUNT(*) FROM pokemons WHERE userId = :userId AND pokeApiId = :pokeApiId")
    boolean hasPokemon(int userId, int pokeApiId);

    // Borrar un pokemon por su ID de base de datos
    @Query("DELETE FROM pokemons WHERE id = :id")
    void deletePokemonById(int id);

    // Borrar usuarios
    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUser(int userId);

    // Actualizar usuarios
    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    void updateUserPassword(int userId, String newPassword);
}