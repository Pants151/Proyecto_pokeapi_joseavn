package com.example.proyecto_pokeapi_joseavn.ui;

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

        // Configurar RecyclerView
        setupRecyclerView();

        // Observar los pokemons del usuario (Base de Datos Local)
        viewModel.getMyPokemons().observe(getViewLifecycleOwner(), pokemons -> {
            adapter.setList(pokemons);
        });

        // Botón BUSCAR POKEMON -> Navegar a SearchFragment
        binding.btnSearch.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_list_to_search)
        );
    }

    private void setupRecyclerView() {
        adapter = new PokemonAdapter(pokemon -> {
            // Al hacer click en un item, vamos al detalle pasando los datos
            Bundle args = new Bundle();
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