package com.app.syspoint.repository.database.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(nameInDb = "sesion")
public class SesionBean extends Bean {


    @Id(autoincrement = true)
    private Long id;

    private boolean remember;
    private long empleadoId;
    
    @ToOne(joinProperty = "empleadoId")
    private EmpleadoBean empleado;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2025210862)
    private transient SesionBeanDao myDao;

    @Generated(hash = 496287141)
    public SesionBean(Long id, boolean remember, long empleadoId) {
        this.id = id;
        this.remember = remember;
        this.empleadoId = empleadoId;
    }

    @Generated(hash = 132673690)
    public SesionBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getRemember() {
        return this.remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    public long getEmpleadoId() {
        return this.empleadoId;
    }

    public void setEmpleadoId(long empleadoId) {
        this.empleadoId = empleadoId;
    }

    @Generated(hash = 1910491088)
    private transient Long empleado__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 313042010)
    public EmpleadoBean getEmpleado() {
        long __key = this.empleadoId;
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
    @Generated(hash = 64476237)
    public void setEmpleado(@NotNull EmpleadoBean empleado) {
        if (empleado == null) {
            throw new DaoException(
                    "To-one property 'empleadoId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.empleado = empleado;
            empleadoId = empleado.getId();
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
    @Generated(hash = 498821959)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSesionBeanDao() : null;
    }
}
