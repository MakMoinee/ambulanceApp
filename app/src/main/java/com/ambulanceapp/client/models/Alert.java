package com.ambulanceapp.client.models;

import lombok.Data;

@Data
public class Alert {
    private Boolean isReceived;
    private String location;

    public Alert() {
    }

    public Alert(AlertBuilder builder) {
        this.isReceived = builder.isReceived;
        this.location = builder.location;
    }

    public static class AlertBuilder {
        private Boolean isReceived;
        private String location;

        public AlertBuilder() {
        }

        public AlertBuilder setReceived(Boolean received) {
            isReceived = received;
            return this;
        }

        public AlertBuilder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Alert build() {
            return new Alert(this);
        }


    }
}
