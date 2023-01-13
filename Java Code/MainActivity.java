package com.example.interview_solution;

import static java.lang.Float.*;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.interview_solution.databinding.ActivityMainBinding;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private  int filesRead = 0 ;
    public float[][] sample = new float[1200][2];
    public float[][] reference1 = new float[1200][2];
    public float[][] reference2 = new float[1200][2];
    public float[][] reference3 = new float[1200][2];
    public float[][] reference4 = new float[1200][2];
    public float[][] reference5 = new float[1200][2];
    public float[][] reference6 = new float[1200][2];

    public float[][] repairedRefData = new float[1200][2];

    public int sampleLength = 0 ;
    public int refLength1 = 0 ;
    public int refLength2 = 0 ;
    public int refLength3 = 0 ;
    public int refLength4 = 0 ;
    public int refLength5 = 0 ;
    public int refLength6 = 0 ;

    public float matchPrcnt1 = 0 ;
    public float matchPrcnt2 = 0 ;
    public float matchPrcnt3 = 0 ;
    public float matchPrcnt4 = 0 ;
    public float matchPrcnt5 = 0 ;
    public float matchPrcnt6 = 0 ;


    private TextView analysisSummary;
    private Button StartAnalysis ;

    LineGraphSeries<DataPoint> series ;
    LineGraphSeries<DataPoint> seriesSamp ;
    LineGraphSeries<DataPoint> seriesRef1 ;
    LineGraphSeries<DataPoint> seriesRef2 ;
    LineGraphSeries<DataPoint> seriesRef3 ;
    LineGraphSeries<DataPoint> seriesRef4 ;
    LineGraphSeries<DataPoint> seriesRef5 ;

    GraphView graph ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        analysisSummary = (TextView) findViewById(R.id.analysisText);
        analysisSummary.setMovementMethod(new ScrollingMovementMethod());
        analysisSummary.setScrollBarFadeDuration(0);
        analysisSummary.scrollTo(0,analysisSummary.getBottom());

        StartAnalysis = (Button) findViewById(R.id.Compare_Samples);

        graph = (GraphView) findViewById(R.id.graph) ;

//
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(700);
        graph.getViewport().setMaxX(1900);
        graph.getLegendRenderer().setVisible(true);
        graph.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);  // activate horizontal zooming and scrolling
        graph.getViewport().setScrollableY(true);
        graph.getLegendRenderer().setFixedPosition(1   ,1);

