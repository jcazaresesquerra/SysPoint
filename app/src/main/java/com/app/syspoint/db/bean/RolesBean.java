package com.app.syspoint.db.bean;

import com.app.syspoint.db.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(nameInDb = "roles")
public class RolesBean extends Bean {


    @Id(autoincrement = true)
    private Long id;

    private Long empleadoId;
    @ToOne(joinProperty = "empleadoId")
    private EmpleadoBean empleado;

    private String modulo;

    private boolean active;
    
    private String identificador;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 525975980)
    private transient RolesBeanDao myDao;

    @Generated(hash = 539301010)
    public RolesBean(Long id, Long empleadoId, String modulo, boolean active,
            String identificador) {
        this.id = id;
        this.empleadoId = empleadoId;
        this.modulo = modulo;
        this.active = active;
        this.identificador = identificador;
    }

    @Generated(hash = 1038441555)
    public RolesBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpleadoId() {
        return this.empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getModulo() {
        return this.modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
    @Generated(hash = 118007675)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRolesBeanDao() : null;
    }

    public String getIdentificador() {
        return this.identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }




}
