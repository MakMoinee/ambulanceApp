package com.ambulanceapp.client.models;

import lombok.Data;

@Data
public class Feedback {
    private String documentID;
    private String userID;
    private String feedBack;

    public Feedback() {
    }

    public Feedback(FeedbackBuilder builder) {
        this.documentID = builder.documentID;
        this.userID = builder.userID;
        this.feedBack = builder.feedBack;
    }

    public static class FeedbackBuilder {
        private String documentID;
        private String userID;
        private String feedBack;

        public FeedbackBuilder() {
        }

        public FeedbackBuilder setDocumentID(String documentID) {
            this.documentID = documentID;
            return this;
        }

        public FeedbackBuilder setUserID(String userID) {
            this.userID = userID;
            return this;
        }

        public FeedbackBuilder setFeedBack(String feedBack) {
            this.feedBack = feedBack;
            return this;
        }

        public Feedback build() {
            return new Feedback(this);
        }
    }
}
