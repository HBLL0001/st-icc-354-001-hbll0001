package com.example.practicaspringboot.domain;

public enum ExpirationOption {
    ONE_HOUR("1 hora", 1),
    ONE_DAY("1 día", 24),
    ONE_WEEK("1 semana", 168),
    ONE_MONTH("1 mes", 720),
    ONE_YEAR("1 año", 8760);

    private final String label;
    private final int hours;

    ExpirationOption(String label, int hours) {
        this.label = label;
        this.hours = hours;
    }

    public String getLabel() { return label; }
    public int getHours() { return hours; }
}
