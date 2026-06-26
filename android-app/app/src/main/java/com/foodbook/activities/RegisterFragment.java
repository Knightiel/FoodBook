package com.foodbook.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.foodbook.databinding.FragmentRegisterBinding;
import com.foodbook.utils.Resource;
import com.foodbook.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
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
        binding.btnRegister.setOnClickListener(v -> realizarCadastro());

        binding.tvLoginLink.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void realizarCadastro() {
        String nome = obterTexto(binding.etNome);
        String email = obterTexto(binding.etEmail);
        String senha = obterTexto(binding.etSenha);
        String confirmar = obterTexto(binding.etConfirmarSenha);

        limparErros();
        viewModel.registrar(nome, email, senha, confirmar);
    }

    private void observarResultado() {
        viewModel.getAuthResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            binding.progressBar.setVisibility(result.isLoading() ? View.VISIBLE : View.GONE);
            binding.btnRegister.setEnabled(!result.isLoading());

            if (result.isSuccess()) {
                ((AuthActivity) requireActivity()).navigateToMain();
            } else if (result.isError()) {
                Snackbar.make(binding.getRoot(), result.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void limparErros() {
        binding.tilNome.setError(null);
        binding.tilEmail.setError(null);
        binding.tilSenha.setError(null);
        binding.tilConfirmarSenha.setError(null);
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
