package com.app.syspoint.ui.stock.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.InventarioBean;
import com.app.syspoint.repository.database.bean.InventarioHistorialBean;
import com.app.syspoint.repository.database.dao.StockHistoryDao;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class AdapterInventario extends RecyclerView.Adapter<AdapterInventario.Holder> {


    private List<InventarioBean> mData;
    private OnItemLongClickListener onItemLongClickListener;
    int inicial = 0;
    int vendido = 0;
    int total = 0;
    public AdapterInventario(List<InventarioBean> mData, OnItemLongClickListener onItemLongClickListener) {
        this.mData = mData;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public AdapterInventario(List<InventarioBean> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_inventario, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
      holder.bind(mData.get(position), onItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size()>0 ? mData.size() : 0;
    }

    public void setInventario(List<InventarioBean> mDataRefresh) {
        this.mData = mDataRefresh;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView textViewTotal;
        TextView textViewCantidad;
        TextView textViewDescripcion;
        TextView textViewArticulo;
        ImageView imageView4_inv;
        TextView tv_inventario_vendido, tv_inventario_total_inventario;


        public Holder(@NonNull View itemView) {
            super(itemView);
            textViewTotal = itemView.findViewById(R.id.tv_inventario_total);
            textViewCantidad = itemView.findViewById(R.id.tv_inventario_cantidad);
            textViewDescripcion = itemView.findViewById(R.id.tv_inventario_descripcion);
            textViewArticulo= itemView.findViewById(R.id.tv_inventario_articulo);
            imageView4_inv = itemView.findViewById(R.id.imageView4_inv);
            tv_inventario_total_inventario = itemView.findViewById(R.id.tv_inventario_total_inventario);
            tv_inventario_vendido = itemView.findViewById(R.id.tv_inventario_vendido);
        }

        private void bind(InventarioBean item, OnItemLongClickListener onItemLongClickListener){

            final StockHistoryDao stockHistoryDao = new StockHistoryDao();
            final InventarioHistorialBean inventarioHistorialBean = stockHistoryDao.getInvatarioPorArticulo(item.getArticulo().getArticulo());
            if (inventarioHistorialBean != null){
                vendido = inventarioHistorialBean.getCantidad();
            }
            inicial = item.getCantidad();
            textViewTotal.setText("" + Utils.FDinero(item.getCantidad() * item.getPrecio()));
            textViewCantidad.setText("" + inicial);
            textViewDescripcion.setText("" + item.getArticulo().getDescripcion());
            textViewArticulo.setText("" + item.getArticulo().getArticulo());
            tv_inventario_vendido.setText(""+vendido);
            total = inicial - vendido;

            tv_inventario_total_inventario.setText("" + total);
            if (item.getArticulo().getPath_img() != null){
                byte[] decodedString = Base64.decode(item.getArticulo().getPath_img(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView4_inv.setImageBitmap(decodedByte);
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClicked(getAdapterPosition());
                    return false;
                }
            });

             inicial = 0;
             vendido = 0;
             total = 0;

        }
    }

    /**
     * Evento Click Largo
     * **/
    public interface OnItemLongClickListener {
        boolean onItemLongClicked(int position);
    }
}
