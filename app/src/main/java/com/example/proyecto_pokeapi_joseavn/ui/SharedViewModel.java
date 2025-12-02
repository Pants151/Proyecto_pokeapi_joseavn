package com.example.proyecto_pokeapi_joseavn.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyecto_pokeapi_joseavn.data.AppDatabase;
import com.example.proyecto_pokeapi_joseavn.data.PokemonEntity;
import com.example.proyecto_pokeapi_joseavn.data.User;
import com.example.proyecto_pokeapi_joseavn.network.PokeApiService;
import com.example.proyecto_pokeapi_joseavn.network.PokemonResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SharedViewModel extends AndroidViewModel {

    private AppDatabase db;
    private PokeApiService api;

    // Usuario Logueado
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    // Pokemon buscado (Random)
    private MutableLiveData<PokemonResponse> wildPokemon = new MutableLiveData<>();
    // Estado de mensajes (Error/Exito)
    public MutableLiveData<String> message = new MutableLiveData<>();

    public SharedViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(PokeApiService.class);
    }

    //GESTIÓN USUARIOS
    public void login(String username, String pass) {
        User user = db.appDao().login(username, pass);
        if (user != null) {
            currentUser.setValue(user);
        } else {
            message.setValue("Credenciales incorrectas");
        }
    }

    public void register(String username, String pass) {
        if (db.appDao().checkUserExists(username) == null) {
            db.appDao().insertUser(new User(username, pass));
            message.setValue("Registro exitoso. Ahora inicia sesión.");
        } else {
            message.setValue("El usuario ya existe");
        }
    }

    public LiveData<User> getCurrentUser() { return currentUser; }

    //GESTIÓN POKEMONS
    public LiveData<List<PokemonEntity>> getMyPokemons() {
        if (currentUser.getValue() == null) return null;
        return db.appDao().getUserPokemons(currentUser.getValue().id);
    }

    // Buscar Pokemon Aleatorio
    public void searchRandomPokemon() {
        int randomId = (int) (Math.random() * 800) + 1; // Gen 1-7 aprox
        api.getPokemon(randomId).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful()) {
                    wildPokemon.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                message.setValue("Error de red: " + t.getMessage());
            }
        });
    }

    public LiveData<PokemonResponse> getWildPokemon() { return wildPokemon; }

    // Intentar Capturar
    public void tryCatchPokemon() {
        PokemonResponse wild = wildPokemon.getValue();
        User user = currentUser.getValue();
        if (wild == null || user == null) return;

        // 1. Ver si ya lo tiene
        if (db.appDao().hasPokemon(user.id, wild.id)) {
            message.setValue("¡Ya tienes a este Pokemon!");
            return;
        }

        // 2. Lógica de Poder
        int myTotalPower = db.appDao().getUserTotalPower(user.id);
        int myCount = db.appDao().getUserPokemonCount(user.id);
        int wildPower = wild.getTotalPower();

        // "Esta norma solo se rompe si el usuario no tiene ningún pokemon"
        if (myCount == 0 || myTotalPower > wildPower) {
            PokemonEntity newPoke = new PokemonEntity(
                    wild.id, wild.name, wildPower,
                    wild.sprites.frontDefault, wild.getPrimaryType(), user.id
            );
            db.appDao().insertPokemon(newPoke);
            message.setValue("¡Capturado " + wild.name + "!");
        } else {
            message.setValue("Tu equipo es demasiado débil (" + myTotalPower + " vs " + wildPower + ")");
        }
    }
}