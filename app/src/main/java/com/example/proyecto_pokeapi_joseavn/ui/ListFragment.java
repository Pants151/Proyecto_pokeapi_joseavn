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
import android.widget.EditText;
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

        binding.btnLogout.setOnClickListener(v -> showProfileOptions());

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

    private void showProfileOptions() {
        String[] options = {"Cerrar Sesión", "Cambiar Contraseña", "Borrar Cuenta (Peligro)"};

        new AlertDialog.Builder(getContext())
                .setTitle("Gestión de Usuario")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Cerrar Sesión
                            viewModel.logout();
                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_list_to_login_logout);
                            break;
                        case 1: // Cambiar Contraseña
                            showChangePasswordDialog();
                            break;
                        case 2: // Borrar Cuenta
                            confirmDeleteAccount();
                            break;
                    }
                })
                .show();
    }

    private void showChangePasswordDialog() {
        final EditText input = new EditText(getContext());
        input.setHint("Nueva contraseña");

        new AlertDialog.Builder(getContext())
                .setTitle("Cambiar Contraseña")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String newPass = input.getText().toString();
                    if (!newPass.isEmpty()) {
                        viewModel.changePassword(newPass);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(getContext())
                .setTitle("ELIMINAR CUENTA")
                .setMessage("¿Estás seguro? Se borrarán todos tus pokemons y no podrás recuperar la cuenta.")
                .setPositiveButton("SÍ, BORRAR", (dialog, which) -> {
                    viewModel.deleteAccount();
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_list_to_login_logout);
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