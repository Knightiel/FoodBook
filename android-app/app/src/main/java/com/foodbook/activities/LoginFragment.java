package com.foodbook.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.foodbook.databinding.FragmentLoginBinding;
import com.foodbook.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        observarResultado();
        configurarCliques();
    }

    private void configurarCliques() {
        binding.btnLogin.setOnClickListener(v -> realizarLogin());

        binding.tvRegisterLink.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(com.foodbook.R.id.auth_container, new RegisterFragment())
                        .addToBackStack(null)
                        .commit());
    }

    private void realizarLogin() {
        String email = obterTexto(binding.etEmail);
        String senha = obterTexto(binding.etSenha);
        binding.tilEmail.setError(null);
        binding.tilSenha.setError(null);
        viewModel.login(email, senha);
    }

    private void observarResultado() {
        viewModel.getAuthResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            binding.progressBar.setVisibility(result.isLoading() ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!result.isLoading());
            if (result.isSuccess()) {
                ((AuthActivity) requireActivity()).navigateToMain();
            } else if (result.isError()) {
                Snackbar.make(binding.getRoot(), result.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private String obterTexto(com.google.android.material.textfield.TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
