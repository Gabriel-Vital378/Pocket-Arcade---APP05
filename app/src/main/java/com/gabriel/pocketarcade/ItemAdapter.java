package com.gabriel.pocketarcade;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gabriel.pocketarcade.databinding.ItemPalavraBinding;

import java.util.List;

/**
 * O Adapter fornece as views dos itens; o ViewHolder as guarda para reutilizacao.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    public interface OnItemLongClickListener {
        void onItemLongClick(ItemSorteio item, int posicao);
    }

    private final List<ItemSorteio> itens;
    private final OnItemLongClickListener listener;
    private ItemSorteio destacado;

    public ItemAdapter(List<ItemSorteio> itens, OnItemLongClickListener listener) {
        this.itens = itens;
        this.listener = listener;
    }

    public ItemSorteio getDestacado() {
        return destacado;
    }

    /** Destaca o item sorteado (e remove o destaque do anterior). */
    public void setDestacado(ItemSorteio novo) {
        ItemSorteio antigo = destacado;
        destacado = novo;
        int posAntigo = itens.indexOf(antigo);
        if (antigo != null && posAntigo >= 0) {
            notifyItemChanged(posAntigo);
        }
        int posNovo = itens.indexOf(novo);
        if (novo != null && posNovo >= 0) {
            notifyItemChanged(posNovo);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPalavraBinding binding = ItemPalavraBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemSorteio item = itens.get(position);
        holder.binding.txtPalavra.setText(item.getPalavra());
        holder.binding.txtCategoria.setText(item.getCategoria());
        holder.binding.itemRaiz.setBackgroundResource(
                item == destacado ? R.drawable.bg_item_destaque : R.drawable.bg_item);
        holder.binding.getRoot().setOnLongClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(itens.get(pos), pos);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final ItemPalavraBinding binding;

        ItemViewHolder(ItemPalavraBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
