package com.example.priyam.speedtest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.example.priyam.speedtest.HistoryDB.HistoryDBHelper;
import com.example.priyam.speedtest.HistoryDB.Models.HistoryData;
import com.example.priyam.speedtest.HistoryDB.Tables.HistoryTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {

    TextView tvDown, tvUp, tvType, tvCon;
    long startTime;
    long endTime;
    long fileSize;

    private int POOR_BANDWIDTH = 10;
    private int AVERAGE_BANDWIDTH = 200;
    private int GOOD_BANDWIDTH = 500;

    public final String TAG = "YO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SQLiteDatabase historyDb = new HistoryDBHelper(this).getWritableDatabase();

        tvDown = findViewById(R.id.tvDown);
        tvUp = findViewById(R.id.tvUp);
        tvType = findViewById(R.id.tvType);
        tvCon = findViewById(R.id.tvCon);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("No Internet Connection");
            alertDialogBuilder.setMessage("Test failed to complete. Check your internet connection and try again.");

            alertDialogBuilder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    if (info != null) {
                        startActivity(new Intent(TestActivity.this, MainActivity.class));
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TestActivity.this);
                        alertDialogBuilder.setTitle("No Internet Connection");
                        alertDialogBuilder.setMessage("Test failed to complete. Check your internet connection and try again.");
                        alertDialogBuilder.setNeutralButton("QUIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        alertDialogBuilder.show();
                    }
                }
            });

            alertDialogBuilder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            alertDialogBuilder.show();

        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            tvType.setText("WIFI");
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS) {
                tvType.setText("GPRS");
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                tvType.setText("EDGE");
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE) {
                tvType.setText("LTE");
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GSM) {
                tvType.setText("GSM");
            }
        }

        final long BeforeTime = System.currentTimeMillis();
        final long TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
        final long TotalRxBeforeTest = TrafficStats.getTotalRxBytes();

        Log.d(TAG, "Before Traffic Stats TX:" + TotalTxBeforeTest + "Traffic Stats TX:" + TotalRxBeforeTest);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://www.effigis.com/wp-content/uploads/2015/02/DigitalGlobe_WorldView1_50cm_8bit_BW_DRA_Bangkok_Thailand_2009JAN06_8bits_sub_r_1.jpg")
                .build();

        startTime = System.currentTimeMillis();

        //http://www.effigis.com/wp-content/uploads/2015/02/DigitalGlobe_WorldView1_50cm_8bit_BW_DRA_Bangkok_Thailand_2009JAN06_8bits_sub_r_1.jpg


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                InputStream input = response.body().byteStream();

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];

                    while (input.read(buffer) != -1) {
                        bos.write(buffer);
                    }
                    byte[] docBuffer = bos.toByteArray();
                    fileSize = bos.size();

                } finally {
                    input.close();
                }

                endTime = System.currentTimeMillis();

                // calculate how long it took by subtracting endtime from starttime

                double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                double timeTakenSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                final int kilobytePerSec = (int) Math.round(1024 / timeTakenSecs);
                final String Conn;

                if (kilobytePerSec <= POOR_BANDWIDTH) {
                    Conn = "Poor Network";
                } else if (kilobytePerSec >= GOOD_BANDWIDTH) {
                    Conn = "Very Good Network";
                } else {
                    Conn = "Good Network";
                }

                // get the download speed by dividing the file size by time taken to download
                final double speed = fileSize / timeTakenMills;

                Log.d(TAG, "Time taken in secs: " + timeTakenSecs);
                Log.d(TAG, "kilobyte per sec: " + kilobytePerSec);
                Log.d(TAG, "Download Speed: " + speed);
                Log.d(TAG, "File size: " + fileSize);
                //---------------------------------------------------------------------//

                long TotalTxAfterTest = TrafficStats.getTotalTxBytes();
                long TotalRxAfterTest = TrafficStats.getTotalRxBytes();
                long AfterTime = System.currentTimeMillis();

                Log.d(TAG, "After Traffic Stats TX:" + TotalTxAfterTest + " Traffic Stats TX:" + TotalRxAfterTest);

                double TimeDifference = AfterTime - BeforeTime;

                double rxDiff = TotalRxAfterTest - TotalRxBeforeTest;
                double txDiff = TotalTxAfterTest - TotalTxBeforeTest;

                double txBPS = 0;
                double rxBPS = 0;

                if ((txDiff != 0)) {
                    rxBPS = (rxDiff / (TimeDifference / 1000));
                    txBPS = (txDiff / (TimeDifference / 1000));
                } else {
                }

                final double finalRxBPS = rxBPS / 1000;
                final double finalTxBPS = txBPS / 1000;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDown.setText(String.format("%.2f", finalRxBPS) + "kBps");
                        tvUp.setText(String.format("%.2f", finalTxBPS) + "kBps");
                        tvCon.setText(Conn);

                        Date curDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                        String Date = dateFormat.format(curDate);

                        HistoryActivity.history = HistoryTable.getHistory(historyDb);

                        HistoryTable.insertHistory(
                                historyDb,
                                new HistoryData(
                                        (String.format("%.2f", finalRxBPS)) + "kBps",
                                        (String.format("%.2f", finalTxBPS)) + "kBps",
                                        Date,
                                        tvType.getText().toString()
                                )
                        );


                        HistoryActivity.history = HistoryTable.getHistory(historyDb);
                    }
                });
            }
        });
    }
}