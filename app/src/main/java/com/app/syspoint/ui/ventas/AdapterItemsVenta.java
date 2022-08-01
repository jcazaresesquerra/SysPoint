package com.app.syspoint.ui.ventas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.db.VentasModelBean;
import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.dao.ProductoDao;
import com.app.syspoint.utils.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterItemsVenta extends RecyclerView.Adapter<AdapterItemsVenta.Holder> {

    private List<VentasModelBean> mData;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemClickListener onItemClickListener;

    public AdapterItemsVenta(List<VentasModelBean> mData, OnItemLongClickListener onItemLongClickListener, OnItemClickListener onItemClickListener) {
        this.mData = mData;
        this.onItemLongClickListener = onItemLongClickListener;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ventas_cardview, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mData.get(position), onItemLongClickListener, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size() > 0 ? mData.size() : 0;
    }

    public void setItems(List<VentasModelBean> item){
        this.mData = item;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView textViewArticulo;
        TextView textViewDescripcion;
        TextView textViewCantidad;
        TextView textViewPrecio;
        TextView textViewTotal;
        CircleImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            this.textViewArticulo = itemView.findViewById(R.id.textView_producto_codigo_venta);
            this.textViewDescripcion = itemView.findViewById(R.id.textView_producto_descripcion_venta);
            this.textViewCantidad = itemView.findViewById(R.id.textView_producto_cantidad_venta);
            this.textViewPrecio = itemView.findViewById(R.id.textView_producto_precio_venta);
            this.textViewTotal = itemView.findViewById(R.id.textView_producto_total_venta);
            this.imageView = itemView.findViewById(R.id.img_producto_venta);
        }

        private void bind(final VentasModelBean item, final OnItemLongClickListener listener, final OnItemClickListener onItemClickListener) {

            this.textViewArticulo.setText("" + item.getArticulo());
            this.textViewDescripcion.setText(""+ item.getDescripcion());
            this.textViewCantidad.setText("Cant. " + item.getCantidad());
            this.textViewPrecio.setText("" + Utils.FDinero(item.getPrecio()));
            double total = (item.getPrecio() * item.getCantidad());
            this.textViewTotal.setText("" + Utils.FDinero(total));

            final ProductoDao productoDao = new ProductoDao();
            final ProductoBean productoBean = productoDao.getProductoByArticulo(item.getArticulo());

            //Si el producto es != null entonces muestra la imagen
            if (productoBean != null) {
                if (productoBean.getPath_img() != null){
                    byte[] decodedString = Base64.decode(productoBean.getPath_img(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClicked(getAdapterPosition());
                    return false;
                }
            });
        }
    }

    /**
     *Evento click corto
     ***/
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    /**
     * Evento Click Largo
     * **/
    public interface OnItemLongClickListener {
        boolean onItemLongClicked(int position);
    }
}
