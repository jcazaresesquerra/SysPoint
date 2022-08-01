package com.app.syspoint;

public class TipoVisitaModel {

    private int id;
    private String name;
    private boolean isSelected;


    public TipoVisitaModel(int id, String name, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    public TipoVisitaModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "TipoVisitaModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
