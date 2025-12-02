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
}