package com.app.syspoint.ui.clientes.PreciosEspeciales;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.PreciosEspecialesBean;
import com.app.syspoint.repository.database.bean.ProductoBean;
import com.app.syspoint.repository.database.dao.ProductoDao;
import com.app.syspoint.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AdapterListaPreciosEspeciales extends RecyclerView.Adapter<AdapterListaPreciosEspeciales.Holder> implements Filterable {


    private List<PreciosEspecialesBean> mData;
    private List<PreciosEspecialesBean> mDataFilter;
    private OnItemClickListener onItemClickListener;


    public AdapterListaPreciosEspeciales(List<PreciosEspecialesBean> mData, OnItemClickListener onItemClickListener) {
        this.mData = mData;
        this.mDataFilter = mData;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_productos_clientes, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mDataFilter.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        if (mDataFilter.size() == 0) {
            return 0;
        }else {
            return mDataFilter.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filtro = charSequence.toString();


                if (filtro.isEmpty()){
                    mDataFilter = mData;
                }else {
                    List<PreciosEspecialesBean> filtroProductos = new ArrayList<>();


                    for (PreciosEspecialesBean row : mDataFilter){


                        if (row.getCliente().toLowerCase().contains(filtro) || row.getArticulo().toLowerCase().contains(filtro) ){
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
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataFilter = (ArrayList<PreciosEspecialesBean>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView img_producto_cliente_lista;
        private TextView textView_producto_descripcion_cliente_lista;
        private TextView  textView_producto_codigo_lista_cliente_lista;
        private TextView textView_producto_precio_lista_cliente_lista;
        private TextView textView_producto_precio_normal_lista_cliente_lista;

        public Holder(@NonNull View itemView) {
            super(itemView);
            img_producto_cliente_lista = itemView.findViewById(R.id.img_producto_cliente_lista);
            textView_producto_descripcion_cliente_lista = itemView.findViewById(R.id.textView_producto_descripcion_cliente_lista);
            textView_producto_codigo_lista_cliente_lista = itemView.findViewById(R.id.textView_producto_codigo_lista_cliente_lista);
            textView_producto_precio_lista_cliente_lista = itemView.findViewById(R.id.textView_producto_precio_lista_cliente_lista);
            textView_producto_precio_normal_lista_cliente_lista  = itemView.findViewById(R.id.textView_producto_precio_normal_lista_cliente_lista);
        }

        private void bind(final PreciosEspecialesBean item, OnItemClickListener onItemClickListener){


            final ProductoDao  productoDao = new ProductoDao();
            final ProductoBean productoBean = productoDao.getProductoByArticulo(item.getArticulo());

            if (productoBean != null){
                if (productoBean.getPath_img() != null){
                    byte[] decodedString = Base64.decode(productoBean.getPath_img(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    img_producto_cliente_lista.setImageBitmap(decodedByte);
                }
                textView_producto_descripcion_cliente_lista.setText("" + productoBean.getDescripcion());
                textView_producto_codigo_lista_cliente_lista.setText(""+ productoBean.getArticulo());
                textView_producto_precio_lista_cliente_lista.setText(""+ Utils.formatMoneyMX(productoBean.getPrecio()));
            }

            textView_producto_precio_normal_lista_cliente_lista.setText("" + Utils.formatMoneyMX(item.getPrecio()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }


    //Para saber si cambio algo
    public void setPrecios(List<PreciosEspecialesBean> data){
        this.mDataFilter = data;
        notifyDataSetChanged();
    }


    /**
     *Evento click corto
     ***/
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
