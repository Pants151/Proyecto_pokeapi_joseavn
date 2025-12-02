package com.example.proyecto_pokeapi_joseavn.ui;

import android.app.AlertDialog; // Importante
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Importante
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.proyecto_pokeapi_joseavn.databinding.FragmentDetailBinding;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;
    private SharedViewModel viewModel;
    private int pokemonId = -1; // Para guardar el ID

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        if (getArguments() != null) {
            // RECUPERAR EL ID
            pokemonId = getArguments().getInt("id", -1);

            String name = getArguments().getString("name");
            int power = getArguments().getInt("power");
            String type = getArguments().getString("type");
            String image = getArguments().getString("image");

            binding.tvDetailName.setText(name);
            binding.tvDetailPower.setText("Poder: " + power);
            binding.tvDetailType.setText("Tipo: " + type);
            Glide.with(this).load(image).into(binding.imgDetail);
        }

        binding.btnBack.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });

        // LÓGICA BOTÓN ABANDONAR
        binding.btnRelease.setOnClickListener(v -> {
            if (pokemonId != -1) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Abandonar Pokemon")
                        .setMessage("¿Estás seguro de que quieres abandonar a " + binding.tvDetailName.getText() + "? Esta acción no se puede deshacer.")
                        .setPositiveButton("Sí, adiós", (dialog, which) -> {
                            // 1. Borrar de la BD
                            viewModel.releasePokemon(pokemonId);
                            Toast.makeText(getContext(), "Has abandonado al pokemon...", Toast.LENGTH_SHORT).show();

                            // 2. Volver al listado
                            Navigation.findNavController(view).popBackStack();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
}