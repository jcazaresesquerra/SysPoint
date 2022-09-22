package com.app.syspoint.ui.products.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.ProductoBean;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterListaProductos extends RecyclerView.Adapter<AdapterListaProductos.Holder> implements Filterable {

    private List<ProductoBean> mData;
    private List<ProductoBean> mDataFilter;
    private OnItemClickListener onItemClickListener;

    public AdapterListaProductos(List<ProductoBean> mData, OnItemClickListener onItemClickListener) {
        this.mData = mData;
        this.mDataFilter = mData;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_productod, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mDataFilter.get(position), onItemClickListener);
    }

    public void setProducts(List<ProductoBean> data){
        this.mDataFilter = data;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
      return  mDataFilter.size()>0 ? mDataFilter.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filtro = constraint.toString();

                if (filtro.isEmpty()){
                    mDataFilter = mData;
                }else {

                    //TODO filtro productos
                    List<ProductoBean> filtroProductos = new ArrayList<>();

                    for (ProductoBean row : mDataFilter){
                        if (row.getArticulo().toLowerCase().contains(filtro) || row.getDescripcion().toLowerCase().contains(filtro) ){
                            filtroProductos.add(row);
                        }
                    }
                    mDataFilter = filtroProductos;
                }

                FilterResults results = new FilterResults();
                results.values = mDataFilter;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataFilter = (ArrayList<ProductoBean>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView textViewArticulo;
        TextView textViewDescripcion;
        TextView textViewPrecio;
        TextView textViewUnidadMedida;
        TextView textViewIVA;
        TextView textViewIEPS;
        TextView textViewCategoria;
        TextView textViewPrioridad;
        TextView textViewStatus;
        TextView textViewCodAlfa;
        TextView textViewCodBarras;
        TextView textViewRegion;
        CircleImageView imageView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            this.textViewArticulo = itemView.findViewById(R.id.textViewArticuloList);
            this.textViewDescripcion= itemView.findViewById(R.id.textViewDescripcionProductoList);
            this.textViewPrecio= itemView.findViewById(R.id.textViewPreciosArticuloList);
            this.textViewUnidadMedida= itemView.findViewById(R.id.textViewArticuloUnidadMedidaList);
            this.textViewIVA = itemView.findViewById(R.id.textViewArticuloIVAList);
            this.textViewIEPS= itemView.findViewById(R.id.textViewArticuloIESPList);
            this.textViewCategoria= itemView.findViewById(R.id.textViewArticuloCategoriaList);
            this.textViewPrioridad= itemView.findViewById(R.id.textViewArticuloPrioridadList);
            this.textViewStatus= itemView.findViewById(R.id.textViewArticuloStatusList);
            this.textViewCodAlfa= itemView.findViewById(R.id.textViewArticuloCodAlfaList);
            this.textViewCodBarras= itemView.findViewById(R.id.textViewArticuloCodBarrasList);
            this.textViewRegion= itemView.findViewById(R.id.textViewArticuloRegionList);
            this.imageView = itemView.findViewById(R.id.imageView2);
        }

        private void bind(ProductoBean producto, final OnItemClickListener onItemClickListener){
            this.textViewArticulo.setText(producto.getArticulo());
            this.textViewDescripcion.setText(producto.getDescripcion());
            this.textViewPrecio.setText("$"+ producto.getPrecio());
            this.textViewUnidadMedida.setText(producto.getUnidad_medida());
            this.textViewIVA.setText(""+producto.getIva() + "%");
            this.textViewIEPS.setText(""+producto.getIeps() + "%");
            this.textViewCategoria.setText("SYS");
            this.textViewPrioridad.setText(""+ producto.getPrioridad());
            this.textViewStatus.setText(""+producto.getStatus());
            this.textViewCodAlfa.setText(""+ producto.getCodigo_alfa());
            this.textViewCodBarras.setText(""+ producto.getCodigo_barras());
            this.textViewRegion.setText(""+ producto.getRegion());

            if (producto.getPath_img() != null){
                byte[] decodedString = Base64.decode(producto.getPath_img(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(mData.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(ProductoBean productoBean);
    }
}
