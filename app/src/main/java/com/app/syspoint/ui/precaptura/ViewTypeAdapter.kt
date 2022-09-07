package com.app.syspoint.ui.precaptura

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.R
import com.app.syspoint.models.VisitType

class ViewTypeAdapter(data: List<VisitType>, onItemClickListener: OnItemClickListener): RecyclerView.Adapter<ViewTypeAdapter.Holder>() {

    private val mData: List<VisitType> = data
    private val mOnItemClickListener: OnItemClickListener = onItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_tipo_visita, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position], mOnItemClickListener)
    }

    override fun getItemCount(): Int {
        return if (mData.isNotEmpty()) mData.size else 0

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTipo: TextView
        private val card_tipo_seleccion: CardView

        fun bind(item: VisitType, onItemClickListener: ViewTypeAdapter.OnItemClickListener) {
            textViewTipo.text = "" + item.name
            itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
            if (item.isSelected) {
                card_tipo_seleccion.setBackgroundColor(Color.CYAN)
            } else {
                card_tipo_seleccion.setBackgroundColor(Color.WHITE)
            }
        }

        init {
            textViewTipo = itemView.findViewById(R.id.tv_tipo)
            card_tipo_seleccion = itemView.findViewById(R.id.card_tipo_seleccion)
        }
    }
}