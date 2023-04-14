package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.TaskBox
import com.app.syspoint.repository.objectBox.entities.TaskBox_
import io.objectbox.query.QueryBuilder

class TaskDao: AbstractDao<TaskBox>() {

    fun clear() {
        abstractBox<TaskBox>().removeAll()
    }

    fun getTask(date: String?): TaskBox? {
        val query = abstractBox<TaskBox>().query()
            .equal(TaskBox_.date, date, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isNotEmpty()) results[0] else null
    }
}