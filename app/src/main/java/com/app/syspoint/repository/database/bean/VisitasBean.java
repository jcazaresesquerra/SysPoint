package com.app.syspoint.repository.database.bean;

import com.app.syspoint.db.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(nameInDb = "visitas")
public class VisitasBean extends Bean{


    @Id(autoincrement = true)
    private Long id;
    private String fecha;
    private String hora;
    private Long clienteId;
    @ToOne(joinProperty = "clienteId")
    private ClienteBean cliente;
    private Long empleadoId;
    @ToOne(joinProperty = "empleadoId")
    private EmpleadoBean empleado;
    private String latidud;
    private String longitud;
    private String motivo_visita;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 452171996)
    private transient VisitasBeanDao myDao;
    @Generated(hash = 1406771150)
    public VisitasBean(Long id, String fecha, String hora, Long clienteId,
            Long empleadoId, String latidud, String longitud,
            String motivo_visita) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.clienteId = clienteId;
        this.empleadoId = empleadoId;
        this.latidud = latidud;
        this.longitud = longitud;
        this.motivo_visita = motivo_visita;
    }
    @Generated(hash = 1785176600)
    public VisitasBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public String getLatidud() {
        return this.latidud;
    }
    public void setLatidud(String latidud) {
        this.latidud = latidud;
    }
    public String getLongitud() {
        return this.longitud;
    }
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
    public String getMotivo_visita() {
        return this.motivo_visita;
    }
    public void setMotivo_visita(String motivo_visita) {
        this.motivo_visita = motivo_visita;
    }
    @Generated(hash = 1668724671)
    private transient Long cliente__resolvedKey;
    /** To-one relationship, resolved on first access. */
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
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1582975631)
    public void setCliente(ClienteBean cliente) {
        synchronized (this) {
            this.cliente = cliente;
            clienteId = cliente == null ? null : cliente.getId();
            cliente__resolvedKey = clienteId;
        }
    }
    @Generated(hash = 1910491088)
    private transient Long empleado__resolvedKey;
    /** To-one relationship, resolved on first access. */
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
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1261922638)
    public void setEmpleado(EmpleadoBean empleado) {
        synchronized (this) {
            this.empleado = empleado;
            empleadoId = empleado == null ? null : empleado.getId();
            empleado__resolvedKey = empleadoId;
        }
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
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1942838695)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getVisitasBeanDao() : null;
    }

}
