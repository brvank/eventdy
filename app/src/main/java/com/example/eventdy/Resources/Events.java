package com.example.eventdy.Resources;

public class Events {
    private String eventId, eventTitle, eventAbout, eventCategory, eventDate, eventEndingDate, eventLocation, eventExcerpt;

    /*
    this.eventId = eventId;
    this.eventName = eventName;
    this.eventDetails = eventDetails;
    this.eventCategory = eventCategory;
    this.eventDate = eventDate;
    this.eventEndingDate = eventEndingDate;
    this.eventLocation = eventLocation;
     */

    public Events() {
    }

    public Events(String eventTitle, String eventCategory, String eventId) {
        this.eventTitle = eventTitle;
        this.eventCategory = eventCategory;
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventAbout() {
        return eventAbout;
    }

    public void setEventAbout(String eventAbout) {
        this.eventAbout = eventAbout;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventEndingDate() {
        return eventEndingDate;
    }

    public void setEventEndingDate(String eventEndingDate) {
        this.eventEndingDate = eventEndingDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventExcerpt() {
        return eventExcerpt;
    }

    public void setEventExcerpt(String eventExcerpt) {
        this.eventExcerpt = eventExcerpt;
    }
}
