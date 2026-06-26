package com.foodbook.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.foodbook.R;
import com.foodbook.adapter.ReceitaAdapter;
import com.foodbook.databinding.FragmentProfileBinding;
import com.foodbook.dto.ReceitaResumoDto;
import com.foodbook.dto.UsuarioPerfilDto;
import com.foodbook.utils.Resource;
import com.foodbook.viewmodel.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private ReceitaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        configurarRecyclerView();
        observarViewModel();

        binding.btnEditarPerfil.setOnClickListener(v -> mostrarDialogEditar());

        viewModel.carregarMeuPerfil();
    }

    private void configurarRecyclerView() {
        adapter = new ReceitaAdapter(new ReceitaAdapter.OnReceitaListener() {
            @Override
            public void onClick(ReceitaResumoDto receita) {
                Bundle args = new Bundle();
                args.putLong("recipeId", receita.getId());
                Navigation.findNavController(requireView())
                        .navigate(R.id.recipeDetailFragment, args);
            }

            @Override
            public void onCurtirClick(ReceitaResumoDto receita) {}

            @Override
            public void onFavoritoClick(ReceitaResumoDto receita) {}
        });

        binding.rvReceitas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReceitas.setAdapter(adapter);
    }

    private void observarViewModel() {
        viewModel.getPerfil().observe(getViewLifecycleOwner(), result -> {
            if (result.getStatus() == Resource.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                return;
            }
            binding.progressBar.setVisibility(View.GONE);

            if (result.isSuccess() && result.getData() != null) {
                exibirPerfil(result.getData());
            }
        });

        viewModel.getReceitas().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                adapter.submitList(result.getData());
            }
        });

        viewModel.getAtualizacaoResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                Snackbar.make(requireView(), R.string.msg_perfil_atualizado, Snackbar.LENGTH_SHORT).show();
                viewModel.carregarMeuPerfil();
            } else if (!result.isSuccess()) {
                Snackbar.make(requireView(), result.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void exibirPerfil(UsuarioPerfilDto perfil) {
        binding.tvNome.setText(perfil.getNome());
        binding.tvTotalReceitas.setText(String.valueOf(perfil.getTotalReceitas()));
        binding.tvTotalCurtidas.setText(String.valueOf(perfil.getTotalCurtidasRecebidas()));
        binding.tvTotalFavoritos.setText(String.valueOf(perfil.getTotalFavoritos()));
        binding.btnEditarPerfil.setVisibility(View.VISIBLE);
    }

    private void mostrarDialogEditar() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_editar_nome, null);
        TextInputEditText etNome = dialogView.findViewById(R.id.etNome);

        UsuarioPerfilDto perfil = viewModel.getPerfil().getValue() != null
                ? viewModel.getPerfil().getValue().getData() : null;
        if (perfil != null) etNome.setText(perfil.getNome());

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.titulo_editar_perfil)
                .setView(dialogView)
                .setPositiveButton(R.string.btn_salvar, (d, w) -> {
                    String nome = etNome.getText() != null ? etNome.getText().toString().trim() : "";
                    if (!nome.isEmpty()) viewModel.atualizarNome(nome);
                })
                .setNegativeButton(R.string.btn_cancelar, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
