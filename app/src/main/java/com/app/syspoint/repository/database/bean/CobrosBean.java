package com.app.syspoint.repository.database.bean;

import com.app.syspoint.db.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

@Entity(nameInDb = "cobros",
        indexes = {
                @Index(value = "cobro")
        })
public class CobrosBean extends Bean {


    @Id(autoincrement = true)
    private Long id;
    private int cobro;
    private String fecha;
    private String hora;
    private Long clienteId;
    @ToOne(joinProperty = "clienteId")
    private ClienteBean cliente;
    private Long empleadoId;
    @ToOne(joinProperty = "empleadoId")
    private EmpleadoBean empleado;
    private double importe;
    private String estado;
    private int temporal = 0;
    private int sinc = 0;


    @ToMany(referencedJoinProperty = "cobro")
    private List<CobdetBean> listaPartidas;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 2076005323)
    private transient CobrosBeanDao myDao;

    @Generated(hash = 1608426416)
    public CobrosBean(Long id, int cobro, String fecha, String hora, Long clienteId,
                      Long empleadoId, double importe, String estado, int temporal,
                      int sinc) {
        this.id = id;
        this.cobro = cobro;
        this.fecha = fecha;
        this.hora = hora;
        this.clienteId = clienteId;
        this.empleadoId = empleadoId;
        this.importe = importe;
        this.estado = estado;
        this.temporal = temporal;
        this.sinc = sinc;
    }

    @Generated(hash = 218966627)
    public CobrosBean() {
    }

    @Generated(hash = 1668724671)
    private transient Long cliente__resolvedKey;
    @Generated(hash = 1910491088)
    private transient Long empleado__resolvedKey;

    public void setListaPartidas(List<CobdetBean> listaPartidas) {
        this.listaPartidas = listaPartidas;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCobro() {
        return this.cobro;
    }

    public void setCobro(int cobro) {
        this.cobro = cobro;
    }

    public String getFecha() {
        return this.fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return this.hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Long getClienteId() {
        return this.clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getEmpleadoId() {
        return this.empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public double getImporte() {
        return this.importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTemporal() {
        return this.temporal;
    }

    public void setTemporal(int temporal) {
        this.temporal = temporal;
    }

    public int getSinc() {
        return this.sinc;
    }

    public void setSinc(int sinc) {
        this.sinc = sinc;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 686314593)
    public ClienteBean getCliente() {
        Long __key = this.clienteId;
        if (cliente__resolvedKey == null || !cliente__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClienteBeanDao targetDao = daoSession.getClienteBeanDao();
            ClienteBean clienteNew = targetDao.load(__key);
            synchronized (this) {
                cliente = clienteNew;
                cliente__resolvedKey = __key;
            }
        }
        return cliente;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1582975631)
    public void setCliente(ClienteBean cliente) {
        synchronized (this) {
            this.cliente = cliente;
            clienteId = cliente == null ? null : cliente.getId();
            cliente__resolvedKey = clienteId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1964694665)
    public EmpleadoBean getEmpleado() {
        Long __key = this.empleadoId;
        if (empleado__resolvedKey == null || !empleado__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EmpleadoBeanDao targetDao = daoSession.getEmpleadoBeanDao();
            EmpleadoBean empleadoNew = targetDao.load(__key);
            synchronized (this) {
                empleado = empleadoNew;
                empleado__resolvedKey = __key;
            }
        }
        return empleado;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1261922638)
    public void setEmpleado(EmpleadoBean empleado) {
        synchronized (this) {
            this.empleado = empleado;
            empleadoId = empleado == null ? null : empleado.getId();
            empleado__resolvedKey = empleadoId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 945730767)
    public List<CobdetBean> getListaPartidas() {
        if (listaPartidas == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CobdetBeanDao targetDao = daoSession.getCobdetBeanDao();
            List<CobdetBean> listaPartidasNew = targetDao
                    ._queryCobrosBean_ListaPartidas(id);
            synchronized (this) {
                if (listaPartidas == null) {
                    listaPartidas = listaPartidasNew;
                }
            }
        }
        return listaPartidas;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1732985482)
    public synchronized void resetListaPartidas() {
        listaPartidas = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 237796536)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCobrosBeanDao() : null;
    }


}
