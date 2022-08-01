package com.app.syspoint.db.dao;


import com.app.syspoint.db.bean.TaskBean;
import com.app.syspoint.db.bean.TaskBeanDao;

import java.util.List;

public class TaskDao extends Dao{

    public TaskDao() {
        super("TaskBean");
    }


    public final TaskBean getTask(String date){
        final List<TaskBean> taskBeans = dao.queryBuilder()
                .where(TaskBeanDao.Properties.Date.eq(date  ))
                .list();
        return taskBeans.size()> 0? taskBeans.get(0) : null;
    }

}
