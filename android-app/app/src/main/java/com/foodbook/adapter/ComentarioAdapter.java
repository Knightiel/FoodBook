package com.foodbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.foodbook.R;
import com.foodbook.dto.ComentarioDto;
import com.foodbook.utils.SessionManager;

public class ComentarioAdapter extends ListAdapter<ComentarioDto, ComentarioAdapter.ViewHolder> {

    public interface OnComentarioListener {
        void onExcluir(ComentarioDto comentario);
    }

    private final OnComentarioListener listener;
    private final long meuId;
    private final long donoReceitaId;

    public ComentarioAdapter(long meuId, long donoReceitaId, OnComentarioListener listener) {
        super(DIFF_CALLBACK);
        this.meuId = meuId;
        this.donoReceitaId = donoReceitaId;
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ComentarioDto> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull ComentarioDto o, @NonNull ComentarioDto n) {
                    return o.getId().equals(n.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull ComentarioDto o, @NonNull ComentarioDto n) {
                    return o.getTexto().equals(n.getTexto());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComentarioDto comentario = getItem(position);
        boolean podeExcluir = (comentario.getAutor() != null && comentario.getAutor().getId().equals(meuId))
                || meuId == donoReceitaId;
        holder.bind(comentario, podeExcluir, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNome;
        private final TextView tvTexto;
        private final TextView tvData;
        private final ImageButton btnExcluir;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvTexto = itemView.findViewById(R.id.tvTexto);
            tvData = itemView.findViewById(R.id.tvData);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }

        void bind(ComentarioDto comentario, boolean podeExcluir, OnComentarioListener listener) {
            tvNome.setText(comentario.getAutor() != null ? comentario.getAutor().getNome() : "");
            tvTexto.setText(comentario.getTexto());
            tvData.setText(comentario.getCriadoEm() != null
                    ? comentario.getCriadoEm().substring(0, 10) : "");

            btnExcluir.setVisibility(podeExcluir ? View.VISIBLE : View.GONE);
            btnExcluir.setOnClickListener(v -> listener.onExcluir(comentario));
        }
    }
}
