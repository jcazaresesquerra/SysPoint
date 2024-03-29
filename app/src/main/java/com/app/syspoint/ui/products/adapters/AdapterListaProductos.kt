package com.app.syspoint.ui.products.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaProductodBinding
import com.app.syspoint.repository.objectBox.entities.ProductBox
import com.app.syspoint.utils.click
import com.github.satoshun.coroutine.autodispose.view.autoDisposeScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class AdapterListaProductos(
    data: List<ProductBox?>,
    val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<AdapterListaProductos.Holder>(), Filterable {

    private var mData = data
    private var mDataFilter = data

    interface OnItemClickListener {
        fun onItemClick(productoBean: ProductBox?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding = ItemListaProductodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                mDataFilter =if (filtro.isEmpty()) {
                    mData
                } else {
                    //TODO filtro productos
                    val filtroProductos: MutableList<ProductBox> = ArrayList()
                    for (row in mDataFilter) {
                        row?.let {
                            if (row.articulo!!.toLowerCase().contains(filtro.toLowerCase()) ||
                                row.descripcion!!.toLowerCase().contains(filtro.toLowerCase())
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
        mData = data
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemListaProductodBinding): RecyclerView.ViewHolder(binding.root) {

        val _stateFlow = MutableStateFlow(-1)
        val stateFlow = _stateFlow.asStateFlow()

        fun bind(productoBean: ProductBox?, onItemClickListener: OnItemClickListener) {
            productoBean?.let { producto ->

                binding.textViewArticuloList.text = producto.articulo
                binding.textViewDescripcionProductoList.text = producto.descripcion
                binding.textViewPreciosArticuloList.text = "$" + producto.precio
                binding.textViewArticuloIVAList.text = producto.iva.toString() + "%"
                binding.textViewArticuloCategoriaList.text = "SYS"
                binding.textViewArticuloStatusList.text = producto.status
                binding.textViewArticuloCodBarrasList.text = producto.codigo_barras

                if (producto.path_img != null) {
                    binding.imageView2.autoDisposeScope.launch(Dispatchers.Default) {
                        val decodedString: ByteArray =
                            Base64.decode(producto.path_img, Base64.DEFAULT)
                        val decodedByte =
                            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        CoroutineScope(Dispatchers.Main).launch {
                            stateFlow.collect { id ->
                                binding.imageView2.setImageBitmap(decodedByte)
                            }
                        }

                    }
                }

                itemView click {
                    onItemClickListener.onItemClick(producto)
                }
            }
        }
    }
}