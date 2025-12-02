package com.example.proyecto_pokeapi_joseavn.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Importamos Glide
import com.example.proyecto_pokeapi_joseavn.R;
import com.example.proyecto_pokeapi_joseavn.data.PokemonEntity;
import java.util.ArrayList;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder> {

    private List<PokemonEntity> list = new ArrayList<>();
    private final OnItemClick listener;

    public interface OnItemClick {
        void onClick(PokemonEntity pokemon);
    }

    public PokemonAdapter(OnItemClick listener) {
        this.listener = listener;
    }

    public void setList(List<PokemonEntity> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PokemonEntity poke = list.get(position);

        holder.tvName.setText(poke.name);
        holder.tvPower.setText("Poder: " + poke.power);

        // Cargar imagen pequeña con Glide
        Glide.with(holder.itemView.getContext())
                .load(poke.imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.ivImage);

        holder.itemView.setOnClickListener(v -> listener.onClick(poke));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPower;
        ImageView ivImage; // Referencia a la imagen

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvPokeName);
            tvPower = v.findViewById(R.id.tvPokePower);
            ivImage = v.findViewById(R.id.ivPokeItem); // Enlazamos el ID del XML
        }
    }
}