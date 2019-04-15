package com.redhat.cajun.navy.incident.priority.rules.model;

public class IncidentReportedEvent {

    private String incident;

    public IncidentReportedEvent(String incident) {
        this.incident = incident;
    }

    public String getIncident() {
        return incident;
    }

    public void setIncident(String incident) {
        this.incident = incident;
    }
}
