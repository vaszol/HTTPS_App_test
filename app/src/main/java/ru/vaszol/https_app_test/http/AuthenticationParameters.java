package ru.vaszol.https_app_test.http;

import java.io.File;

/**
 * Created by vaszol on 18.02.2016.
 */
public class AuthenticationParameters {
    private File clientCertificate = null;
    private String clientCertificatePassword = null;
    private String caCertificate = null;

    public File getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(File clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public String getClientCertificatePassword() {
        return clientCertificatePassword;
    }

    public void setClientCertificatePassword(String clientCertificatePassword) {
        this.clientCertificatePassword = clientCertificatePassword;
    }

    public String getCaCertificate() {
        return caCertificate;
    }

    public void setCaCertificate(String caCertificate) {
        this.caCertificate = caCertificate;
    }
}
