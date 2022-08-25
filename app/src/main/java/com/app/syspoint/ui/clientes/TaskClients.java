package com.app.syspoint.ui.clientes;

import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.dao.ClienteDao;
import com.app.syspoint.repository.database.dao.CobranzaDao;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class TaskClients {

    final static public void recalculated_data(){
        final ClienteDao clienteDao = new ClienteDao();
        final List<ClienteBean> listaClientesCredito = clienteDao.getClientsByDay(Utils.fechaActual());
        final CobranzaDao cobranzaDao = new CobranzaDao();
        for( ClienteBean item : listaClientesCredito){

            try {
                final ClienteDao dao = new ClienteDao();
                item.setSaldo_credito(cobranzaDao.getTotalSaldoDocumentosCliente(item.getCuenta()));
                dao.save(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
