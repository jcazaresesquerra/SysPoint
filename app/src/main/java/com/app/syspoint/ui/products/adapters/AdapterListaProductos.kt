package com.app.syspoint.ui.products.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaProductodBinding
import com.app.syspoint.repository.database.bean.ProductoBean
import com.app.syspoint.utils.click
import java.util.*

class AdapterListaProductos(
    data: List<ProductoBean?>,
    val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<AdapterListaProductos.Holder>(), Filterable {

    private var mData = data
    private var mDataFilter = data

    interface OnItemClickListener {
        fun onItemClick(productoBean: ProductoBean?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding = ItemListaProductodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position], onItemClickListener)
    }

    override fun getItemCount(): Int = if (mData.isEmpty()) 0 else mData.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filtro = constraint.toString()
                if (filtro.isEmpty()) {
                    mDataFilter = mData
                } else {
                    //TODO filtro productos
                    val filtroProductos: MutableList<ProductoBean> = ArrayList()
                    for (row in mDataFilter) {
                        row?.let {
                            if (row.articulo.lowercase(Locale.getDefault()).contains(filtro) ||
                                row.descripcion.lowercase(Locale.getDefault()).contains(filtro)
                            ) {
                                filtroProductos.add(row)
                            }
                        }
                    }
                    mDataFilter = filtroProductos
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
        mData = data
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemListaProductodBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(productoBean: ProductoBean?, onItemClickListener: OnItemClickListener) {
            productoBean?.let { producto ->
                binding.textViewArticuloList.text = producto.articulo
                binding.textViewDescripcionProductoList.text = producto.descripcion
                binding.textViewPreciosArticuloList.text = "$" + producto.precio
                binding.textViewArticuloIVAList.text = producto.iva.toString() + "%"
                binding.textViewArticuloCategoriaList.text = "SYS"
                binding.textViewArticuloStatusList.text = producto.status
                binding.textViewArticuloCodBarrasList.text = producto.codigo_barras

                if (producto.path_img != null) {
                    val decodedString: ByteArray =
                        Base64.decode(producto.path_img, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    binding.imageView2.setImageBitmap(decodedByte)
                }

                itemView click {
                    onItemClickListener.onItemClick(producto)
                }
            }
        }
    }
}