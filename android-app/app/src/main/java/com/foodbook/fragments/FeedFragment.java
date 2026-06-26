package com.foodbook.fragments;

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
import androidx.recyclerview.widget.RecyclerView;

import com.foodbook.R;
import com.foodbook.adapter.ReceitaAdapter;
import com.foodbook.databinding.FragmentFeedBinding;
import com.foodbook.dto.ReceitaResumoDto;
import com.foodbook.utils.Resource;
import com.foodbook.viewmodel.FeedViewModel;
import com.google.android.material.snackbar.Snackbar;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private FeedViewModel viewModel;
    private ReceitaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        configurarRecyclerView();
        configurarSwipeRefresh();
        observarViewModel();

        viewModel.carregarFeed();
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
            public void onCurtirClick(ReceitaResumoDto receita) {
                viewModel.toggleCurtida(receita);
            }

            @Override
            public void onFavoritoClick(ReceitaResumoDto receita) {
                viewModel.toggleFavorito(receita);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.rvReceitas.setLayoutManager(layoutManager);
        binding.rvReceitas.setAdapter(adapter);

        binding.rvReceitas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!viewModel.isUltimaPagina() && !viewModel.isCarregando()) {
                    if (!rv.canScrollVertically(1)) {
                        viewModel.carregarMais();
                    }
                }
            }
        });
    }

    private void configurarSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.carregarFeed());
    }

    private void observarViewModel() {
        viewModel.getReceitas().observe(getViewLifecycleOwner(), result -> {
            binding.swipeRefresh.setRefreshing(false);

            if (result.getStatus() == Resource.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.layoutVazio.setVisibility(View.GONE);
                return;
            }

            binding.progressBar.setVisibility(View.GONE);

            if (result.isSuccess()) {
                adapter.submitList(result.getData());
                binding.layoutVazio.setVisibility(
                        result.getData() == null || result.getData().isEmpty()
                                ? View.VISIBLE : View.GONE);
            } else {
                Snackbar.make(requireView(), result.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getCurtidaResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess() && result.getData() != null) {
                viewModel.atualizarCurtidaNaLista(result.getData().getReceitaId(), result.getData());
            } else if (!result.isSuccess()) {
                Snackbar.make(requireView(), result.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getFavoritoResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess() && result.getData() != null) {
                viewModel.atualizarFavoritoNaLista(result.getData().getReceitaId(), result.getData());
            } else if (!result.isSuccess()) {
                Snackbar.make(requireView(), result.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
