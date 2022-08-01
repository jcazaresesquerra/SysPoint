package com.app.syspoint.ui.productos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.app.syspoint.db.bean.ProductoBean;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterListaProductosVentas  extends RecyclerView.Adapter<AdapterListaProductosVentas.Holder> implements Filterable {

    private List<ProductoBean>mData;
    private List<ProductoBean>mDataFilter;
    private OnItemClickListener onItemClickListener;

    public AdapterListaProductosVentas(List<ProductoBean> mData, OnItemClickListener onItemClickListener) {
        this.mData = mData;
        this.mDataFilter = mData;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_productos_ventas, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mDataFilter.get(position), onItemClickListener);
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

    public void setData(List<ProductoBean> newData) {
        this.mDataFilter = newData;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView textViewArticulo;
        TextView textViewDescripcion;
        TextView textViewPrecio;
        CircleImageView imageView;
        TextView textView_producto_disponible_lista_venta_lista;
        TextView textView_producto_disponible_view_lista_venta_lista;

        public Holder(@NonNull View itemView) {
            super(itemView);

            this.textViewArticulo = itemView.findViewById(R.id.textView_producto_codigo_lista_venta_lista);
            this.textViewDescripcion = itemView.findViewById(R.id.textView_producto_descripcion_venta_lista);
            this.textViewPrecio = itemView.findViewById(R.id.textView_producto_precio_lista_venta_lista);
            this.imageView = itemView.findViewById(R.id.img_producto_venta_lista);
            this.textView_producto_disponible_view_lista_venta_lista = itemView.findViewById(R.id.textView_producto_disponible_lista_venta_lista_view);
        }

        private void bind(ProductoBean producto, final OnItemClickListener onItemClickListener){

            this.textViewArticulo.setText(""+producto.getArticulo());
            this.textViewDescripcion.setText(""+ producto.getDescripcion());
            this.textViewPrecio.setText(""+ producto.getPrecio());

            if (producto.getPath_img() != null){
                byte[] decodedString = Base64.decode(producto.getPath_img(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });

            textView_producto_disponible_view_lista_venta_lista.setText("" + producto.getExistencia());
            if (producto.getExistencia() > 0){
                textView_producto_disponible_view_lista_venta_lista.setTextColor(Color.GREEN);
            }else {
                textView_producto_disponible_view_lista_venta_lista.setTextColor(Color.RED);
            }

        }

    }

    //Cuando el usuario da click en el item del cliente
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
