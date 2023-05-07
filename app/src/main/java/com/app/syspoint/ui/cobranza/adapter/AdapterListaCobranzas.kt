package com.app.syspoint.ui.cobranza.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaCobranzaCardviewBinding
import com.app.syspoint.repository.objectBox.entities.CobrosBox
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.click

class AdapterListaCobranzas(
    data: List<CobrosBox?>,
    val onItemClickListener: OnItemClickListener
    ): RecyclerView.Adapter<AdapterListaCobranzas.Holder>() {

    private val mData = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemListaCobranzaCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position], onItemClickListener)
    }

    override fun getItemCount(): Int = if (mData.isEmpty()) 0 else mData.size

    class Holder(val binding: ItemListaCobranzaCardviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(cobrosBean: CobrosBox?, onItemClickListener: OnItemClickListener) {
            cobrosBean?.let {
                val total = it.importe
                val subtotalFormato = Utils.formatMoneyMX(total)
                binding.tvListaCobranzaClienteView.text = it.cliente!!.target.cuenta
                binding.tvListaCobranzaClienteNombreView.text = it.cliente!!.target.nombre_comercial
                binding.tvListaCobranzaCobranzaView.text = it.cobro.toString()
                binding.tvListaCobranzaHoraCobranzaView.text = it.hora
                binding.tvListaCobranzaFechaView.text = it.fecha
                binding.tvListaCobranzaImporteView.text = "$$subtotalFormato"

                binding.itemContainer click  {
                    onItemClickListener.onItemClick(adapterPosition)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}