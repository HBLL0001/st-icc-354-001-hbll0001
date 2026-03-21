package com.example.cliente1.mensajeria;

import java.text.SimpleDateFormat;
import java.util.Date;
public class Respuesta {

    private String name;
    private DataWeather main;
    private Date sd;

    public Date getSd() {
        return sd;
    }

    public void setSd(Date sd) {
        this.sd = sd;
    }

    public DataWeather getMain() {
        return main;
    }

    public void setMain(DataWeather main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
