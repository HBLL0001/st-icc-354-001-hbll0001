package com.example.practicaspringboot.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class MockHeader {

    private String headerKey;
    private String headerValue;

    public MockHeader() {}

    public MockHeader(String headerKey, String headerValue) {
        this.headerKey = headerKey;
        this.headerValue = headerValue;
    }

    public String getHeaderKey() { return headerKey; }
    public void setHeaderKey(String headerKey) { this.headerKey = headerKey; }
    public String getHeaderValue() { return headerValue; }
    public void setHeaderValue(String headerValue) { this.headerValue = headerValue; }
}
