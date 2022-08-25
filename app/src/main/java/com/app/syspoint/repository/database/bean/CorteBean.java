package com.app.syspoint.repository.database.bean;

import com.app.syspoint.db.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(nameInDb = "cortes")
public class CorteBean extends Bean{

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String nombre;

    private long clienteId;

    @ToOne(joinProperty = "clienteId")
    private ClienteBean clienteBean;

    private long productoId;

    @ToOne(joinProperty = "productoId")
    private ProductoBean productoBean;

    @NotNull
    private int cantidad;

    @NotNull
    private double precio;

    @NotNull
    private String descripcion;

    @NotNull
    private double impuesto;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 299982043)
    private transient CorteBeanDao myDao;

    @Generated(hash = 430953120)
    public CorteBean(Long id, @NotNull String nombre, long clienteId,
            long productoId, int cantidad, double precio,
            @NotNull String descripcion, double impuesto) {
        this.id = id;
        this.nombre = nombre;
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
        this.impuesto = impuesto;
    }

    @Generated(hash = 1838407668)
    public CorteBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getClienteId() {
        return this.clienteId;
    }

    public void setClienteId(long clienteId) {
        this.clienteId = clienteId;
    }

    public long getProductoId() {
        return this.productoId;
    }

    public void setProductoId(long productoId) {
        this.productoId = productoId;
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

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getImpuesto() {
        return this.impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    @Generated(hash = 1910965194)
    private transient Long clienteBean__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1782721090)
    public ClienteBean getClienteBean() {
        long __key = this.clienteId;
        if (clienteBean__resolvedKey == null
                || !clienteBean__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClienteBeanDao targetDao = daoSession.getClienteBeanDao();
            ClienteBean clienteBeanNew = targetDao.load(__key);
            synchronized (this) {
                clienteBean = clienteBeanNew;
                clienteBean__resolvedKey = __key;
            }
        }
        return clienteBean;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1336031696)
    public void setClienteBean(@NotNull ClienteBean clienteBean) {
        if (clienteBean == null) {
            throw new DaoException(
                    "To-one property 'clienteId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.clienteBean = clienteBean;
            clienteId = clienteBean.getId();
            clienteBean__resolvedKey = clienteId;
        }
    }

    @Generated(hash = 636877775)
    private transient Long productoBean__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1300948245)
    public ProductoBean getProductoBean() {
        long __key = this.productoId;
        if (productoBean__resolvedKey == null
                || !productoBean__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProductoBeanDao targetDao = daoSession.getProductoBeanDao();
            ProductoBean productoBeanNew = targetDao.load(__key);
            synchronized (this) {
                productoBean = productoBeanNew;
                productoBean__resolvedKey = __key;
            }
        }
        return productoBean;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1047805412)
    public void setProductoBean(@NotNull ProductoBean productoBean) {
        if (productoBean == null) {
            throw new DaoException(
                    "To-one property 'productoId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.productoBean = productoBean;
            productoId = productoBean.getId();
            productoBean__resolvedKey = productoId;
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
    @Generated(hash = 1328722584)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCorteBeanDao() : null;
    }

   
}
