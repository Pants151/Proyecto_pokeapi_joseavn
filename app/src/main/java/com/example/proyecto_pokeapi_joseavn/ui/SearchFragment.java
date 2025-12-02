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
import com.example.proyecto_pokeapi_joseavn.R;
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

        // 1. Limpiar estado al entrar para obligar a buscar manualmente
        // Solo limpiamos si acabamos de entrar (para evitar borrarlo si rotamos pantalla)
        if (savedInstanceState == null) {
        }

        // --- NUEVO: Observar el estado de búsqueda (Ruleta) ---
        viewModel.getIsSearching().observe(getViewLifecycleOwner(), isSearching -> {
            if (isSearching) {
                // ESTADO: GIRANDO RULETA
                binding.btnInitialSearch.setEnabled(false); // Desactivar botones
                binding.btnRetry.setEnabled(false);
                binding.btnCatch.setEnabled(false);
                binding.pokeName.setText("Buscando...");
                binding.pokePower.setText("");
            } else {
                // ESTADO: BÚSQUEDA TERMINADA
                binding.btnInitialSearch.setEnabled(true); // Reactivar botones
                binding.btnRetry.setEnabled(true);
                binding.btnCatch.setEnabled(true);
            }
        });

        // --- NUEVO: Observar las imágenes de la ruleta ---
        viewModel.getRouletteImage().observe(getViewLifecycleOwner(), imageUrl -> {
            if (Boolean.TRUE.equals(viewModel.getIsSearching().getValue()) && imageUrl != null) {
                // Cargar la imagen temporal de la ruleta
                Glide.with(this).load(imageUrl).into(binding.pokeImage);
            }
        });

        // Observar el Pokemon final
        viewModel.getWildPokemon().observe(getViewLifecycleOwner(), pokemon -> {
            // Solo actualizar si NO estamos buscando (para que no parpadee al final)
            if (Boolean.FALSE.equals(viewModel.getIsSearching().getValue())) {
                if (pokemon != null) {
                    // ESTADO: POKEMON ENCONTRADO FINAL
                    binding.btnInitialSearch.setVisibility(View.GONE);
                    binding.layoutActions.setVisibility(View.VISIBLE);

                    binding.pokeName.setText(pokemon.name);
                    binding.pokePower.setText("Poder: " + pokemon.getTotalPower());

                    Glide.with(this)
                            .load(pokemon.sprites.frontDefault)
                            .placeholder(R.drawable.pokeball_placeholder) // Placeholder mientras carga el final
                            .into(binding.pokeImage);
                } else if (!Boolean.TRUE.equals(viewModel.getIsSearching().getValue())) {
                    // ESTADO: INICIAL (Solo si no se está buscando)
                    binding.btnInitialSearch.setVisibility(View.VISIBLE);
                    binding.layoutActions.setVisibility(View.GONE);

                    binding.pokeName.setText("¿?");
                    binding.pokePower.setText("");
                    binding.pokeImage.setImageResource(R.drawable.pokeball_placeholder);
                }
            }
        });

        // 3. Listeners de los Botones

        // Botón: BUSCAR POKEMON ALEATORIO (Inicial)
        binding.btnInitialSearch.setOnClickListener(v -> {
            binding.pokeName.setText("Buscando..."); // Feedback visual inmediato
            viewModel.searchRandomPokemon();
        });

        // Botón: VOLVER A BUSCAR (Si no te gusta el que ha salido)
        binding.btnRetry.setOnClickListener(v -> {
            binding.pokeName.setText("Buscando...");
            viewModel.searchRandomPokemon();
        });

        // Botón: CAPTURAR
        binding.btnCatch.setOnClickListener(v -> {
            viewModel.tryCatchPokemon();
        });

        // Botón: VER MIS POKEMONS (Volver atrás)
        binding.btnBackToList.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });

        // 4. Mensajes (Toast)
        viewModel.message.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                if (msg.contains("Capturado")) {
                    Navigation.findNavController(view).popBackStack();
                    viewModel.message.setValue("");
                }
            }
        });
    }
}