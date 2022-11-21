package com.app.syspoint.repository.database.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;

@Entity(nameInDb = "partidas")
public class PartidasBean extends Bean {

    @Id(autoincrement = true    )
    private Long id;
    private Long venta;
    private long articuloId;
    @ToOne(joinProperty = "articuloId")
    private ProductoBean articulo;
    private int cantidad;
    private double precio;
    private double impuesto;
    private String descripcion;
    private String observ;
    private Date fecha;
    private String hora;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1422542357)
    private transient PartidasBeanDao myDao;
    @Generated(hash = 1011504966)
    public PartidasBean(Long id, Long venta, long articuloId, int cantidad, double precio,
            double impuesto, String descripcion, String observ, Date fecha, String hora) {
        this.id = id;
        this.venta = venta;
        this.articuloId = articuloId;
        this.cantidad = cantidad;
        this.precio = precio;
        this.impuesto = impuesto;
        this.descripcion = descripcion;
        this.observ = observ;
        this.fecha = fecha;
        this.hora = hora;
    }
    @Generated(hash = 1993345627)
    public PartidasBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getVenta() {
        return this.venta;
    }
    public void setVenta(Long venta) {
        this.venta = venta;
    }
    public long getArticuloId() {
        return this.articuloId;
    }
    public void setArticuloId(long articuloId) {
        this.articuloId = articuloId;
    }
    public int getCantidad() {
        return this.cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public double getPrecio() {
        return this.precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public double getImpuesto() {
        return this.impuesto;
    }
    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }
    public String getObserv() {
        return this.observ;
    }
    public void setObserv(String observ) {
        this.observ = observ;
    }
    public Date getFecha() {
        return this.fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public String getHora() {
        return this.hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }
    @Generated(hash = 344637210)
    private transient Long articulo__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2047299235)
    public ProductoBean getArticulo() {
        long __key = this.articuloId;
        if (articulo__resolvedKey == null || !articulo__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProductoBeanDao targetDao = daoSession.getProductoBeanDao();
            ProductoBean articuloNew = targetDao.load(__key);
            synchronized (this) {
                articulo = articuloNew;
                articulo__resolvedKey = __key;
            }
        }
        return articulo;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 983862737)
    public void setArticulo(@NotNull ProductoBean articulo) {
        if (articulo == null) {
            throw new DaoException(
                    "To-one property 'articuloId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.articulo = articulo;
            articuloId = articulo.getId();
            articulo__resolvedKey = articuloId;
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
    public String getDescripcion() {
        return this.descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1143940856)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPartidasBeanDao() : null;
    }
   
    
}

