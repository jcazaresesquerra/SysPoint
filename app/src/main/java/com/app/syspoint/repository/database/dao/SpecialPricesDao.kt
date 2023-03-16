package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.PreciosEspecialesBean
import com.app.syspoint.repository.database.bean.PreciosEspecialesBeanDao

class SpecialPricesDao: Dao("PreciosEspecialesBean") {

    //Retorna el precio por cliente y por articulo
    fun getPrecioEspeciaPorCliente(
        productID: String?,
        clientID: String?
    ): PreciosEspecialesBean? {
        return try {
            val ruteoBeans = dao.queryBuilder()
                .where(
                    PreciosEspecialesBeanDao.Properties.Articulo.eq(productID),
                    PreciosEspecialesBeanDao.Properties.Cliente.eq(clientID),
                    PreciosEspecialesBeanDao.Properties.Active.eq(true)
                )
                .list() as List<PreciosEspecialesBean>
            if (ruteoBeans.isNotEmpty()) ruteoBeans[0] else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //Retorna la listos especiales por ID
    fun getListaPrecioPorCliente(clienteID: String?): List<PreciosEspecialesBean> {
        return dao.queryBuilder()
            .where(
                PreciosEspecialesBeanDao.Properties.Cliente.eq(clienteID),
                PreciosEspecialesBeanDao.Properties.Active.eq(
                    true
                )
            )
            .list() as List<PreciosEspecialesBean>
    }


    //Retorna la listos especiales por ID
    fun getListaPrecioPorClienteUpdate(clienteID: String?): List<PreciosEspecialesBean> {
        return dao.queryBuilder()
            .where(
                PreciosEspecialesBeanDao.Properties.Cliente.eq(clienteID),
                PreciosEspecialesBeanDao.Properties.Active.eq(
                    false
                )
            )
            .list() as List<PreciosEspecialesBean>
    }


    fun getPreciosBydate(fecha: String?): List<PreciosEspecialesBean> {
        return dao.queryBuilder()
            .where(
                PreciosEspecialesBeanDao.Properties.Fecha_sync.eq(fecha),
                PreciosEspecialesBeanDao.Properties.Active.eq(
                    false
                )
            )
            .list() as List<PreciosEspecialesBean>
    }


    fun getPrecioEspecialPorIdentificador(
        cliente: String?,
        articulo: String?
    ): PreciosEspecialesBean? {
        val ruteoBeans = dao.queryBuilder()
            .where(
                PreciosEspecialesBeanDao.Properties.Articulo.eq(articulo),
                PreciosEspecialesBeanDao.Properties.Cliente.eq(cliente)
            )
            .list() as List<PreciosEspecialesBean>
        return if (ruteoBeans.size > 0) ruteoBeans[0] else null
    }
}