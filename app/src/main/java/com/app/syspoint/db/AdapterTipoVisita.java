package com.app.syspoint.db;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.TipoVisitaModel;

import java.util.List;

public class AdapterTipoVisita extends RecyclerView.Adapter<AdapterTipoVisita.Holder> {

    private List<TipoVisitaModel> mData;
    private OnItemClickListener onItemClickListener;

    public AdapterTipoVisita(List<TipoVisitaModel> mData, OnItemClickListener onItemClickListener) {
        this.mData = mData;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_tipo_visita, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mData.get(position),  onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size() > 0 ? mData.size() : 0;
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView textViewTipo;
        private CardView card_tipo_seleccion;
        public Holder(@NonNull View itemView) {
            super(itemView);
            textViewTipo = itemView.findViewById(R.id.tv_tipo);
            card_tipo_seleccion = itemView.findViewById(R.id.card_tipo_seleccion);
        }

        private void bind(TipoVisitaModel item, OnItemClickListener onItemClickListener){

            textViewTipo.setText(""+item.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });

            if (item.isSelected()){
                card_tipo_seleccion.setBackgroundColor(Color.CYAN);
            }else {
                card_tipo_seleccion.setBackgroundColor(Color.WHITE);
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
