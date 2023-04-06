package com.app.syspoint.usecases

import android.database.Observable
import com.app.syspoint.interactor.charge.ChargeInteractor
import com.app.syspoint.interactor.employee.GetEmployeeInteractor.GetEmployeesListener
import com.app.syspoint.interactor.prices.PriceInteractor.GetSpecialPricesListener
import com.app.syspoint.interactor.product.GetProductInteractor.OnGetProductsListener
import com.app.syspoint.interactor.roles.RolInteractor.OnGetAllRolesListener
import com.app.syspoint.models.Resource
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.request.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call

class GetDataUseCase {
    var getCharge = false
    var getEmployees = false
    var getProducts = false
    var getRoles = false
    var getPrices = false

    suspend operator fun invoke(): Flow<Resource<Boolean>> = callbackFlow {
        trySend(Resource.Loading)

        val requests = ArrayList<Call<*>>()
        val chargeCallback = RequestCharge.requestGetCharge(object: ChargeInteractor.OnGetChargeListener {
            override fun onGetChargeSuccess(chargeList: List<CobranzaBean>) {
                //trySend(Resource.Success(chargeList))
                getCharge = true
                if (getCharge && getEmployees && getProducts && getRoles)
                    trySend(Resource.Finished)
            }

            override fun onGetChargeError() {
                getCharge = true
                if (getCharge && getEmployees && getProducts && getRoles)
                    trySend(Resource.Finished)
                //trySend(Resource.Error("Ha ocurrido un error al obtener cobranzas"))
            }
        })


        val employeesCallback = RequestEmployees.requestEmployees(object : GetEmployeesListener {
            override fun onGetEmployeesSuccess(employees: List<EmpleadoBean?>) {
                getEmployees = true
                if (getCharge && getEmployees && getProducts && getRoles)
                    trySend(Resource.Finished)
            }

            override fun onGetEmployeesError() {
                getEmployees = true
                if (getCharge && getEmployees && getProducts && getRoles)
                    trySend(Resource.Finished)
            }
        })

        val productCallback = RequestProducts.requestProducts(object : OnGetProductsListener {
            override fun onGetProductsSuccess(products: List<ProductoBean?>) {
                getProducts = true
                if (getCharge && getEmployees && getProducts && getRoles)
                    trySend(Resource.Finished)
            }

            override fun onGetProductsError() {
                getProducts = true
                if (getCharge && getEmployees && getProducts && getRoles)
                    trySend(Resource.Finished)
            }
        })

        val rolesCallback = RequestRol.requestAllRoles(object : OnGetAllRolesListener {
            override fun onGetAllRolesSuccess(roles: List<RolesBean>) {
                getRoles = true
                if (getCharge && getEmployees && getProducts && getRoles)
                    trySend(Resource.Finished)
            }

            override fun onGetAllRolesError() {
                getRoles = true
                if (getCharge && getEmployees && getProducts && getRoles)
                    trySend(Resource.Finished)
            }
        })

        val pricesCallback = RequestPrice.requestAllPrices(object : GetSpecialPricesListener {
            override fun onGetSpecialPricesSuccess(priceList: List<PreciosEspecialesBean>) {
                getPrices = true

            }

            override fun onGetSpecialPricesError() {
                getPrices = true

            }
        })



        awaitClose {
            chargeCallback.cancel()
            employeesCallback.cancel()
            productCallback.cancel()
            rolesCallback.cancel()
            pricesCallback.cancel()
        }
    }
}