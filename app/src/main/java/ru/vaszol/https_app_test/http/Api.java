package ru.vaszol.https_app_test.http;

/**
 * Created by vaszol on 18.02.2016.
 */

import android.text.TextUtils;
import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import ru.vaszol.https_app_test.MainActivity;
import ru.vaszol.https_app_test.util.IOUtil;

public class Api {
    private SSLContext sslContext;
    private int lastResponseCode;
    static final String COOKIES_HEADER = "Set-Cookie";
//    static java.net.CookieManager msCookieManager = new java.net.CookieManager();

    public int getLastResponseCode() {
        return lastResponseCode;
    }

    public Api(AuthenticationParameters authParams) throws Exception {

        File clientCertFile = authParams.getClientCertificate();
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new CustomTrustManager()}, new SecureRandom());

        CookieHandler.setDefault(new CookieManager());
    }


    public String doGet(String url) throws Exception {
        String result = null;

        HttpURLConnection urlConnection = null;

        try {
            URL requestedUrl = new URL(url);
            urlConnection = (HttpURLConnection) requestedUrl.openConnection();
            //Get Cookies form cookieManager and load them to connection:
            if (MainActivity.msCookieManager.getCookieStore().getCookies().size() > 0) {
                //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                urlConnection.setRequestProperty("Cookie",
                        TextUtils.join(";", MainActivity.msCookieManager.getCookieStore().getCookies()));
            }

            if (urlConnection instanceof HttpsURLConnection) {
                Log.d(MainActivity.TAG, "connection instanceof https");
                HostnameVerifier TRUST_ALL_CERTIFICATES = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                ((HttpsURLConnection) urlConnection).setDefaultHostnameVerifier(TRUST_ALL_CERTIFICATES);
                ((HttpsURLConnection) urlConnection).setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            } else {
                Log.d(MainActivity.TAG, "connection not https");
            }

            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(1500);
            urlConnection.setReadTimeout(1500);

            lastResponseCode = urlConnection.getResponseCode();
            result = IOUtil.readFully(urlConnection.getInputStream());

            //Get Cookies form response header and load them to cookieManager:
            Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    MainActivity.msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }
        } catch (Exception ex) {
            result = ex.toString();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }
}
