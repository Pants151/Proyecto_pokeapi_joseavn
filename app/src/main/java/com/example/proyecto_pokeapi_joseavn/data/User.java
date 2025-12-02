package com.example.proyecto_pokeapi_joseavn.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String username;
    @NonNull
    public String password;

    public User(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }
}