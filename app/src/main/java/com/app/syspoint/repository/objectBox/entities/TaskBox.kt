package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class TaskBox (
    @Id(assignable = true)
    var id: Long = 0,
    var date: String? = null,
    var task: String? = null,
)