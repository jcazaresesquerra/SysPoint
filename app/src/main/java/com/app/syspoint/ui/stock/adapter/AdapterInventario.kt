package com.app.syspoint.ui.stock.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemProductoInventarioBinding
import com.app.syspoint.repository.database.bean.InventarioBean
import com.app.syspoint.repository.database.dao.StockHistoryDao
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.longClick

class AdapterInventario(
    data: List<InventarioBean?>,
    val onItemLongClickListener: OnItemLongClickListener
    ): RecyclerView.Adapter<AdapterInventario.Holder>() {

    private var mData = data

    interface OnItemLongClickListener {
        fun onItemLongClicked(position: Int): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemProductoInventarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position], onItemLongClickListener)
    }

    override fun getItemCount(): Int = if (mData.isEmpty()) 0 else mData.size

    fun setData(mDataRefresh: List<InventarioBean?>) {
        mData = mDataRefresh
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemProductoInventarioBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(inventarioBean: InventarioBean?, onItemLongClickListener: OnItemLongClickListener) {
            inventarioBean?.let { item ->
                val stockHistoryDao = StockHistoryDao()
                val inventarioHistorialBean =
                    stockHistoryDao.getInvatarioPorArticulo(item.articulo.articulo)

                val vendido = inventarioHistorialBean?.cantidad ?: 0
                val inicial = item.totalCantidad

                binding.tvInventarioTotal.text = Utils.FDinero(item.cantidad * item.precio)
                binding.tvInventarioCantidad.text = inicial.toString()
                binding.tvInventarioDescripcion.text = item.articulo.descripcion
                binding.tvInventarioArticulo.text = item.articulo.articulo
                binding.tvInventarioVendido.text = vendido.toString()
                val total = inicial - vendido

                binding.tvInventarioTotalInventario.text = total.toString()
                if (item.articulo.path_img != null) {
                    val decodedString: ByteArray =
                        Base64.decode(item.articulo.path_img, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    binding.imageView4Inv.setImageBitmap(decodedByte)
                }

                itemView longClick  {
                    onItemLongClickListener.onItemLongClicked(adapterPosition)
                    false
                }
            }
        }
    }


}