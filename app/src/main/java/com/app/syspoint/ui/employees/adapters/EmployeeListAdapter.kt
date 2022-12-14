package com.app.syspoint.ui.employees.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaEmpleadosBinding
import com.app.syspoint.repository.database.bean.EmpleadoBean
import com.app.syspoint.utils.click
import java.util.*

class EmployeeListAdapter(
    data: List<EmpleadoBean?>,
    val onItemClickListener: OnItemClickListener
    ): RecyclerView.Adapter<EmployeeListAdapter.Holder>(), Filterable {

    private var mData = data
    private var mDataFilter = data

    interface OnItemClickListener {
        fun onItemClick(employeeBean: EmpleadoBean?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemListaEmpleadosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mDataFilter[position], onItemClickListener)
    }

    override fun getItemCount(): Int = if (mDataFilter.isEmpty()) 0 else mDataFilter.size

    fun setData(data: List<EmpleadoBean?>) {
        mDataFilter = data
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
                    val filtroEmpleados: MutableList<EmpleadoBean?> = ArrayList()
                    for (row in mData) {
                        row?.let {
                            if (row.nombre.lowercase(Locale.getDefault()).contains(filtro)
                                || row.identificador.lowercase(Locale.getDefault()).contains(filtro)) {
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
                mDataFilter = results.values as ArrayList<EmpleadoBean?>
                notifyDataSetChanged()
            }
        }
    }

    class Holder(val binding: ItemListaEmpleadosBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(empleadoBean: EmpleadoBean?, onItemClickListener: OnItemClickListener) {
            empleadoBean?.let { empleado ->
                binding.textViewListaEmpleadoNombre.text = empleado.getNombre()
                binding.textViewListaEmpleadoIdentificador.text = empleado.getIdentificador()

                if (empleado.getPath_image() != null) {
                    val decodedString: ByteArray =
                        Base64.decode(empleado.getPath_image(), Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    binding.imgEmpleado.setImageBitmap(decodedByte)
                }

                itemView click { onItemClickListener.onItemClick(empleado) }
            }
        }
    }
}