package com.ambulanceapp.client.models;

import java.util.Map;

import lombok.Data;

@Data
public class FirebaseRequestBody {
    private String collectionName;
    private String documentID;
    private String whereFromField;
    private Object whereValueField;

    private Map<String,Object> params;

    public FirebaseRequestBody() {
    }

    public FirebaseRequestBody(RequestBodyBuilder builder) {
        this.collectionName = builder.collectionName;
        this.documentID = builder.documentID;
        this.whereFromField = builder.whereFromField;
        this.whereValueField = builder.whereValueField;
        this.params = builder.params;
    }

    public static class RequestBodyBuilder {
        private String collectionName;
        private String documentID;
        private String whereFromField;
        private Object whereValueField;
        private Map<String,Object> params;

        public RequestBodyBuilder() {
        }

        public RequestBodyBuilder setCollectionName(String collectionName) {
            this.collectionName = collectionName;
            return this;
        }

        public RequestBodyBuilder setDocumentID(String documentID) {
            this.documentID = documentID;
            return this;
        }

        public RequestBodyBuilder setWhereFromField(String whereFromField) {
            this.whereFromField = whereFromField;
            return this;
        }

        public RequestBodyBuilder setWhereValueField(Object whereValueField) {
            this.whereValueField = whereValueField;
            return this;
        }

        public RequestBodyBuilder setParams(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public FirebaseRequestBody build() {
            return new FirebaseRequestBody(this);
        }
    }
}
