package ru.vaszol.https_app_test;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpCookie;

import ru.vaszol.https_app_test.http.Api;
import ru.vaszol.https_app_test.http.AuthenticationParameters;
import ru.vaszol.https_app_test.util.IOUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Api exampleApi;
    public static java.net.CookieManager msCookieManager = new java.net.CookieManager();
    String exampleUrl;

    public static String TAG = "MainActivity";
    private TextView responseText;

    //    @InjectResource(R.string.server_cert_asset_name)
    String caCertificateName;

    //    @InjectResource(R.string.client_cert_file_name)
    String clientCertificateName;

    //    @InjectResource(R.string.client_cert_password)
    String clientCertificatePassword;

    private String user;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        responseText = (TextView) findViewById(R.id.response_text);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.httpLoginPhone) {    // Handle the http request:
            exampleUrl = "http://myserver.ru/login.action?username=hello&password=world";
            Toast.makeText(getApplicationContext(), exampleUrl, Toast.LENGTH_LONG).show();
            doRequest();
        } else if (id == R.id.httpCamerasJson) {
//            exampleUrl = String.valueOf(R.string.getCameras_url);
            exampleUrl = "http://myserver.ru/json.action";
            Toast.makeText(getApplicationContext(), exampleUrl, Toast.LENGTH_LONG).show();
            doRequest();
        } else if (id == R.id.httpPing) {
            exampleUrl = "http://myserver.ru/ping.action";
            Toast.makeText(getApplicationContext(), exampleUrl, Toast.LENGTH_LONG).show();
            doRequest();
        } else if (id == R.id.httpLogout) {
            exampleUrl = "http://myserver.ru/logout";
            Toast.makeText(getApplicationContext(), exampleUrl, Toast.LENGTH_LONG).show();
            doRequest();
        } else if (id == R.id.httpsLoginPhone) {    // Handle the https request:
            exampleUrl = "https://myserver.ru/login.action?username=hello&password=world";
            Toast.makeText(getApplicationContext(), exampleUrl, Toast.LENGTH_LONG).show();
            doRequest();
        } else if (id == R.id.httpsCamerasJson) {
            exampleUrl = "https://myserver.ru/json.action";
            Toast.makeText(getApplicationContext(), exampleUrl, Toast.LENGTH_LONG).show();
            doRequest();
        } else if (id == R.id.httpsPing) {
            exampleUrl = "https://myserver.ru/ping.action";
            Toast.makeText(getApplicationContext(), exampleUrl, Toast.LENGTH_LONG).show();
            doRequest();
        } else if (id == R.id.httpsLogout) {
            exampleUrl = "https://myserver.ru/logout";
            Toast.makeText(getApplicationContext(), exampleUrl, Toast.LENGTH_LONG).show();
            doRequest();
        } else if (id == R.id.clear_text) {
            responseText.setText("");
        } else if (id == R.id.clear_cookie) {
            MainActivity.msCookieManager.getCookieStore().removeAll();
            updateOutput("Cookie: " + String.valueOf(MainActivity.msCookieManager.getCookieStore().getCookies()));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateOutput(String text) {
        responseText.setText(responseText.getText() + "\n\n" + text);
    }

    private void doRequest() {

        try {
            AuthenticationParameters authParams = new AuthenticationParameters();
//            authParams.setClientCertificate(getClientCertFile());
//            authParams.setClientCertificatePassword(clientCertificatePassword);
//            authParams.setCaCertificate(readCaCert());

            exampleApi = new Api(authParams);
            updateOutput("Connecting to " + exampleUrl);

            new AsyncTask() {
                @Override
                protected Object doInBackground(Object... objects) {

                    try {
                        String result = exampleApi.doGet(exampleUrl);
                        int responseCode = exampleApi.getLastResponseCode();
                        if (responseCode == 200) {
                            publishProgress("HTTP Response Code: " + responseCode + " result: " + result);
                        } else {
                            publishProgress("HTTP Response Code: " + responseCode);
                        }

                    } catch (Throwable ex) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintWriter writer = new PrintWriter(baos);
                        ex.printStackTrace(writer);
                        writer.flush();
                        writer.close();
                        publishProgress(ex.toString() + " : " + baos.toString());
                    }

                    return null;
                }

                @Override
                protected void onProgressUpdate(final Object... values) {
                    StringBuilder buf = new StringBuilder();
                    for (final Object value : values) {
                        buf.append(value.toString());
                    }
                    updateOutput(buf.toString());
                }

                @Override
                protected void onPostExecute(final Object result) {
                    updateOutput("Done!");
                    updateOutput("Cookie: " + String.valueOf(MainActivity.msCookieManager.getCookieStore().getCookies()));
                }
            }.execute();

        } catch (Exception ex) {
            Log.e(TAG, "failed to create timeApi", ex);
            updateOutput(ex.toString());
        }
    }

    private File getClientCertFile() {
        File externalStorageDir = Environment.getExternalStorageDirectory();
        return new File(externalStorageDir, clientCertificateName);
    }

    private String readCaCert() throws Exception {
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open(caCertificateName);
        return IOUtil.readFully(inputStream);
    }
}
