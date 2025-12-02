package com.example.proyecto_pokeapi_joseavn.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "pokemons",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
public class PokemonEntity {
    @PrimaryKey(autoGenerate = true)
    public int id; // ID interno de la BD

    public int pokeApiId; // ID oficial del Pokemon (para evitar duplicados)
    public String name;
    public int power; // base_stat acumulado o base_experience
    public String imageUrl;
    public String type;
    public int userId; // Dueño del pokemon

    public PokemonEntity(int pokeApiId, String name, int power, String imageUrl, String type, int userId) {
        this.pokeApiId = pokeApiId;
        this.name = name;
        this.power = power;
        this.imageUrl = imageUrl;
        this.type = type;
        this.userId = userId;
    }
}