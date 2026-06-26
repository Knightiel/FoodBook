package com.foodbook.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.foodbook.R;
import com.foodbook.adapter.ComentarioAdapter;
import com.foodbook.databinding.FragmentRecipeDetailBinding;
import com.foodbook.dto.ReceitaDetalheDto;
import com.foodbook.dto.ReceitaIngredienteDto;
import com.foodbook.network.ApiClient;
import com.foodbook.utils.Resource;
import com.foodbook.utils.SessionManager;
import com.foodbook.viewmodel.RecipeDetailViewModel;
import com.google.android.material.snackbar.Snackbar;

public class RecipeDetailFragment extends Fragment {

    private FragmentRecipeDetailBinding binding;
    private RecipeDetailViewModel viewModel;
    private ComentarioAdapter comentarioAdapter;
    private long receitaId;
    private ReceitaDetalheDto receitaAtual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RecipeDetailViewModel.class);

        receitaId = getArguments() != null ? getArguments().getLong("recipeId", -1) : -1;

        configurarComentarios();
        observarViewModel();
        configurarAcoes();

        if (receitaId > 0) {
            viewModel.carregarReceita(receitaId);
            viewModel.carregarComentarios(receitaId);
        }
    }

    private void configurarComentarios() {
        long meuId = SessionManager.getInstance(requireContext()).getUserId();
        comentarioAdapter = new ComentarioAdapter(meuId, -1, comentario ->
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.titulo_excluir_comentario)
                        .setMessage(R.string.msg_confirmar_exclusao_comentario)
                        .setPositiveButton(R.string.btn_excluir, (d, w) ->
                                viewModel.excluirComentario(receitaId, comentario.getId()))
                        .setNegativeButton(R.string.btn_cancelar, null)
                        .show());
        binding.rvComentarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvComentarios.setAdapter(comentarioAdapter);
    }

    private void configurarAcoes() {
        binding.btnEnviarComentario.setOnClickListener(v -> {
            String texto = binding.etComentario.getText() != null
                    ? binding.etComentario.getText().toString() : "";
            viewModel.enviarComentario(receitaId, texto);
            binding.etComentario.setText("");
        });
    }

    private void observarViewModel() {
        viewModel.getReceita().observe(getViewLifecycleOwner(), result -> {
            if (result.getStatus() == Resource.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.scrollView.setVisibility(View.GONE);
                return;
            }
            binding.progressBar.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);

            if (result.isSuccess() && result.getData() != null) {
                receitaAtual = result.getData();
                exibirReceita(receitaAtual);
                requireActivity().invalidateOptionsMenu();
            }
        });

        viewModel.getComentarios().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess() && result.getData() != null) {
                comentarioAdapter.submitList(result.getData());
            }
        });

        viewModel.getCurtidaResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess() && result.getData() != null && receitaAtual != null) {
                receitaAtual.setCurtidoPorMim(result.getData().isCurtidoPorMim());
                receitaAtual.setTotalCurtidas(result.getData().getTotalCurtidas());
                binding.tvCurtidas.setText(String.valueOf(receitaAtual.getTotalCurtidas()));
                requireActivity().invalidateOptionsMenu();
            }
        });

        viewModel.getFavoritoResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess() && result.getData() != null && receitaAtual != null) {
                receitaAtual.setFavoritadoPorMim(result.getData().isFavoritadoPorMim());
                requireActivity().invalidateOptionsMenu();
            }
        });

        viewModel.getExcluido().observe(getViewLifecycleOwner(), excluido -> {
            if (Boolean.TRUE.equals(excluido)) {
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }

    private void exibirReceita(ReceitaDetalheDto receita) {
        binding.tvTitulo.setText(receita.getTitulo());
        binding.tvAutor.setText(receita.getAutor() != null ? receita.getAutor().getNome() : "");
        binding.tvCategoria.setText(receita.getCategoria() != null ? receita.getCategoria().getNome() : "");
        binding.tvDescricao.setText(receita.getDescricao());
        binding.tvModoPreparo.setText(receita.getModoPreparo());
        binding.tvTempoPreparo.setText(receita.getTempoPreparo() > 0 ? receita.getTempoPreparo() + " min" : "-");
        binding.tvPorcoes.setText(receita.getPorcoes() > 0 ? String.valueOf(receita.getPorcoes()) : "-");
        binding.tvCurtidas.setText(String.valueOf(receita.getTotalCurtidas()));

        if (receita.getImagemUrl() != null && !receita.getImagemUrl().isEmpty()) {
            Glide.with(this)
                    .load(ApiClient.getBaseUrl() + receita.getImagemUrl())
                    .placeholder(R.drawable.bg_placeholder_receita)
                    .into(binding.imgCapa);
        }

        binding.layoutIngredientes.removeAllViews();
        if (receita.getIngredientes() != null) {
            for (ReceitaIngredienteDto ing : receita.getIngredientes()) {
                View item = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_ingrediente, binding.layoutIngredientes, false);
                android.widget.TextView tvIng = item.findViewById(R.id.tvIngrediente);
                tvIng.setText(ing.getQuantidade() + " " + ing.getUnidade() + " de " + ing.getNomeIngrediente());
                binding.layoutIngredientes.addView(item);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipe_detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (receitaAtual == null) return;
        long meuId = SessionManager.getInstance(requireContext()).getUserId();

        MenuItem itemCurtir = menu.findItem(R.id.action_curtir);
        MenuItem itemFavoritar = menu.findItem(R.id.action_favoritar);
        MenuItem itemEditar = menu.findItem(R.id.action_editar);
        MenuItem itemExcluir = menu.findItem(R.id.action_excluir);

        if (itemCurtir != null) {
            itemCurtir.setIcon(receitaAtual.isCurtidoPorMim()
                    ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        }
        if (itemFavoritar != null) {
            itemFavoritar.setIcon(receitaAtual.isFavoritadoPorMim()
                    ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        }

        boolean ehDono = receitaAtual.getAutor() != null
                && receitaAtual.getAutor().getId().equals(meuId);
        if (itemEditar != null) itemEditar.setVisible(ehDono);
        if (itemExcluir != null) itemExcluir.setVisible(ehDono);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (receitaAtual == null) return super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.action_curtir) {
            viewModel.toggleCurtida(receitaAtual);
            return true;
        } else if (id == R.id.action_favoritar) {
            viewModel.toggleFavorito(receitaAtual);
            return true;
        } else if (id == R.id.action_editar) {
            Bundle args = new Bundle();
            args.putLong("recipeId", receitaAtual.getId());
            Navigation.findNavController(requireView()).navigate(R.id.recipeFormFragment, args);
            return true;
        } else if (id == R.id.action_excluir) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.titulo_excluir_receita)
                    .setMessage(R.string.msg_confirmar_exclusao_receita)
                    .setPositiveButton(R.string.btn_excluir,
                            (d, w) -> viewModel.excluirReceita(receitaAtual.getId()))
                    .setNegativeButton(R.string.btn_cancelar, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
