package com.app.syspoint.ui.employees.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaEmpleadosBinding
import com.app.syspoint.repository.objectBox.entities.EmployeeBox
import com.app.syspoint.utils.click
import com.github.satoshun.coroutine.autodispose.view.autoDisposeScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class EmployeeListAdapter(
    data: List<EmployeeBox?>,
    val onItemClickListener: OnItemClickListener
    ): RecyclerView.Adapter<EmployeeListAdapter.Holder>(), Filterable {

    private var mData = data
    private var mDataFilter = data

    interface OnItemClickListener {
        fun onItemClick(employeeBean: EmployeeBox?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemListaEmpleadosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mDataFilter[position], onItemClickListener)
    }

    override fun getItemCount(): Int = if (mDataFilter.isEmpty()) 0 else mDataFilter.size

    fun setData(data: List<EmployeeBox?>) {
        mDataFilter = data
        mData = data
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filtro = constraint.toString()
                if (filtro.isEmpty()) {
                    mDataFilter = mData
                } else {

                    //TODO filtro productos
                    val filtroEmpleados: MutableList<EmployeeBox?> = ArrayList()
                    for (row in mData) {
                        row?.let {
                            if (row.nombre!!.lowercase(Locale.getDefault()).contains(filtro)
                                || row.identificador!!.lowercase(Locale.getDefault()).contains(filtro)) {
                                filtroEmpleados.add(row)
                            }
                        }
                    }
                    mDataFilter = filtroEmpleados
                }
                val results = FilterResults()
                results.values = mDataFilter
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                mDataFilter = results.values as ArrayList<EmployeeBox?>
                notifyDataSetChanged()
            }
        }
    }

    class Holder(val binding: ItemListaEmpleadosBinding): RecyclerView.ViewHolder(binding.root) {

        val _stateFlow = MutableStateFlow(-1)
        val stateFlow = _stateFlow.asStateFlow()

        fun bind(empleadoBean: EmployeeBox?, onItemClickListener: OnItemClickListener) {
            empleadoBean?.let { empleado ->
                binding.textViewListaEmpleadoNombre.text = empleado.nombre
                binding.textViewListaEmpleadoIdentificador.text = empleado.identificador

                if (empleado.path_image != null) {
                    binding.imgEmpleado.autoDisposeScope.launch(Dispatchers.Default) {
                        val decodedString: ByteArray =
                            Base64.decode(empleado.path_image, Base64.DEFAULT)
                        val decodedByte =
                            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                        CoroutineScope(Dispatchers.Main).launch {
                            stateFlow.collect { id ->
                                binding.imgEmpleado.setImageBitmap(decodedByte)
                            }
                        }

                    }
                }

                itemView click { onItemClickListener.onItemClick(empleado) }
            }
        }
    }
}