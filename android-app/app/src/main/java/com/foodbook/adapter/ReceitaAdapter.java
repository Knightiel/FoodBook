package com.foodbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.foodbook.R;
import com.foodbook.dto.ReceitaResumoDto;
import com.foodbook.network.ApiClient;

public class ReceitaAdapter extends ListAdapter<ReceitaResumoDto, ReceitaAdapter.ViewHolder> {

    public interface OnReceitaListener {
        void onClick(ReceitaResumoDto receita);
        void onCurtirClick(ReceitaResumoDto receita);
        void onFavoritoClick(ReceitaResumoDto receita);
    }

    private final OnReceitaListener listener;

    public ReceitaAdapter(OnReceitaListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ReceitaResumoDto> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull ReceitaResumoDto o, @NonNull ReceitaResumoDto n) {
                    return o.getId().equals(n.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull ReceitaResumoDto o, @NonNull ReceitaResumoDto n) {
                    return o.isCurtidoPorMim() == n.isCurtidoPorMim()
                            && o.isFavoritadoPorMim() == n.isFavoritadoPorMim()
                            && o.getTotalCurtidas() == n.getTotalCurtidas();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receita_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgCapa;
        private final TextView tvTitulo;
        private final TextView tvAutor;
        private final TextView tvCategoria;
        private final TextView tvTempoPreparo;
        private final TextView tvCurtidas;
        private final ImageButton btnCurtir;
        private final ImageButton btnFavorito;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCapa = itemView.findViewById(R.id.imgCapa);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvAutor = itemView.findViewById(R.id.tvAutor);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvTempoPreparo = itemView.findViewById(R.id.tvTempoPreparo);
            tvCurtidas = itemView.findViewById(R.id.tvCurtidas);
            btnCurtir = itemView.findViewById(R.id.btnCurtir);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
        }

        void bind(ReceitaResumoDto receita, OnReceitaListener listener) {
            tvTitulo.setText(receita.getTitulo());
            tvAutor.setText(receita.getAutor() != null ? receita.getAutor().getNome() : "");
            tvCategoria.setText(receita.getCategoria() != null ? receita.getCategoria().getNome() : "");
            tvTempoPreparo.setText(receita.getTempoPreparo() > 0 ? receita.getTempoPreparo() + " min" : "");
            tvCurtidas.setText(String.valueOf(receita.getTotalCurtidas()));

            btnCurtir.setImageResource(receita.isCurtidoPorMim()
                    ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            btnFavorito.setImageResource(receita.isFavoritadoPorMim()
                    ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);

            if (receita.getImagemUrl() != null && !receita.getImagemUrl().isEmpty()) {
                String url = ApiClient.getBaseUrl() + receita.getImagemUrl();
                Glide.with(imgCapa.getContext())
                        .load(url)
                        .transform(new CenterCrop(), new RoundedCorners(16))
                        .placeholder(R.drawable.bg_placeholder_receita)
                        .error(R.drawable.bg_placeholder_receita)
                        .into(imgCapa);
            } else {
                imgCapa.setImageResource(R.drawable.bg_placeholder_receita);
            }

            itemView.setOnClickListener(v -> listener.onClick(receita));
            btnCurtir.setOnClickListener(v -> listener.onCurtirClick(receita));
            btnFavorito.setOnClickListener(v -> listener.onFavoritoClick(receita));
        }
    }
}
