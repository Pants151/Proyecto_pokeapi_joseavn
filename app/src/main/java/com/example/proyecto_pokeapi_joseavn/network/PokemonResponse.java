package com.example.proyecto_pokeapi_joseavn.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PokemonResponse {
    public int id;
    public String name;
    public Sprites sprites;
    public List<Stat> stats;
    public List<TypeSlot> types;

    // Calcular poder total sumando stats base
    public int getTotalPower() {
        int total = 0;
        if (stats != null) {
            for (Stat s : stats) total += s.base_stat;
        }
        return total;
    }

    // Obtener primer tipo como String
    public String getPrimaryType() {
        if (types != null && !types.isEmpty()) return types.get(0).type.name;
        return "unknown";
    }

    public static class Sprites {
        @SerializedName("front_default")
        public String frontDefault;
    }

    public static class Stat {
        public int base_stat;
    }

    public static class TypeSlot {
        public Type type;
    }

    public static class Type {
        public String name;
    }
}