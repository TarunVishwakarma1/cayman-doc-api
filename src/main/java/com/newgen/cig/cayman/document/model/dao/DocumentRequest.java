package com.newgen.cig.cayman.document.model.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentRequest {

    @JsonProperty("NGOGetDocumentBDO")
    private NGOGetDocumentBDO ngoGetDocumentBDO;

    // Getter and Setter for ngoGetDocumentBDO
    public NGOGetDocumentBDO getNgoGetDocumentBDO() {
        return ngoGetDocumentBDO;
    }

    public void setNgoGetDocumentBDO(NGOGetDocumentBDO ngoGetDocumentBDO) {
        this.ngoGetDocumentBDO = ngoGetDocumentBDO;
    }

    // Inner class for NGOGetDocumentBDO
    public static class NGOGetDocumentBDO {

        private String cabinetName;
        private String docIndex;
        private String versionNo;
        private String userName;
        private String userPassword;
        private String userDBId;
        private String downloadLocation;
        private String authToken;
        private String authTokenType;
        private String locale;

        // Getters and Setters for each field
        public String getCabinetName() {
            return cabinetName;
        }

        public void setCabinetName(String cabinetName) {
            this.cabinetName = cabinetName;
        }

        public String getDocIndex() {
            return docIndex;
        }

        public void setDocIndex(String docIndex) {
            this.docIndex = docIndex;
        }

        public String getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(String versionNo) {
            this.versionNo = versionNo;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPassword() {
            return userPassword;
        }

        public void setUserPassword(String userPassword) {
            this.userPassword = userPassword;
        }

        public String getUserDBId() {
            return userDBId;
        }

        public void setUserDBId(String userDBId) {
            this.userDBId = userDBId;
        }

        public String getDownloadLocation() {
            return downloadLocation;
        }

        public void setDownloadLocation(String downloadLocation) {
            this.downloadLocation = downloadLocation;
        }

        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        public String getAuthTokenType() {
            return authTokenType;
        }

        public void setAuthTokenType(String authTokenType) {
            this.authTokenType = authTokenType;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        // Optional: Override toString() for better representation
        @Override
        public String toString() {
            return "NGOGetDocumentBDO{" +
                    "cabinetName='" + cabinetName + '\'' +
                    ", docIndex='" + docIndex + '\'' +
                    ", versionNo='" + versionNo + '\'' +
                    ", userName='" + userName + '\'' +
                    ", userPassword='" + userPassword + '\'' +
                    ", userDBId='" + userDBId + '\'' +
                    ", downloadLocation='" + downloadLocation + '\'' +
                    ", authToken='" + authToken + '\'' +
                    ", authTokenType='" + authTokenType + '\'' +
                    ", locale='" + locale + '\'' +
                    '}';
        }

        // Optional: Override equals() and hashCode() for better object comparison and hashing
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NGOGetDocumentBDO that = (NGOGetDocumentBDO) o;

            if (!cabinetName.equals(that.cabinetName)) return false;
            if (!docIndex.equals(that.docIndex)) return false;
            if (!versionNo.equals(that.versionNo)) return false;
            if (!userName.equals(that.userName)) return false;
            if (!userPassword.equals(that.userPassword)) return false;
            if (!userDBId.equals(that.userDBId)) return false;
            if (!downloadLocation.equals(that.downloadLocation)) return false;
            if (!authToken.equals(that.authToken)) return false;
            if (!authTokenType.equals(that.authTokenType)) return false;
            return locale.equals(that.locale);
        }

        @Override
        public int hashCode() {
            int result = cabinetName.hashCode();
            result = 31 * result + docIndex.hashCode();
            result = 31 * result + versionNo.hashCode();
            result = 31 * result + userName.hashCode();
            result = 31 * result + userPassword.hashCode();
            result = 31 * result + userDBId.hashCode();
            result = 31 * result + downloadLocation.hashCode();
            result = 31 * result + authToken.hashCode();
            result = 31 * result + authTokenType.hashCode();
            result = 31 * result + locale.hashCode();
            return result;
        }
    }

    // Optional: Override toString() for the main DocumentRequest class
    @Override
    public String toString() {
        return "DocumentRequest{" +
                "ngoGetDocumentBDO=" + ngoGetDocumentBDO +
                '}';
    }

    // Optional: Override equals() and hashCode() for the main DocumentRequest class
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentRequest that = (DocumentRequest) o;

        return ngoGetDocumentBDO.equals(that.ngoGetDocumentBDO);
    }

    @Override
    public int hashCode() {
        return ngoGetDocumentBDO.hashCode();
    }
}
