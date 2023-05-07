package com.app.syspoint.usecases

import com.app.syspoint.models.Resource
import com.app.syspoint.repository.objectBox.dao.ChargeDao
import com.app.syspoint.repository.objectBox.dao.StockDao
import com.app.syspoint.repository.objectBox.entities.ChargeBox
import com.app.syspoint.repository.request.RequestCharge
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.awaitResponse
import java.text.SimpleDateFormat

class GetChargeUseCase {

    suspend operator fun invoke(): Flow<Resource<List<ChargeBox>>> = callbackFlow {
        trySend(Resource.Loading)

        val call = RequestCharge.requestGetCharge2()

        val response = call.awaitResponse()

        if (response.isSuccessful) {
            val chargeList = arrayListOf<ChargeBox>()
            val stockId = StockDao().getCurrentStockId()
            val chargeDao = ChargeDao()

            response.body()!!.payments!!.map {item ->
                val charge = chargeDao.getByCobranza(item!!.cobranza)
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                if (charge == null) {
                    val chargeBox = ChargeBox()
                    chargeBox.cobranza = item.cobranza
                    chargeBox.cliente = item.cuenta
                    chargeBox.importe = item.importe
                    chargeBox.saldo = item.saldo
                    chargeBox.venta = item.venta
                    chargeBox.estado = item.estado
                    chargeBox.observaciones = item.observaciones
                    chargeBox.fecha = item.fecha
                    chargeBox.hora = item.hora
                    chargeBox.empleado = item.identificador
                    chargeBox.updatedAt = formatter.parse(item.updatedAt)
                    chargeBox.stockId = stockId
                    chargeDao.insert(chargeBox)
                    chargeList.add(chargeBox)
                } else {

                    val update = if (charge.updatedAt != null && !formatter.format(charge.updatedAt).isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                        val dateItem = try {
                            formatter.parse(item.updatedAt)
                        } catch (e: Exception) {
                            formatter.parse(item.updatedAt + "00:00:00")
                        }
                        val dateBean = try {
                            charge.updatedAt
                        } catch (e: Exception) {
                            formatter.parse(formatter.format(charge.updatedAt) + "00:00:00")
                        }
                        dateItem?.compareTo(dateBean) ?: 1
                    } else 1

                    if (update > 0) {
                        charge.cobranza = item.cobranza
                        charge.cliente = item.cuenta
                        charge.importe = item.importe
                        charge.saldo = item.saldo
                        charge.venta = item.venta
                        charge.estado = item.estado
                        charge.observaciones = item.observaciones
                        charge.fecha = item.fecha
                        charge.hora = item.hora
                        charge.empleado = item.identificador
                        charge.updatedAt = formatter.parse(item.updatedAt)
                        charge.stockId = stockId
                        chargeDao.insert(charge)
                    }
                    chargeList.add(charge)
                }
            }
            trySend(Resource.Success(chargeList))
        } else {
            trySend(Resource.Error("Ha ocurrido un error al obtener cobranzas"))
        }

        awaitClose {
            call.cancel()
        }
    }
}