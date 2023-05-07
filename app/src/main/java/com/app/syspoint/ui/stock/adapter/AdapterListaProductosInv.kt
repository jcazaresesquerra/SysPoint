package com.app.syspoint.ui.stock.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaProductosInventarioBinding
import com.app.syspoint.repository.objectBox.entities.ProductBox
import com.app.syspoint.utils.click
import timber.log.Timber
import java.util.*

private const val TAG = "AdapterListaProductosInv"

class AdapterListaProductosInv(
    data: List<ProductBox?>,
    val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<AdapterListaProductosInv.Holder>(), Filterable {

    private var mData = data
    private var mDataFilter = data

    interface OnItemClickListener {
        fun onItemClick(productoBean: ProductBox?)
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
                    val filtroProductos: MutableList<ProductBox> = ArrayList()
                    for (row in mDataFilter) {
                        row?.let {
                            if (row.articulo!!.lowercase(Locale.getDefault())
                                    .contains(filtro) || row.descripcion!!.lowercase(
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
                mDataFilter = results.values as ArrayList<ProductBox?>
                notifyDataSetChanged()
            }
        }
    }

    fun setData(data: List<ProductBox?>) {
        mDataFilter = data
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemListaProductosInventarioBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(productoBean: ProductBox?, onItemClickListener: OnItemClickListener) {
            productoBean?.let { producto ->
                binding.textViewArticuloListInv.text = producto.articulo
                binding.textViewDescripcionProductoListInv.text = producto.descripcion
                binding.textViewPreciosArticuloListInv.text = "$" + producto.precio
                binding.textViewArticuloIVAListInv.text = producto.iva.toString() + "%"
                binding.textViewArticuloCategoriaListInv.text = "SYS"
                binding.textViewArticuloStatusListInv.text = producto.status
                binding.textViewArticuloCodBarrasListInv.text = producto.codigo_barras

                if (producto.path_img != null) {
                    val decodedString: ByteArray =
                        Base64.decode(producto.path_img, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    binding.imageView2.setImageBitmap(decodedByte)
                }

                itemView click {
                    Timber.tag(TAG).d("AdapterListaProductosInv -> Holder -> bind -> itemView -> click");
                    onItemClickListener.onItemClick(producto)
                }
            }
        }
    }

}