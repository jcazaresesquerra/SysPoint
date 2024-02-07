package com.app.syspoint.ui.home.adapter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.R
import com.app.syspoint.databinding.ItemListaClientesRutaBinding
import com.app.syspoint.repository.objectBox.entities.RuteClientBox
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.click
import com.app.syspoint.utils.longClick
import timber.log.Timber
import java.net.URLEncoder
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


private const val TAG = "AdapterRutaClientes"

class AdapterRutaClientes(
    data: List<RuteClientBox?>,
    val onItemClickListener: OnItemClickListener,
    val onItemLongClickListener: OnItemLongClickListener,
    val onCallPermissionRequestListener: OnCallPermissionRequestListener
    ): RecyclerView.Adapter<AdapterRutaClientes.Holder>(), Filterable {

    private var mData = data
    private var mDataFiltrable: List<RuteClientBox?> = data

    interface OnItemClickListener {
        fun onItemClick(clientBox: RuteClientBox, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(position: Int): Boolean
    }

    interface OnCallPermissionRequestListener {
        fun onCallPermissionRequest()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemListaClientesRutaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mDataFiltrable[position], position + 1, onItemClickListener, onItemLongClickListener, onCallPermissionRequestListener)
    }

    override fun getItemCount(): Int = if (mDataFiltrable.isEmpty()) 0 else mDataFiltrable.size

    fun setData(data: List<RuteClientBox?>) {
        mData = data
        mDataFiltrable = data
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filtro = constraint.toString()
                if (filtro.isEmpty()) {
                    mDataFiltrable = mData
                } else {
                    val filtroEmpleados: MutableList<RuteClientBox?> = ArrayList()
                    for (row in mData) {
                        if (row!!.nombre_comercial!!.lowercase(Locale.getDefault())
                                .contains(filtro) || row.calle!!.lowercase(
                                Locale.getDefault()
                            ).contains(filtro)
                        ) {
                            filtroEmpleados.add(row)
                        }
                    }
                    mDataFiltrable = filtroEmpleados
                }
                val results = FilterResults()
                results.values = mDataFiltrable
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mDataFiltrable = results!!.values as List<RuteClientBox?>
                notifyDataSetChanged()
            }


        }
    }

    class Holder(val binding: ItemListaClientesRutaBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(clienteBean: RuteClientBox?, position: Int, onItemClickListener: OnItemClickListener, onItemLongClickListener: OnItemLongClickListener, onCallPermissionRequestListener: OnCallPermissionRequestListener) {
            clienteBean?.let { clienteBean ->
                binding.textViewNombreClienteRuta.text = position.toString() + " - " + clienteBean.nombre_comercial
                binding.textViewDireccionClienteRuta.text = clienteBean.calle + " " + clienteBean.numero
                binding.textViewColoniaClienteRuta.text = "Col. " + clienteBean.colonia

                binding.imgCall click {
                    Timber.tag(TAG).d("AdapterRutaClientes -> Holder -> bind -> imgCall -> click")

                    if (ActivityCompat.checkSelfPermission(itemView.context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        onCallPermissionRequestListener.onCallPermissionRequest()
                    } else {
                        val popup =
                            PopupMenu(itemView.context, binding.imgCall)
                        //Inflating the Popup using xml file
                        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener { item ->

                            if (clienteBean.phone_contact.isNullOrEmpty() || clienteBean.phone_contact == "null") {
                                Timber.tag(TAG).d("AdapterRutaClientes -> Holder -> bind -> imgCall -> setOnMenuItemClickListener -> empty number")
                                Toast.makeText(
                                    itemView.context,
                                    "El cliente no cuenta con número de contacto",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@setOnMenuItemClickListener false
                            } else if (clienteBean.phone_contact?.length != 10) {
                                Timber.tag(TAG).d("AdapterRutaClientes -> Holder -> bind -> imgCall -> setOnMenuItemClickListener -> bad number ${clienteBean.phone_contact}")

                                Toast.makeText(
                                    itemView.context,
                                    "El número del cliente es erroneo ${clienteBean.phone_contact}",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@setOnMenuItemClickListener false
                            } else {
                                when(item.itemId ) {
                                    R.id.call_client -> {

                                        // Filtrar el número de teléfono
                                        val numeroTelefonoFiltrado = clienteBean.phone_contact!!.filter { it.isDigit() }

                                        Timber.tag(TAG)
                                            .d("AdapterRutaClientes -> Holder -> bind -> imgCall -> setOnMenuItemClickListener -> call number ${clienteBean.phone_contact}")

                                        val intent = Intent(
                                            Intent.ACTION_CALL,
                                            Uri.parse("tel:+52$numeroTelefonoFiltrado")
                                        )
                                        itemView.context.startActivity(intent)
                                    }
                                    R.id.send_whatsapp -> {
                                        Timber.tag(TAG)
                                            .d("AdapterRutaClientes -> Holder -> bind -> imgCall -> setOnMenuItemClickListener -> send WP ${clienteBean.phone_contact}")

                                        // Filtrar el número de teléfono
                                        val numeroTelefonoFiltrado = clienteBean.phone_contact!!.filter { it.isDigit() }

                                        val uri = Uri.parse(
                                            "https://api.whatsapp.com/send?phone=+52$numeroTelefonoFiltrado&text=" + URLEncoder.encode(
                                                "Soy el vendedor de Agua Point, Responsable de la entrega de agua en tu domicilio\n" +
                                                        "\n" +
                                                        "¿Deseas que visite tu domicilio el dia de hoy?"

                                            )
                                        )

                                        val pm: PackageManager = itemView.context.packageManager
                                        try {
                                            val waIntent = Intent(Intent.ACTION_VIEW, uri)
                                            val info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)

                                            waIntent.setPackage("com.whatsapp")
                                            itemView.context.startActivity(
                                                Intent.createChooser(
                                                    waIntent,
                                                    "Abrir con"
                                                )
                                            )
                                        } catch (e: Exception) {
                                            try {
                                                val waIntent = Intent(Intent.ACTION_VIEW, uri)

                                                val info = pm.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_META_DATA)

                                                waIntent.setPackage("com.whatsapp.w4b")
                                                itemView.context.startActivity(
                                                    Intent.createChooser(
                                                        waIntent,
                                                        "Abrir con"
                                                    )
                                                )
                                            } catch (e: Exception) {
                                                Toast.makeText(itemView.context, "WhatsApp no instalado", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                            true
                        }
                        popup.show() //showing popup menu
                    }
                }

                itemView longClick  {
                    Timber.tag(TAG).d("AdapterRutaClientes -> Holder -> bind -> imgCall -> longClick")
                    onItemLongClickListener.onItemLongClicked(adapterPosition)
                    false
                }

                itemView click  {
                    Timber.tag(TAG).d("AdapterRutaClientes -> Holder -> bind -> itemView -> click")
                    onItemClickListener.onItemClick(clienteBean, adapterPosition)
                }

                val calendar = Calendar.getInstance()
                val day = calendar[Calendar.DAY_OF_WEEK]


                if (clienteBean.ventaCreatedAt.isNullOrEmpty()) {
                    binding.tvLastVisit.text = "Ultima venta: sin ventas"
                } else {
                    try {
                        val diff = Utils.getCurrentDayHMS().time - Utils.getDataFromString(clienteBean.ventaCreatedAt).time
                        val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
                        if (days <= 0) {
                            binding.tvLastVisit.text = "Ultima venta: hoy"
                        } else if (days == 1L) {
                            binding.tvLastVisit.text = "Ultima venta: hace $days día"
                        } else {
                            binding.tvLastVisit.text = "Ultima venta: hace $days días"
                        }
                    } catch (e: Exception) {
                        binding.tvLastVisit.visibility = View.GONE
                    }
                }

                binding.textViewFechaVisitaClienteRuta.apply {
                    when (day) {
                        Calendar.SUNDAY -> if (clienteBean.lun == 1) {
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
                        } else if (clienteBean.sab == 1) {
                            text = "Sig visita SABADO"
                            return
                        } else if (clienteBean.dom == 1) {
                            text = "Sig visita DOMINGO"
                            return
                        }
                        Calendar.MONDAY -> {
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
                            } else if (clienteBean.lun == 1) {
                                text = "Sig visita LUNES"
                                return
                            }
                        }
                        Calendar.TUESDAY -> {
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
                            } else if (clienteBean.mar == 1) {
                                text = "Sig visita MARTES"
                                return
                            }
                        }
                        Calendar.WEDNESDAY -> {
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
                            } else if (clienteBean.mie == 1) {
                                text = "Sig visita MIERCOLES"
                                return
                            }
                        }
                        Calendar.THURSDAY -> {
                            if (clienteBean.vie == 1) {
                                text = "Sig visita VIERNES"
                                return
                            } else if (clienteBean.sab == 1) {
                                text = "Sig visita SABADO"
                                return
                            } else if (clienteBean.dom == 1) {
                                text  = "Sig visita DOMINGO"
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
                            } else if (clienteBean.jue == 1) {
                                text = "Sig visita JUEVES"
                                return
                            }
                        }
                        Calendar.FRIDAY -> {
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
                                text  =  "Sig visita MARTES"
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
                        Calendar.SATURDAY -> {
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
                            } else if (clienteBean.sab == 1) {
                                text = "Sig visita SABADO"
                                return
                            }
                        }
                    }
                }

                /*val routingDao = RoutingDao()
                val ruteoBean = routingDao.getRutaEstablecida()

                ruteoBean?.let { bean ->
                    binding.textViewFechaVisitaClienteRuta.apply {
                        if (bean.dia == 1) {
                            text = "Sig visita LUNES"
                            return
                        } else if (bean.dia == 2) {
                            text = "Sig visita MARTES"
                            return
                        } else if (bean.dia == 3) {
                            text = "Sig visita MIERCOLES"
                            return
                        } else if (bean.dia == 4) {
                            text = "Sig visita JUEVES"
                            return
                        } else if (bean.dia == 5) {
                            text = "Sig visita VIERNES"
                            return
                        } else if (bean.dia == 6) {
                            text = "Sig visita SABADO"
                            return
                        } else if (bean.dia == 7) {
                            text = "Sig visita DOMINGO"
                            return
                        }
                    }
                }*/


            }
        }
    }

}