package com.example.proyecto_pokeapi_joseavn.ui;

import android.app.Application;
import android.os.CountDownTimer;
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
    private MutableLiveData<Boolean> isDuplicate = new MutableLiveData<>(false);

    // Usuario Logueado
    private MutableLiveData<User> currentUser = new MutableLiveData<>();

    // Pokemon final encontrado
    private MutableLiveData<PokemonResponse> wildPokemon = new MutableLiveData<>();

    // VARIABLES NUEVAS PARA LA RULETA
    private MutableLiveData<String> rouletteImage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSearching = new MutableLiveData<>(false);

    // Mensajes para la UI
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

    // GETTERS QUE TE FALTABAN
    public LiveData<String> getRouletteImage() { return rouletteImage; }
    public LiveData<Boolean> getIsSearching() { return isSearching; }
    public LiveData<PokemonResponse> getWildPokemon() { return wildPokemon; }
    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<Boolean> getIsDuplicate() { return isDuplicate; }

    // LÓGICA DE BÚSQUEDA CON RULETA
    public void searchRandomPokemon() {
        wildPokemon.setValue(null);
        isSearching.setValue(true); // Activamos estado de búsqueda

        // Efecto ruleta: dura 2 segundos, cambia cada 150ms
        new CountDownTimer(2000, 150) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Buscamos una imagen temporal
                int tempId = (int) (Math.random() * 800) + 1;
                fetchTempImage(tempId);
            }

            @Override
            public void onFinish() {
                // Al terminar, buscamos el definitivo
                fetchFinalPokemon();
            }
        }.start();
    }

    // Carga imagen temporal para la ruleta
    private void fetchTempImage(int id) {
        api.getPokemon(id).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Solo actualizamos si seguimos en modo búsqueda
                    if (Boolean.TRUE.equals(isSearching.getValue())) {
                        rouletteImage.setValue(response.body().sprites.frontDefault);
                    }
                }
            }
            @Override public void onFailure(Call<PokemonResponse> call, Throwable t) {}
        });
    }

    // Carga el pokemon final
    private void fetchFinalPokemon() {
        int randomId = (int) (Math.random() * 800) + 1;
        api.getPokemon(randomId).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                isSearching.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    PokemonResponse pokemon = response.body();
                    wildPokemon.setValue(pokemon);

                    // Verificacion
                    User user = currentUser.getValue();
                    if (user != null) {
                        // Verificamos si ya lo tiene en la BD
                        boolean tiene = db.appDao().hasPokemon(user.id, pokemon.id);
                        isDuplicate.setValue(tiene);
                    }
                }
            }
            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                isSearching.setValue(false);
                message.setValue("Error de red: " + t.getMessage());
            }
        });
    }

    // INTENTAR CAPTURAR
    public void tryCatchPokemon() {
        PokemonResponse wild = wildPokemon.getValue();
        User user = currentUser.getValue();
        if (wild == null || user == null) return;

        // 1. Verificar si ya lo tiene
        if (db.appDao().hasPokemon(user.id, wild.id)) {
            message.setValue("¡Ya tienes a este Pokemon!");
            return;
        }

        // 2. Verificar poder
        int myTotalPower = db.appDao().getUserTotalPower(user.id);
        int myCount = db.appDao().getUserPokemonCount(user.id);
        int wildPower = wild.getTotalPower();

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

    // GESTIÓN DE USUARIOS
    public void logout() {
        currentUser.setValue(null);
        wildPokemon.setValue(null);
    }

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

    // Metodo para cambiar contraseña
    public void changePassword(String newPass) {
        User user = currentUser.getValue();
        if (user != null) {
            db.appDao().updateUserPassword(user.id, newPass);
            message.setValue("Contraseña actualizada correctamente");
        }
    }

    // Metodo para borrar cuenta
    public void deleteAccount() {
        User user = currentUser.getValue();
        if (user != null) {
            db.appDao().deleteUser(user.id);
            logout(); // Cerramos sesión tras borrar
            message.setValue("Cuenta eliminada. ¡Hasta pronto!");
        }
    }

    // Método para resetear la búsqueda al entrar a la pantalla
    public void clearSearch() {
        wildPokemon.setValue(null);
        isSearching.setValue(false);
    }

    public LiveData<List<PokemonEntity>> getMyPokemons() {
        if (currentUser.getValue() == null) return null;
        return db.appDao().getUserPokemons(currentUser.getValue().id);
    }

    // Método para liberar (borrar) un pokemon
    public void releasePokemon(int id) {
        db.appDao().deletePokemonById(id);
    }
}