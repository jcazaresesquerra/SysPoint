package com.app.syspoint.ui.ventas.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaProductosVentasBinding
import com.app.syspoint.repository.database.bean.ProductoBean
import com.app.syspoint.utils.click
import java.util.*

class AdapterListaProductosVentas(
    data: List<ProductoBean?>,
    val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<AdapterListaProductosVentas.Holder>(), Filterable {

    private var mData = data
    private var mDataFilter = data

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding = ItemListaProductosVentasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mDataFilter[position], onItemClickListener)
    }

    override fun getItemCount(): Int = if (mDataFilter.isEmpty()) 0 else mDataFilter.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filtro = constraint.toString()
                mDataFilter = if (filtro.isEmpty()) {
                    mData
                } else {

                    //TODO filtro productos
                    val filtroProductos: MutableList<ProductoBean> = ArrayList()
                    for (row in mDataFilter) {
                        row?.let {
                            if (row.articulo.lowercase(Locale.getDefault())
                                    .contains(filtro) || row.descripcion.lowercase(
                                    Locale.getDefault()
                                ).contains(filtro)
                            ) {
                                filtroProductos.add(row)
                            }
                        }
                    }
                    filtroProductos
                }
                val results = FilterResults()
                results.values = mDataFilter
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                mDataFilter = results.values as ArrayList<ProductoBean?>
                notifyDataSetChanged()
            }
        }
    }

    fun setData(data: List<ProductoBean?>) {
        mDataFilter = data
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemListaProductosVentasBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(productoBean: ProductoBean?, onItemClickListener: OnItemClickListener) {
            productoBean?.let { producto ->
                binding.textViewProductoCodigoListaVentaLista.text = producto.articulo
                binding.textViewProductoDescripcionVentaLista.text = producto.descripcion
                binding.textViewProductoPrecioListaVentaLista.text = producto.precio.toString()

                if (producto.path_img != null) {
                    val decodedString: ByteArray =
                        Base64.decode(producto.path_img, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    binding.imgProductoVentaLista.setImageBitmap(decodedByte)
                }
                itemView click  { onItemClickListener.onItemClick(adapterPosition) }

                binding.textViewProductoDisponibleListaVentaListaView.apply {
                    setText(producto.existencia)
                    if (producto.existencia > 0) {
                        setTextColor(Color.GREEN)
                    } else {
                        setTextColor(Color.RED)
                    }
                }
            }
        }
    }

}