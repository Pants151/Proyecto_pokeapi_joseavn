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
import com.example.proyecto_pokeapi_joseavn.R;
import com.example.proyecto_pokeapi_joseavn.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private SharedViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Botón Login
        binding.btnLogin.setOnClickListener(v -> {
            String user = binding.etUsername.getText().toString();
            String pass = binding.etPassword.getText().toString();
            if (!user.isEmpty() && !pass.isEmpty()) {
                viewModel.login(user, pass);
            } else {
                Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Ir a Registro
        binding.btnGoToRegister.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_login_to_register));

        // Observar si el usuario se ha logueado correctamente
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Navegar a la lista y borrar historial para no volver al login
                Navigation.findNavController(view).navigate(R.id.action_login_to_list);
            }
        });

        // Observar mensajes de error (credenciales incorrectas)
        viewModel.message.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                viewModel.message.setValue(""); // Limpiar mensaje
            }
        });
    }
}