//
//        for (int i = 0 ; i < 1051 ; i++){
//            x = x + 1 ;
//            series.appendData(new DataPoint(x,y), true , 1051);
//        }
//        graph.addSeries(series);


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        readSampleFromGithub();
        readReference1FromGithub();
        readReference2FromGithub();
        readReference3FromGithub();
        readReference4FromGithub();
        readReference5FromGithub();
        readReference6FromGithub();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void readSampleFromGithub(){
        new Thread() {

            private ArrayList<String> records =new ArrayList<String>(); //to read each line
            private String[] splitStringArr = new String[2];
            private String str = "";
            private int linesCounter = 0 ;
            @Override
            public void run() {

                String path ="https://raw.githubusercontent.com/ameyaphatak/interview_data/main/sample.csv";
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");

                    if(c instanceof HttpURLConnection) {
                        ((HttpURLConnection)c).setRequestMethod("GET");
                    }
                    c.connect();
//                    c.setConnectTimeout(60000); // timing out in a minute - doesn't seem to do anything.
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    while ((str = in.readLine()) != null) {
                       splitStringArr = str.split(",");
                        for (int i = 0 ; i < 2 ; i++){
                            sample[linesCounter][i] = parseFloat(splitStringArr[i]) ;
                        }
                        linesCounter++ ;
                        records.add(str);
                    }
                    sampleLength = records.size();

                    filesRead++ ;
                    in.close();

                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seriesSamp = new LineGraphSeries<DataPoint>() ;
                            seriesSamp.setColor(Color.BLUE);
                            double x , y ;
                            for (int i = 0 ; i < sampleLength ; i++){
                                x = sample[i][0] ;
                                y = sample[i][1] ;
                                seriesSamp.appendData(new DataPoint(x,y), true , 1051);
                            }
                            graph.addSeries(seriesSamp);
                            seriesSamp.setTitle("SAMPLE");
                            graph.getLegendRenderer().resetStyles();


//                            Toast.makeText(MainActivity.this, String.valueOf(sample[2][2]) , Toast.LENGTH_LONG).show();
                            analysisSummary.append(System.getProperty("line.separator"));
                            analysisSummary.append("sample = " + String.valueOf(records.size()) );
                            if (filesRead >= 7) {
                                StartAnalysis.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void readReference1FromGithub(){
        new Thread() {

            //TextView t; //to show the result, please declare and find it inside onCreate()
            private ArrayList<String> records =new ArrayList<String>(); //to read each line
            private String[] splitStringArr = new String[2];
            private String str = "";
            private int linesCounter = 0 ;
            @Override
            public void run() {

                String path ="https://raw.githubusercontent.com/ameyaphatak/interview_data/main/reference1.csv";
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");

                    if(c instanceof HttpURLConnection) {
                        ((HttpURLConnection)c).setRequestMethod("GET");
                    }
                    c.connect();
//                    c.setConnectTimeout(60000); // timing out in a minute - doesn't seem to do anything.

                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    while ((str = in.readLine()) != null) {
                        splitStringArr = str.split(",");
                        for (int i = 0 ; i < 2 ; i++){
                            reference1[linesCounter][i] = parseFloat(splitStringArr[i]) ;
                        }
                        linesCounter++ ;
                        records.add(str);
                    }
                    filesRead++;
                    refLength1 = records.size();
                    in.close();

                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            seriesRef1 = new LineGraphSeries<DataPoint>() ;
                            seriesRef1.setColor(Color.YELLOW);
                            seriesRef1.setTitle("REF 1");
                            double x , y ;
                            for (int i = 0 ; i < refLength1 ; i++){
                                x = reference1[i][0] ;
                                y = reference1[i][1] ;
                                seriesRef1.appendData(new DataPoint(x,y), true , 1051);
                            }
                            graph.addSeries(seriesRef1);
                            graph.getLegendRenderer().resetStyles();

                            analysisSummary.append(System.getProperty("line.separator"));
                            analysisSummary.append("Reference 1 = " + String.valueOf(refLength1) );
                            if (filesRead >= 7) {

                                StartAnalysis.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void readReference2FromGithub(){
        new Thread() {
            private ArrayList<String> records =new ArrayList<String>(); //to read each line
            private String[] splitStringArr = new String[2];
            private String str = "";
            private int linesCounter = 0 ;
            //TextView t; //to show the result, please declare and find it inside onCreate()

            @Override
            public void run() {

                String path ="https://raw.githubusercontent.com/ameyaphatak/interview_data/main/reference2.csv";
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");

                    if(c instanceof HttpURLConnection) {
                        ((HttpURLConnection)c).setRequestMethod("GET");
                    }
                    c.connect();
//                    c.setConnectTimeout(60000); // timing out in a minute - doesn't seem to do anything.

                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    while ((str = in.readLine()) != null) {

                        splitStringArr = str.split(",");
                        for (int i = 0 ; i < 2 ; i++){
                            reference2[linesCounter][i] = parseFloat(splitStringArr[i]) ;
                        }
                        linesCounter++ ;
                        records.add(str);
                    }
                    filesRead++;
                    refLength2 = records.size();
                    in.close();

                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            seriesRef2 = new LineGraphSeries<DataPoint>() ;
                            seriesRef2.setColor(Color.GREEN);

                            double x , y ;
                            for (int i = 0 ; i < refLength2 ; i++){
                                x = reference2[i][0] ;
                                y = reference2[i][1] ;
                                seriesRef2.appendData(new DataPoint(x,y), true , 1051);
                            }
                            graph.addSeries(seriesRef2);
                            seriesRef2.setTitle("REF 2");
                            graph.getLegendRenderer().resetStyles();

                            analysisSummary.append(System.getProperty("line.separator"));
                            analysisSummary.append("Reference 2 = " + String.valueOf(refLength2) );
                            if (filesRead >= 7) {
                                StartAnalysis.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void readReference3FromGithub(){
        new Thread() {
            private ArrayList<String> records =new ArrayList<String>(); //to read each line
            private String[] splitStringArr = new String[2];
            private String str = "";
            private int linesCounter = 0 ;
            //TextView t; //to show the result, please declare and find it inside onCreate()

            @Override
            public void run() {

                String path ="https://raw.githubusercontent.com/ameyaphatak/interview_data/main/reference3.csv";
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");

                    if(c instanceof HttpURLConnection) {
                        ((HttpURLConnection)c).setRequestMethod("GET");
                    }
                    c.connect();
//                    c.setConnectTimeout(60000); // timing out in a minute - doesn't seem to do anything.

                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    while ((str = in.readLine()) != null) {

                        splitStringArr = str.split(",");
                        for (int i = 0 ; i < 2 ; i++){
                            reference3[linesCounter][i] = parseFloat(splitStringArr[i]) ;
                        }
                        linesCounter++ ;
                        records.add(str);
                    }
                    filesRead++;
                    refLength3 = records.size();
                    in.close();

                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            seriesRef3 = new LineGraphSeries<DataPoint>() ;
                            seriesRef3.setColor(Color.RED);

                            double x , y ;
                            for (int i = 0 ; i < refLength3 ; i++){
                                x = reference3[i][0] ;
                                y = reference3[i][1] ;
                                seriesRef3.appendData(new DataPoint(x,y), true , 1051);
                            }
                            graph.addSeries(seriesRef3);
                            seriesRef3.setTitle("REF 3");
                            graph.getLegendRenderer().resetStyles();

                            analysisSummary.append(System.getProperty("line.separator"));
                            analysisSummary.append("Reference 3 = " + String.valueOf(refLength3) );
                            if (filesRead >= 7) {
                                StartAnalysis.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void readReference4FromGithub(){
        new Thread() {
            private ArrayList<String> records =new ArrayList<String>(); //to read each line
            private String[] splitStringArr = new String[2];
            private String str = "";
            private int linesCounter = 0 ;
            //TextView t; //to show the result, please declare and find it inside onCreate()

            @Override
            public void run() {

                String path ="https://raw.githubusercontent.com/ameyaphatak/interview_data/main/reference4.csv";
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");

                    if(c instanceof HttpURLConnection) {
                        ((HttpURLConnection)c).setRequestMethod("GET");
                    }
                    c.connect();
//                    c.setConnectTimeout(60000); // timing out in a minute - doesn't seem to do anything.

                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    while ((str = in.readLine()) != null) {

                        splitStringArr = str.split(",");
                        for (int i = 0 ; i < 2 ; i++){
                            reference4[linesCounter][i] = parseFloat(splitStringArr[i]) ;
                        }
                        linesCounter++ ;
                        records.add(str);
                    }
                    filesRead++;
                    refLength4 = records.size();
                    in.close();

                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            seriesRef4 = new LineGraphSeries<DataPoint>() ;
                            seriesRef4.setColor(Color.MAGENTA);

                            double x , y ;
                            for (int i = 0 ; i < refLength4 ; i++){
                                x = reference4[i][0] ;
                                y = reference4[i][1] ;
                                seriesRef4.appendData(new DataPoint(x,y), true , 1051);
                            }
                            graph.addSeries(seriesRef4);
                            seriesRef4.setTitle("REF 4");
                            graph.getLegendRenderer().resetStyles();


                            analysisSummary.append(System.getProperty("line.separator"));
                            analysisSummary.append("Reference 4 = " + String.valueOf(refLength4) );
                            if (filesRead >= 7) {
                                StartAnalysis.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void readReference5FromGithub(){
        new Thread() {
            private ArrayList<String> records =new ArrayList<String>(); //to read each line
            private String[] splitStringArr = new String[2];
            private String str = "";
            private int linesCounter = 0 ;
            //TextView t; //to show the result, please declare and find it inside onCreate()

            @Override
            public void run() {

                String path ="https://raw.githubusercontent.com/ameyaphatak/interview_data/main/reference5.csv";
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");

                    if(c instanceof HttpURLConnection) {
                        ((HttpURLConnection)c).setRequestMethod("GET");
                    }
                    c.connect();
//                    c.setConnectTimeout(60000); // timing out in a minute - doesn't seem to do anything.

                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    while ((str = in.readLine()) != null) {

                        splitStringArr = str.split(",");
                        for (int i = 0 ; i < 2 ; i++){
                            reference5[linesCounter][i] = parseFloat(splitStringArr[i]) ;
                        }
                        linesCounter++ ;
                        records.add(str);
                    }
                    filesRead++;
                    refLength5 = records.size();
                    in.close();

                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            seriesRef5 = new LineGraphSeries<DataPoint>() ;
                            seriesRef5.setColor(Color.CYAN);

                            double x , y ;
                            for (int i = 0 ; i < refLength5 ; i++){
                                x = reference5[i][0] ;
                                y = reference5[i][1] ;
                                seriesRef5.appendData(new DataPoint(x,y), true , 1051);
                            }
                            graph.addSeries(seriesRef5);
                            seriesRef5.setTitle("REF 5");
                            graph.getLegendRenderer().resetStyles();


                            analysisSummary.append(System.getProperty("line.separator"));
                            analysisSummary.append("Reference 5 = " + String.valueOf(refLength5) );
                            if (filesRead >= 7) {
                                StartAnalysis.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void readReference6FromGithub(){
        new Thread() {
            private ArrayList<String> records =new ArrayList<String>(); //to read each line
            private String[] splitStringArr = new String[2];
            private String str = "";
            private int linesCounter = 0 ;
            //TextView t; //to show the result, please declare and find it inside onCreate()

            @Override
            public void run() {

                String path ="https://raw.githubusercontent.com/ameyaphatak/interview_data/main/reference6.csv";
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");

                    if(c instanceof HttpURLConnection) {
                        ((HttpURLConnection)c).setRequestMethod("GET");
                    }
                    c.connect();
//                    c.setConnectTimeout(60000); // timing out in a minute - doesn't seem to do anything.

                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    while ((str = in.readLine()) != null) {

                        splitStringArr = str.split(",");
                        for (int i = 0 ; i < 2 ; i++){
                            reference6[linesCounter][i] = parseFloat(splitStringArr[i]) ;
                        }
                        linesCounter++ ;
                        records.add(str);
                    }
                    filesRead++;
                    refLength6 = records.size();
                    in.close();

                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            analysisSummary.append(System.getProperty("line.separator"));
                            analysisSummary.append("Reference 6 = " + String.valueOf(refLength6) );
                            if (filesRead >= 7) {
                                StartAnalysis.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public float compare2EqualLengthArrays(float[][] Arr1, float[][] Arr2){
        int match = 0 ;
        int dn_match = 0 ;
        int sum = 0 ;
        for (int i = 0 ; i < sampleLength ; i++){

                if ((Arr2[i][1] > (2.5 * Arr1[i][1])) || ((Arr2[i][1] < (0.5 * Arr1[i][1])))) {
                    dn_match++;
                } else {
                    match++;
                }
        }
        sum = match + dn_match ;
        float percent_Change = 100 * (float)  match/ (float) sum   ;
//        Toast.makeText(MainActivity.this, String.valueOf(sum) + " Total, " + String.valueOf(match) + " points match " + String.valueOf(dn_match) + " points do not match, " +  String.valueOf(percent_Change) + " %", Toast.LENGTH_SHORT).show();
        analysisSummary.append(String.valueOf(sum) + " Total, " + String.valueOf(match) + " points match " + String.valueOf(dn_match) + " points do not match, " +  String.valueOf(percent_Change) + " %");
        return percent_Change ;
    }

        public void checkForMatchingBreakpoints(View view) {

            boolean badCtrlPointFound = false ;

            for (int i = 0 ; i < sampleLength ; i++){
                if (sample[i][0] != reference1[i][0]){
                    badCtrlPointFound = true ;
                }

            }
            for (int i = 0 ; i < sampleLength ; i++){
                if (sample[i][0] != reference2[i][0]){
                    badCtrlPointFound = true ;
                }

            }
            for (int i = 0 ; i < sampleLength ; i++){
                if (sample[i][0] != reference3[i][0]){
                    badCtrlPointFound = true ;
                }

            }
            for (int i = 0 ; i < sampleLength ; i++){
                if (sample[i][0] != reference4[i][0]){
                    badCtrlPointFound = true ;
                }

            }

            if (badCtrlPointFound) {
                Toast.makeText(MainActivity.this, "unmatched breakpoint at ", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(MainActivity.this, "data looks good ", Toast.LENGTH_SHORT).show();
                analysisSummary.append("\n data looks good. \n");
            }

        }
    public void reArrangeReferenceData(float[][] ArrIncoming ) {

        int i = 0 ;
        int workingIndex = 0 ;
        int workingValue = 800 ;
        if (ArrIncoming[0][0] < sample[0][0] ) {
            do {
                i++;
            } while (ArrIncoming[i][0] < sample[0][0]);
            workingIndex = i ;
        } else {
            // add code for data starting higher that 800 - does not exist right now.
        }
        for (int j = 0 ; workingValue < 1851 ; j++ ) {
//
            float p = (float) (workingValue - ArrIncoming[workingIndex - 1 + j ][0]) / (ArrIncoming[workingIndex + j ][0] - ArrIncoming[workingIndex - 1 + j  ][0]);
            float result = (float) (ArrIncoming[workingIndex -1 + j ][1] + (p * (ArrIncoming[workingIndex  + j  ][1] - ArrIncoming[workingIndex + j - 1][1])));
            repairedRefData[workingValue - 800][0] = workingValue;
            repairedRefData[workingValue - 800][1] = result;
            workingValue++;


        if (workingValue < ArrIncoming[workingIndex + j][0]) {
//            Toast.makeText(MainActivity.this, " impossible ", Toast.LENGTH_SHORT).show();
        do {
            p = (float) (workingValue - ArrIncoming[workingIndex - 1 + j ][0]) / (ArrIncoming[workingIndex + j ][0] - ArrIncoming[workingIndex - 1 + j  ][0]);
            result = (float) (ArrIncoming[workingIndex -1 + j ][1] + (p * (ArrIncoming[workingIndex  + j  ][1] - ArrIncoming[workingIndex + j - 1][1])));
            repairedRefData[workingValue - 800][0] = workingValue;
            repairedRefData[workingValue - 800][1] = result;
            workingValue++;
        } while (workingValue < ArrIncoming[workingIndex + j][0]) ;

        }

    }


    }
    public void buttonClicked(View view) {
//        String rndlines = String.valueOf(sample[2][1]) + "\n" + String.valueOf(reference1[2][1]) + "\n" +  String.valueOf(reference2[2][1]) + "\n" +  String.valueOf(reference3[2][1]) + "\n" +  reference4[2][1] + "\n" +  String.valueOf(reference5[2][1]) + "\n" +  String.valueOf(reference6[2][1]) ;
//        Toast.makeText(MainActivity.this, rndlines, Toast.LENGTH_SHORT).show();

        analysisSummary.append("\n checking reference 1 \n");
        matchPrcnt1 = compare2EqualLengthArrays(sample, reference1);
        analysisSummary.append("\n checking reference 2 \n");
        matchPrcnt2 = compare2EqualLengthArrays(sample, reference2);
        analysisSummary.append("\n checking reference 3 \n");
        matchPrcnt3 = compare2EqualLengthArrays(sample, reference3);
        analysisSummary.append("\n checking reference 4 \n");
        matchPrcnt4 = compare2EqualLengthArrays(sample, reference4);


    }
    public void button2Clicked(View view) {
//        String rndlines = String.valueOf(sample[2][1]) + "\n" + String.valueOf(reference1[2][1]) + "\n" +  String.valueOf(reference2[2][1]) + "\n" +  String.valueOf(reference3[2][1]) + "\n" +  reference4[2][1] + "\n" +  String.valueOf(reference5[2][1]) + "\n" +  String.valueOf(reference6[2][1]) ;
//        Toast.makeText(MainActivity.this, rndlines, Toast.LENGTH_SHORT).show();
//        analysisSummary.append(rndlines);

        analysisSummary.append("\n checking reference 5 \n");
        reArrangeReferenceData(reference5);
        matchPrcnt5 = compare2EqualLengthArrays(sample, repairedRefData);
    }
    public void button3Clicked(View view) {
//        String rndlines = String.valueOf(sample[2][1]) + "\n" + String.valueOf(reference1[2][1]) + "\n" +  String.valueOf(reference2[2][1]) + "\n" +  String.valueOf(reference3[2][1]) + "\n" +  reference4[2][1] + "\n" +  String.valueOf(reference5[2][1]) + "\n" +  String.valueOf(reference6[2][1]) ;
//        Toast.makeText(MainActivity.this, rndlines, Toast.LENGTH_SHORT).show();
//        analysisSummary.append(rndlines);

        analysisSummary.append("\n checking reference 6 \n");
        reArrangeReferenceData(reference6);
        compare2EqualLengthArrays(sample, repairedRefData);

    }
}