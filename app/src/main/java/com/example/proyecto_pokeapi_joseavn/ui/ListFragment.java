package com.example.proyecto_pokeapi_joseavn.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.proyecto_pokeapi_joseavn.R;
import com.example.proyecto_pokeapi_joseavn.databinding.FragmentListBinding;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private SharedViewModel viewModel;
    private PokemonAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        setupRecyclerView();

        // Verificar que hay usuario (por seguridad, si es null no cargamos nada)
        if (viewModel.getCurrentUser().getValue() != null) {
            viewModel.getMyPokemons().observe(getViewLifecycleOwner(), pokemons -> {
                adapter.setList(pokemons);
            });
        }

        binding.btnSearch.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_list_to_search)
        );

        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());

        // Observar los pokemons del usuario
        if (viewModel.getCurrentUser().getValue() != null) {
            viewModel.getMyPokemons().observe(getViewLifecycleOwner(), pokemons -> {
                adapter.setList(pokemons);

                // LÓGICA DE VISIBILIDAD
                if (pokemons == null || pokemons.isEmpty()) {
                    // Lista vacía
                    binding.tvEmptyList.setVisibility(View.VISIBLE);
                    binding.tvTeamTitle.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    // Tiene equipo
                    binding.tvEmptyList.setVisibility(View.GONE);
                    binding.tvTeamTitle.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres salir?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // 1. Borramos la sesión del ViewModel
                    viewModel.logout();

                    // 2. Navegamos al login (Esto ya no provocará el bucle)
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_list_to_login_logout);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void setupRecyclerView() {
        adapter = new PokemonAdapter(pokemon -> {
            Bundle args = new Bundle();
            args.putInt("id", pokemon.id);
            args.putString("name", pokemon.name);
            args.putInt("power", pokemon.power);
            args.putString("type", pokemon.type);
            args.putString("image", pokemon.imageUrl);

            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_list_to_detail, args);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }
}