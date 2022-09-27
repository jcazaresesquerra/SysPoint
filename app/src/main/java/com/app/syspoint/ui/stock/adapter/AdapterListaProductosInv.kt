package com.app.syspoint.ui.stock.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaProductodBinding
import com.app.syspoint.databinding.ItemListaProductosInventarioBinding
import com.app.syspoint.repository.database.bean.ProductoBean
import com.app.syspoint.utils.click
import java.util.*

class AdapterListaProductosInv(
    data: List<ProductoBean?>,
    val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<AdapterListaProductosInv.Holder>(), Filterable {

    private var mData = data
    private var mDataFilter = data

    interface OnItemClickListener {
        fun onItemClick(productoBean: ProductoBean?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding = ItemListaProductosInventarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class Holder(val binding: ItemListaProductosInventarioBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(productoBean: ProductoBean?, onItemClickListener: OnItemClickListener) {
            productoBean?.let { producto ->
                binding.textViewArticuloListInv.text = producto.articulo
                binding.textViewDescripcionProductoListInv.text = producto.descripcion
                binding.textViewPreciosArticuloListInv.text = "$" + producto.precio
                binding.textViewArticuloUnidadMedidaListInv.text = producto.unidad_medida
                binding.textViewArticuloIVAListInv.text = producto.iva.toString() + "%"
                binding.textViewArticuloIESPListInv.text = producto.ieps.toString() + "%"
                binding.textViewArticuloCategoriaListInv.text = "SYS"
                binding.textViewArticuloPrioridadListInv.text = producto.prioridad.toString()
                binding.textViewArticuloStatusListInv.text = producto.status
                binding.textViewArticuloCodAlfaListInv.text = producto.codigo_alfa
                binding.textViewArticuloCodBarrasListInv.text = producto.codigo_barras
                binding.textViewArticuloRegionListInv.text = producto.region

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