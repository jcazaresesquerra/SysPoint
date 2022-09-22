package com.app.syspoint.repository.database.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(nameInDb = "task", indexes = {
        @Index(value = "task")
})
public class TaskBean extends Bean{

    @Id(autoincrement = true)
    private Long id;
    private String date;
    private String task;
@Generated(hash = 545142213)
public TaskBean(Long id, String date, String task) {
    this.id = id;
    this.date = date;
    this.task = task;
}
@Generated(hash = 1443476586)
public TaskBean() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getDate() {
    return this.date;
}
public void setDate(String date) {
    this.date = date;
}
public String getTask() {
    return this.task;
}
public void setTask(String task) {
    this.task = task;
}


    


}
