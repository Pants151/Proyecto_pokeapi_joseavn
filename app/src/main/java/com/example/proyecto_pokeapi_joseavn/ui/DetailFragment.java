package com.example.proyecto_pokeapi_joseavn.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.proyecto_pokeapi_joseavn.databinding.FragmentDetailBinding;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperar argumentos pasados desde el ListFragment (nav_graph)
        if (getArguments() != null) {
            String name = getArguments().getString("name");
            int power = getArguments().getInt("power");
            String type = getArguments().getString("type");
            String image = getArguments().getString("image");

            binding.tvDetailName.setText(name);
            binding.tvDetailPower.setText("Poder: " + power);
            binding.tvDetailType.setText("Tipo: " + type);

            Glide.with(this).load(image).into(binding.imgDetail);
        }
    }
}