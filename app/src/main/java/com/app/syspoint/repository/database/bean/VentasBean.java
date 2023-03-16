package com.app.syspoint.repository.database.bean;


import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

@Entity(nameInDb = "ventas",
        indexes = {
                @Index(value = "venta")
        })
public class VentasBean extends Bean {

    @Id(autoincrement = true)
    private Long id;
    private int venta;
    private String tipo_doc;
    private String fecha;
    private String hora;
    private Long clienteId;
    @ToOne(joinProperty = "clienteId")
    private ClienteBean cliente;
    private Long empleadoId;
    @ToOne(joinProperty = "empleadoId")
    private EmpleadoBean empleado;
    private double importe;
    private double impuesto;
    private String datos;
    private String estado;
    private String corte;
    private int temporal = 0;
    private int sync = 0;
    private String latidud;
    private String longitud;
    private String ticket;
    private String tipo_venta;
    private String usuario_cancelo;
    private String cobranza;
    private String factudado;
    private int stockId;

    @ToMany(referencedJoinProperty = "venta")
    private List<PartidasBean> listaPartidas;
/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;
/** Used for active entity operations. */
@Generated(hash = 1593761064)
private transient VentasBeanDao myDao;

@Generated(hash = 340347542)
public VentasBean(Long id, int venta, String tipo_doc, String fecha, String hora,
        Long clienteId, Long empleadoId, double importe, double impuesto, String datos,
        String estado, String corte, int temporal, int sync, String latidud,
        String longitud, String ticket, String tipo_venta, String usuario_cancelo,
        String cobranza, String factudado, int stockId) {
    this.id = id;
    this.venta = venta;
    this.tipo_doc = tipo_doc;
    this.fecha = fecha;
    this.hora = hora;
    this.clienteId = clienteId;
    this.empleadoId = empleadoId;
    this.importe = importe;
    this.impuesto = impuesto;
    this.datos = datos;
    this.estado = estado;
    this.corte = corte;
    this.temporal = temporal;
    this.sync = sync;
    this.latidud = latidud;
    this.longitud = longitud;
    this.ticket = ticket;
    this.tipo_venta = tipo_venta;
    this.usuario_cancelo = usuario_cancelo;
    this.cobranza = cobranza;
    this.factudado = factudado;
    this.stockId = stockId;
}

@Generated(hash = 2050629208)
public VentasBean() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public int getVenta() {
    return this.venta;
}

public void setVenta(int venta) {
    this.venta = venta;
}

public String getTipo_doc() {
    return this.tipo_doc;
}

public void setTipo_doc(String tipo_doc) {
    this.tipo_doc = tipo_doc;
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

public double getImpuesto() {
    return this.impuesto;
}

public void setImpuesto(double impuesto) {
    this.impuesto = impuesto;
}

public String getDatos() {
    return this.datos;
}

public void setDatos(String datos) {
    this.datos = datos;
}

public String getEstado() {
    return this.estado;
}

public void setEstado(String estado) {
    this.estado = estado;
}

public String getCorte() {
    return this.corte;
}

public void setCorte(String corte) {
    this.corte = corte;
}

public int getTemporal() {
    return this.temporal;
}

public void setTemporal(int temporal) {
    this.temporal = temporal;
}

public int getSync() {
    return this.sync;
}

public void setSync(int sync) {
    this.sync = sync;
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

public String getTicket() {
    return this.ticket;
}

public void setTicket(String ticket) {
    this.ticket = ticket;
}

public String getTipo_venta() {
    return this.tipo_venta;
}

public void setTipo_venta(String tipo_venta) {
    this.tipo_venta = tipo_venta;
}

public String getUsuario_cancelo() {
    return this.usuario_cancelo;
}

public void setUsuario_cancelo(String usuario_cancelo) {
    this.usuario_cancelo = usuario_cancelo;
}

public String getCobranza() {
    return this.cobranza;
}

public void setCobranza(String cobranza) {
    this.cobranza = cobranza;
}

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getStockId() {
        return stockId;
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
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 1071159781)
public List<PartidasBean> getListaPartidas() {
    if (listaPartidas == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        PartidasBeanDao targetDao = daoSession.getPartidasBeanDao();
        List<PartidasBean> listaPartidasNew = targetDao
                ._queryVentasBean_ListaPartidas(id);
        synchronized (this) {
            if (listaPartidas == null) {
                listaPartidas = listaPartidasNew;
            }
        }
    }
    return listaPartidas;
}

/** Resets a to-many relationship, making the next get call to query for a fresh result. */
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

public String getFactudado() {
    return this.factudado;
}

public void setFactudado(String factudado) {
    this.factudado = factudado;
}

/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 1609789233)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getVentasBeanDao() : null;
}
   
}
