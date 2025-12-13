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

        // Limpiar estado al entrar para obligar a buscar manualmente
        if (savedInstanceState == null) {
            viewModel.clearSearch();
        }

        // OBSERVAR ESTADO DE BÚSQUEDA
        viewModel.getIsSearching().observe(getViewLifecycleOwner(), isSearching -> {
            if (isSearching) {
                // GIRANDO RULETA
                binding.btnInitialSearch.setEnabled(false);
                binding.btnRetry.setEnabled(false);
                binding.btnCatch.setEnabled(false); // Bloqueamos captura mientras busca
                binding.pokeName.setText("Buscando...");
                binding.pokePower.setText("");
            } else {
                // BÚSQUEDA TERMINADA
                binding.btnInitialSearch.setEnabled(true);
                binding.btnRetry.setEnabled(true);
            }
        });

        // OBSERVAR SI ES DUPLICADO
        viewModel.getIsDuplicate().observe(getViewLifecycleOwner(), isDup -> {
            // Solo actualizamos el botón si NO estamos buscando
            if (Boolean.FALSE.equals(viewModel.getIsSearching().getValue())) {
                if (Boolean.TRUE.equals(isDup)) {
                    // CASO 1: YA LO TIENES, BLOQUEAR Y PONER GRIS
                    binding.btnCatch.setEnabled(false);
                    binding.btnCatch.setText("YA LO TIENES");
                    binding.btnCatch.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY)
                    );
                } else {
                    // CASO 2: NO LO TIENES, HABILITAR Y PONER COLOR ORIGINAL
                    if (viewModel.getWildPokemon().getValue() != null) {
                        binding.btnCatch.setEnabled(true);
                        binding.btnCatch.setText("CAPTURAR");
                        binding.btnCatch.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.teal_700, null))
                        );
                    }
                }
            }
        });

        // OBSERVAR IMÁGENES RULETA
        viewModel.getRouletteImage().observe(getViewLifecycleOwner(), imageUrl -> {
            if (Boolean.TRUE.equals(viewModel.getIsSearching().getValue()) && imageUrl != null) {
                Glide.with(this).load(imageUrl).into(binding.pokeImage);
            }
        });

        // OBSERVAR POKEMON FINAL
        viewModel.getWildPokemon().observe(getViewLifecycleOwner(), pokemon -> {
            if (Boolean.FALSE.equals(viewModel.getIsSearching().getValue())) {
                if (pokemon != null) {
                    // MOSTRAR POKEMON
                    binding.btnInitialSearch.setVisibility(View.GONE);
                    binding.layoutActions.setVisibility(View.VISIBLE);

                    binding.pokeName.setText(pokemon.name);
                    binding.pokePower.setText("Poder: " + pokemon.getTotalPower());

                    Glide.with(this)
                            .load(pokemon.sprites.frontDefault)
                            .placeholder(R.drawable.pokeball_placeholder)
                            .into(binding.pokeImage);
                } else if (!Boolean.TRUE.equals(viewModel.getIsSearching().getValue())) {
                    // ESTADO INICIAL
                    binding.btnInitialSearch.setVisibility(View.VISIBLE);
                    binding.layoutActions.setVisibility(View.GONE);

                    binding.pokeName.setText("¿?");
                    binding.pokePower.setText("");
                    binding.pokeImage.setImageResource(R.drawable.pokeball_placeholder);
                }
            }
        });

        // Listeners
        binding.btnInitialSearch.setOnClickListener(v -> {
            resetCatchButtonVisuals(); // Resetear aspecto botón
            binding.pokeName.setText("Buscando...");
            viewModel.searchRandomPokemon();
        });

        binding.btnRetry.setOnClickListener(v -> {
            resetCatchButtonVisuals(); // Resetear aspecto botón
            binding.pokeName.setText("Buscando...");
            viewModel.searchRandomPokemon();
        });

        binding.btnCatch.setOnClickListener(v -> {
            viewModel.tryCatchPokemon();
        });

        binding.btnBackToList.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });

        // Mensajes
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

    // Refresca el boton al volver a buscar
    private void resetCatchButtonVisuals() {
        binding.btnCatch.setText("CAPTURAR");
        binding.btnCatch.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.teal_700, null))
        );
    }
}