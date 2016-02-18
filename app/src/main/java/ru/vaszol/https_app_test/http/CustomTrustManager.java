package ru.vaszol.https_app_test.http;

/**
 * Created by vaszol on 18.02.2016.
 */

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class CustomTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {}
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {}
    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        return null;
    }
}
