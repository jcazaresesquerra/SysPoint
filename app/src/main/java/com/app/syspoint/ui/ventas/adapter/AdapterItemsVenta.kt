package com.app.syspoint.ui.ventas.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemVentasCardviewBinding
import com.app.syspoint.repository.objectBox.dao.ProductDao
import com.app.syspoint.repository.objectBox.entities.SellModelBox
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.click
import com.app.syspoint.utils.longClick
import com.app.syspoint.utils.setInvisible
import com.app.syspoint.utils.setVisible

class AdapterItemsVenta(
    data: List<SellModelBox?>,
    val onItemLongClickListener: OnItemLongClickListener,
    val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<AdapterItemsVenta.Holder>() {

    private var mData = data

    interface OnItemClickListener {
        fun onItemClick(sell: SellModelBox)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(sell: SellModelBox): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemVentasCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position], onItemLongClickListener, onItemClickListener)
    }

    override fun getItemCount(): Int = if (mData.isEmpty()) 0 else mData.size

    fun setData(data: List<SellModelBox?>) {
        mData = data
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemVentasCardviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(ventasModelBean: SellModelBox?, onItemLongClickListener: OnItemLongClickListener, onItemClickListener: OnItemClickListener) {
            ventasModelBean?.let { item ->
                binding.textViewProductoCodigoVenta.text = item.articulo
                binding.textViewProductoDescripcionVenta.text = item.descripcion
                binding.textViewProductoCantidadVenta.text = "Ven. " + item.cantidad
                if (item.returnQuantity != 0) {
                    binding.textViewProductoCantidadDevolucion.setVisible()
                    binding.textViewProductoCantidadDevolucion.text = "Dev. " + item.returnQuantity
                } else
                    binding.textViewProductoCantidadDevolucion.setInvisible()

                binding.textViewProductoPrecioVenta.text = Utils.FDinero(item.precio)
                val total: Double = item.precio * item.cantidad
                binding.textViewProductoTotalVenta.text = Utils.FDinero(total)

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

                itemView click  { onItemClickListener.onItemClick(item) }

                itemView longClick  {
                    onItemLongClickListener.onItemLongClicked(item)
                    false
                }
            }
        }
    }
}