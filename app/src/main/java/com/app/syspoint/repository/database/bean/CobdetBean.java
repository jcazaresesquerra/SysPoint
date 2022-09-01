package com.app.syspoint.repository.database.bean;


import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(nameInDb = "cobdet")
public class CobdetBean extends Bean{

    @Id()
    private Long id;
    private Long cobro;
    private String cobranza;
    private Long clienteId;
    @ToOne(joinProperty = "clienteId")
    private ClienteBean cliente;
    private String fecha;
    private String hora;
    private double importe;
    private Integer venta;
    private Long empleadoId;
    @ToOne(joinProperty = "empleadoId")
    private EmpleadoBean empleado;
    private Integer abono;
    private double saldo;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 87045701)
    private transient CobdetBeanDao myDao;
    @Generated(hash = 186254058)
    public CobdetBean(Long id, Long cobro, String cobranza, Long clienteId,
            String fecha, String hora, double importe, Integer venta,
            Long empleadoId, Integer abono, double saldo) {
        this.id = id;
        this.cobro = cobro;
        this.cobranza = cobranza;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.hora = hora;
        this.importe = importe;
        this.venta = venta;
        this.empleadoId = empleadoId;
        this.abono = abono;
        this.saldo = saldo;
    }
    @Generated(hash = 2128886907)
    public CobdetBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getCobro() {
        return this.cobro;
    }
    public void setCobro(Long cobro) {
        this.cobro = cobro;
    }
    public String getCobranza() {
        return this.cobranza;
    }
    public void setCobranza(String cobranza) {
        this.cobranza = cobranza;
    }
    public Long getClienteId() {
        return this.clienteId;
    }
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
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
    public double getImporte() {
        return this.importe;
    }
    public void setImporte(double importe) {
        this.importe = importe;
    }
    public Integer getVenta() {
        return this.venta;
    }
    public void setVenta(Integer venta) {
        this.venta = venta;
    }
    public Long getEmpleadoId() {
        return this.empleadoId;
    }
    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }
    public Integer getAbono() {
        return this.abono;
    }
    public void setAbono(Integer abono) {
        this.abono = abono;
    }
    public double getSaldo() {
        return this.saldo;
    }
    public void setSaldo(double saldo) {
        this.saldo = saldo;
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
    @Generated(hash = 1323434186)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCobdetBeanDao() : null;
    }


}
