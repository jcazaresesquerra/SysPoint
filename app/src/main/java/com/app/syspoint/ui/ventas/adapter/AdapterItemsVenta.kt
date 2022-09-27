package com.app.syspoint.ui.ventas.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemVentasCardviewBinding
import com.app.syspoint.repository.database.bean.VentasModelBean
import com.app.syspoint.repository.database.dao.ProductDao
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.click
import com.app.syspoint.utils.longClick

class AdapterItemsVenta(
    data: List<VentasModelBean?>,
    val onItemLongClickListener: OnItemLongClickListener,
    val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<AdapterItemsVenta.Holder>() {

    private var mData = data

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(position: Int): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemVentasCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position], onItemLongClickListener, onItemClickListener)
    }

    override fun getItemCount(): Int = if (mData.isEmpty()) 0 else mData.size

    fun setData(data: List<VentasModelBean?>) {
        mData = data
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemVentasCardviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(ventasModelBean: VentasModelBean?, onItemLongClickListener: OnItemLongClickListener, onItemClickListener: OnItemClickListener) {
            ventasModelBean?.let { item ->
                binding.textViewProductoCodigoVenta.setText(item.articulo)
                binding.textViewProductoDescripcionVenta.setText(item.descripcion)
                binding.textViewProductoCantidadVenta.setText("Cant. " + item.cantidad)
                binding.textViewProductoPrecioVenta.setText(Utils.FDinero(item.precio))
                val total: Double = item.precio * item.cantidad
                binding.textViewProductoTotalVenta.setText(Utils.FDinero(total))

                val productDao = ProductDao()
                val productoBean = productDao.getProductoByArticulo(item.articulo)

                if (productoBean != null) {
                    if (productoBean.path_img != null) {
                        val decodedString = Base64.decode(productoBean.path_img, Base64.DEFAULT)
                        val decodedByte =
                            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        binding.imgProductoVenta.setImageBitmap(decodedByte)
                    }
                }

                itemView click  { onItemClickListener.onItemClick(adapterPosition) }

                itemView longClick  {
                    onItemLongClickListener.onItemLongClicked(adapterPosition)
                    false
                }
            }
        }
    }
}