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
import com.example.proyecto_pokeapi_joseavn.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private SharedViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        binding.btnRegister.setOnClickListener(v -> {
            String user = binding.etRegUser.getText().toString();
            String pass = binding.etRegPass.getText().toString();

            if (!user.isEmpty() && !pass.isEmpty()) {
                viewModel.register(user, pass);
            } else {
                Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Observar mensajes
        viewModel.message.observe(getViewLifecycleOwner(), msg -> {
            // Verificamos que el mensaje no sea nulo ni esté vacío
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                // Solo si el mensaje contiene "exitoso" (o la palabra clave que usaste en el ViewModel), volvemos atrás
                if (msg.toLowerCase().contains("exitoso") || msg.toLowerCase().contains("exitosa")) {
                    Navigation.findNavController(view).popBackStack(); // Volver al Login
                    viewModel.message.setValue(""); // Limpiamos el mensaje para que no salte al volver
                }
                // Si el mensaje es "El usuario ya existe" o cualquier error, NO hacemos popBackStack(),
                // así el usuario se queda en el formulario para corregirlo.
            }
        });

        // Botón Volver al Login
        binding.btnBackToLogin.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });
    }
}