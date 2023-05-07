package com.app.syspoint.ui.clientes;

import com.app.syspoint.repository.objectBox.dao.ChargeDao;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class TaskClients {

    static public void recalculated_data(){
        final ClientDao clientDao = new ClientDao();
        final List<ClientBox> listaClientesCredito = clientDao.getClientsByDay(Utils.fechaActual());
        final ChargeDao chargeDao = new ChargeDao();
        for (ClientBox item : listaClientesCredito){
            try {
                final ClientDao dao = new ClientDao();
                item.setSaldo_credito(chargeDao.getSaldoByCliente(item.getCuenta()));
                dao.insertBox(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
