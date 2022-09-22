package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.TaskBean
import com.app.syspoint.repository.database.bean.TaskBeanDao

class TaskDao: Dao("TaskBean") {
    fun getTask(date: String?): TaskBean? {
        val taskBeans = dao.queryBuilder()
            .where(TaskBeanDao.Properties.Date.eq(date))
            .list() as List<TaskBean>
        return if (taskBeans.size > 0) taskBeans[0] else null
    }
}