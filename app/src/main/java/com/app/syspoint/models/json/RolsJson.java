package com.app.syspoint.models.json;

import com.app.syspoint.models.Role;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RolsJson {

    @SerializedName("Roles")
    @Expose
    private List<Role> roles = null;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}