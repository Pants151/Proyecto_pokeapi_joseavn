package com.example.proyecto_pokeapi_joseavn.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.proyecto_pokeapi_joseavn.R; // Asegúrate de importar tu R
import com.example.proyecto_pokeapi_joseavn.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SharedViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 1. Cargar un Pokemon aleatorio automáticamente al entrar
        viewModel.searchRandomPokemon();

        // 2. Observar el Pokemon "salvaje" cargado desde la API
        viewModel.getWildPokemon().observe(getViewLifecycleOwner(), pokemon -> {
            if (pokemon != null) {
                binding.pokeName.setText(pokemon.name);
                binding.pokePower.setText("Poder: " + pokemon.getTotalPower());

                // Usamos Glide para cargar la imagen desde la URL
                Glide.with(this)
                        .load(pokemon.sprites.frontDefault)
                        .placeholder(R.mipmap.ic_launcher) // Imagen mientras carga
                        .into(binding.pokeImage);
            }
        });

        // 3. Observar mensajes (Éxito de captura o error de "demasiado débil")
        viewModel.message.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                // Si el mensaje indica captura exitosa, volvemos a la lista
                if (msg.contains("Capturado")) {
                    Navigation.findNavController(view).popBackStack();
                    viewModel.message.setValue(""); // Limpiar mensaje para que no salte de nuevo
                }
            }
        });

        // 4. Botón CAPTURAR -> Llama a la lógica en el ViewModel
        binding.btnCatch.setOnClickListener(v -> {
            viewModel.tryCatchPokemon();
        });
    }
}