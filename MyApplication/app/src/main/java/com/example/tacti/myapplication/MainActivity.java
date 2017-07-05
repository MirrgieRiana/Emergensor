package com.example.tacti.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int SEND_COOLTIME = 1 * 1000;
    private static final int REQUEST_PERMISSION = 1;

    private TextView textView;
    private WebView webView;
    private LineChart lineChart;
    private LineData lineData;
    private LinkedList<Entry> entriesX = new LinkedList<>();
    private LinkedList<Entry> entriesY = new LinkedList<>();
    private LinkedList<Entry> entriesZ = new LinkedList<>();
    private LinkedList<Entry> entriesA = new LinkedList<>();
    private int i = 0;
    private Long timeLastUpload;
    private String text = "undefined";

    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values.length >= 3 ? event.values[0] : -30;
            float y = event.values.length >= 3 ? event.values[1] : -30;
            float z = event.values.length >= 3 ? event.values[2] : -30;
            float a = (float) Math.sqrt(x * x + y * y + z * z);

            {
                entriesX.set(i, new Entry(x, i));
                entriesY.set(i, new Entry(y, i));
                entriesZ.set(i, new Entry(z, i));
                entriesA.set(i, new Entry(a, i));

                lineChart.notifyDataSetChanged();
                lineChart.invalidate();

                i = (i + 1) % 50;
            }

            if (a > 10) {
                long now = System.currentTimeMillis();
                if (timeLastUpload == null || now - timeLastUpload >= SEND_COOLTIME) {
                    timeLastUpload = now;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Authenticator.setDefault(new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication("a", "gp-^45:w3v9]332c".toCharArray());
                                }
                            });

                            System.err.println("Start send");

                            try {
                                URL url = new URL("http://203.178.135.114:7030/send");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoOutput(true);
                                connection.setDoInput(true);
                                connection.setRequestMethod("POST");
                                try (PrintStream out = new PrintStream(connection.getOutputStream())) {
                                    out.print(text);
                                }
                                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                                    System.err.println(in.readLine());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    };

    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            text = "" + location.getLatitude() + " / " + location.getLongitude();
            updateTextView();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorOn();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationOn();

        textView = (TextView) findViewById(R.id.textView);
        updateTextView();

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://203.178.135.114:7030/");

        {
            lineChart = (LineChart) findViewById(R.id.lineChart);

            lineChart.getAxis(YAxis.AxisDependency.LEFT).setEnabled(false);
            lineChart.getAxis(YAxis.AxisDependency.LEFT).setStartAtZero(false);
            lineChart.getAxis(YAxis.AxisDependency.LEFT).setAxisMinValue(-50);
            lineChart.getAxis(YAxis.AxisDependency.LEFT).setAxisMaxValue(50);
            lineChart.getAxis(YAxis.AxisDependency.LEFT).setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            lineChart.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
            lineChart.getXAxis().setEnabled(false);
            lineChart.getLegend().setEnabled(false);
            lineChart.setVisibleXRangeMaximum(200);
            lineChart.setTouchEnabled(false);
            lineChart.setScaleEnabled(false);
            lineChart.setDragEnabled(false);
            lineChart.setDescription("");

            {
                lineData = new LineData();
                LineDataSet lineDataSet;

                lineDataSet = new LineDataSet(entriesX, "X");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xffff0000);
                lineData.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesY, "Y");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xff00ff00);
                lineData.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesZ, "Z");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xff0000ff);
                lineData.addDataSet(lineDataSet);

                lineDataSet = new LineDataSet(entriesA, "A");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(1);
                lineDataSet.setColor(0xff000000);
                lineData.addDataSet(lineDataSet);

                for (int i = 0; i < 50; i++) {
                    entriesX.addLast(new Entry(0, i));
                    entriesY.addLast(new Entry(0, i));
                    entriesZ.addLast(new Entry(0, i));
                    entriesA.addLast(new Entry(0, i));
                    lineData.addXValue("" + i);
                }
                lineChart.setData(lineData);
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sensorManager != null) sensorManager.unregisterListener(sensorEventListener);
        if (locationListener != null) locationManager.removeUpdates(locationListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) sensorOn();
        if (locationListener != null) locationOn();
    }

    private void sensorOn() {
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void locationOn() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
    }

    private void updateTextView() {
        textView.setText(text);
    }

}
