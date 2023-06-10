package com.ambulanceapp.client.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import lombok.Data;

@Data
public class Enforcers {
    private String documentID;
    private String userID;
    private String circle;

    public Enforcers() {
    }

    public Enforcers(EnforcerBuilder builder) {
        this.documentID = builder.documentID;
        this.userID = builder.userID;
        this.circle = builder.circle;
    }

    public static class EnforcerBuilder {
        private String documentID;
        private String userID;
        private String circle;

        public EnforcerBuilder() {
        }

        public EnforcerBuilder setDocumentID(String documentID) {
            this.documentID = documentID;
            return this;
        }

        public EnforcerBuilder setUserID(String userID) {
            this.userID = userID;
            return this;
        }

        public EnforcerBuilder setCircle(String circle) {
            this.circle = circle;
            return this;
        }

        public Enforcers build() {
            return new Enforcers(this);
        }
    }
}
