package com.app.syspoint.ui.home.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.R
import com.app.syspoint.databinding.ItemListaClientesRutaBinding
import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.utils.click
import com.app.syspoint.utils.longClick
import java.util.*

class AdapterRutaClientes(
    data: List<ClientesRutaBean?>,
    val onItemClickListener: OnItemClickListener,
    val onItemLongClickListener: OnItemLongClickListener
    ): RecyclerView.Adapter<AdapterRutaClientes.Holder>() {

    private var mData = data

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(position: Int): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemListaClientesRutaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position], onItemClickListener, onItemLongClickListener)
    }

    override fun getItemCount(): Int = if (mData.isEmpty()) 0 else mData.size

    fun setData(data: List<ClientesRutaBean?>) {
        mData = data
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemListaClientesRutaBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(clienteBean: ClientesRutaBean?, onItemClickListener: OnItemClickListener, onItemLongClickListener: OnItemLongClickListener) {
            clienteBean?.let { clienteBean ->
                binding.textViewNombreClienteRuta.text = clienteBean.nombre_comercial
                binding.textViewDireccionClienteRuta.text = clienteBean.calle + " " + clienteBean.numero
                binding.textViewColoniaClienteRuta.text = "Col. " + clienteBean.colonia

                binding.imgCall click {
                    val popup =
                        PopupMenu(itemView.context, binding.imgCall)
                    //Inflating the Popup using xml file
                    popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener { _ ->
                        if (clienteBean.phone_contact.isNullOrEmpty() || clienteBean.phone_contact == "null") {
                            Toast.makeText(
                                itemView.context,
                                "El cliente no cuenta con número de contacto",
                                Toast.LENGTH_LONG
                            ).show()
                            return@setOnMenuItemClickListener false
                        } else {
                            val intent = Intent(
                                Intent.ACTION_CALL,
                                Uri.parse("tel:" + "+52" + clienteBean.phone_contact)
                            )
                            itemView.context.startActivity(intent)
                        }
                        true
                    }
                    popup.show() //showing popup menu
                }

                itemView longClick  {
                    onItemLongClickListener.onItemLongClicked(adapterPosition)
                    false
                }

                itemView click  { onItemClickListener.onItemClick(adapterPosition) }

                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_WEEK]

                when (day) {
                    Calendar.SUNDAY -> {
                        binding.textViewFechaVisitaClienteRuta.apply {
                            if (clienteBean.getLun() == 1) {
                                text = "Sig visita LUNES"
                                return
                            } else if (clienteBean.getMar() == 1) {
                                text = "Sig visita MARTES"
                                return
                            } else if (clienteBean.getMie() == 1) {
                                text = "Sig visita MIERCOLES"
                                return
                            } else if (clienteBean.getJue() == 1) {
                                text = "Sig visita JUEVES"
                                return
                            } else if (clienteBean.getVie() == 1) {
                                text = "Sig visita VIERNES"
                                return
                            } else if (clienteBean.getSab() == 1) {
                                text = "Sig visita SABADO"
                                return
                            }
                        }
                    }
                    Calendar.MONDAY -> {
                        binding.textViewFechaVisitaClienteRuta.apply {
                            if (clienteBean.mar == 1) {
                                text = "Sig visita MARTES"
                                return
                            } else if (clienteBean.mie == 1) {
                                text = "Sig visita MIERCOLES"
                                return
                            } else if (clienteBean.jue == 1) {
                                text = "Sig visita JUEVES"
                                return
                            } else if (clienteBean.vie == 1) {
                                text = "Sig visita VIERNES"
                                return
                            } else if (clienteBean.sab == 1) {
                                text = "Sig visita SABADO"
                                return
                            } else if (clienteBean.dom == 1) {
                                text = "Sig visita DOMINGO"
                                return
                            }
                        }
                    }
                    Calendar.TUESDAY -> {
                        binding.textViewFechaVisitaClienteRuta.apply {
                            if (clienteBean.mie == 1) {
                                text = "Sig visita MIERCOLES"
                                return
                            } else if (clienteBean.jue == 1) {
                                text = "Sig visita JUEVES"
                                return
                            } else if (clienteBean.vie == 1) {
                                text = "Sig visita VIERNES"
                                return
                            } else if (clienteBean.sab == 1) {
                                text = "Sig visita SABADO"
                                return
                            } else if (clienteBean.dom == 1) {
                                text = "Sig visita DOMINGO"
                                return
                            } else if (clienteBean.lun == 1) {
                                text = "Sig visita LUNES"
                                return
                            }
                        }
                    }
                    Calendar.WEDNESDAY -> {
                        binding.textViewFechaVisitaClienteRuta.apply {
                            if (clienteBean.jue == 1) {
                                text = "Sig visita JUEVES"
                                return
                            } else if (clienteBean.vie == 1) {
                                text = "Sig visita VIERNES"
                                return
                            } else if (clienteBean.sab == 1) {
                                text = "Sig visita SABADO"
                                return
                            } else if (clienteBean.dom == 1) {
                                text = "Sig visita DOMINGO"
                                return
                            } else if (clienteBean.lun == 1) {
                                text = "Sig visita LUNES"
                                return
                            } else if (clienteBean.mar == 1) {
                                text = "Sig visita LUNES"
                                return
                            }
                        }
                    }
                    Calendar.THURSDAY -> {
                        binding.textViewFechaVisitaClienteRuta.apply {
                            if (clienteBean.vie == 1) {
                                text = "Sig visita VIERNES"
                                return
                            } else if (clienteBean.sab == 1) {
                                text = "Sig visita SABADO"
                                return
                            } else if (clienteBean.dom == 1) {
                                text = "Sig visita DOMINGO"
                                return
                            } else if (clienteBean.lun == 1) {
                                text = "Sig visita LUNES"
                                return
                            } else if (clienteBean.mar == 1) {
                                text = "Sig visita MARTES"
                                return
                            } else if (clienteBean.mar == 1) {
                                text = "Sig visita MIERCOLES"
                                return
                            }
                        }
                    }
                    Calendar.FRIDAY -> {
                        binding.textViewFechaVisitaClienteRuta.apply {
                            if (clienteBean.sab == 1) {
                                text = "Sig visita SABADO"
                                return
                            } else if (clienteBean.dom == 1) {
                                text = "Sig visita DOMINGO"
                                return
                            } else if (clienteBean.lun == 1) {
                                text = "Sig visita LUNES"
                                return
                            } else if (clienteBean.mar == 1) {
                                text = "Sig visita MARTES"
                                return
                            } else if (clienteBean.mie == 1) {
                                text = "Sig visita MIERCOLES"
                                return
                            } else if (clienteBean.jue == 1) {
                                text = "Sig visita JUEVES"
                                return
                            }
                        }
                    }
                    Calendar.SATURDAY -> {
                        binding.textViewFechaVisitaClienteRuta.apply {
                            if (clienteBean.dom == 1) {
                                text = "Sig visita DOMINGO"
                                return
                            } else if (clienteBean.lun == 1) {
                                text = "Sig visita LUNES"
                                return
                            } else if (clienteBean.mar == 1) {
                                text = "Sig visita MARTES"
                                return
                            } else if (clienteBean.mie == 1) {
                                text = "Sig visita MIERCOLES"
                                return
                            } else if (clienteBean.jue == 1) {
                                text = "Sig visita JUEVES"
                                return
                            } else if (clienteBean.vie == 1) {
                                text = "Sig visita VIERNES"
                                return
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

}