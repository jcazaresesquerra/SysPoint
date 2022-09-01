package com.app.syspoint.ui.clientes;

import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.dao.ClientDao;
import com.app.syspoint.repository.database.dao.PaymentDao;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class TaskClients {

    final static public void recalculated_data(){
        final ClientDao clientDao = new ClientDao();
        final List<ClienteBean> listaClientesCredito = clientDao.getClientsByDay(Utils.fechaActual());
        final PaymentDao paymentDao = new PaymentDao();
        for( ClienteBean item : listaClientesCredito){

            try {
                final ClientDao dao = new ClientDao();
                item.setSaldo_credito(paymentDao.getTotalSaldoDocumentosCliente(item.getCuenta()));
                dao.save(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
