package com.app.syspoint.ui.cobranza.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaDocumentosCardviewBinding
import com.app.syspoint.repository.database.bean.CobranzaBean
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.setInvisible
import com.app.syspoint.utils.setVisible

class AdapterListaDocumentosCobranza(
    data: List<CobranzaBean?>,
    val onItemClickListener: OnItemClickListener,
    val onItemLongClickListener: OnItemLongClickListener
    ): RecyclerView.Adapter<AdapterListaDocumentosCobranza.Holder>() {

    private var mData = data

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(position: Int): Boolean
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding = ItemListaDocumentosCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position], onItemClickListener, onItemLongClickListener)
    }

    override fun getItemCount(): Int= if (mData.isEmpty()) 0 else mData.size

    fun setData(data: List<CobranzaBean?>) {
        mData = data
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemListaDocumentosCardviewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(cobranzaBean: CobranzaBean?, onItemClickListener: OnItemClickListener, onItemLongClickListener: OnItemLongClickListener) {
            cobranzaBean?.let {
                binding.tvVentaCobranzaView.text = cobranzaBean.venta.toString()
                binding.tvCobranzaCobranzaView.text = cobranzaBean.cobranza
                binding.tvImporteCobranzaView.text = Utils.FDinero(cobranzaBean.importe)
                binding.tvSaldoCobranzaView.text = Utils.FDinero(cobranzaBean.saldo)
                binding.tvFechaCobranzaView.text = cobranzaBean.fecha

                if (cobranzaBean.isCheck) {
                    binding.imageViewCheck.setVisible()
                } else {
                    binding.imageViewCheck.setInvisible()
                }

                itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }

                itemView.setOnLongClickListener {
                    onItemLongClickListener.onItemLongClicked(adapterPosition)
                    false
                }
            }
        }
    }
}