package com.example.application;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Defining TextViews to show values of probabilities for each activity
    private TextView walkTV;
    private TextView walkupTV;
    private TextView walkdownTV;
    private TextView sitTV;
    private TextView standTV;
    private TextView layTV;
    // Defining TableRows for each activity
    private TableRow walkTR;
    private TableRow walkupTR;
    private TableRow walkdownTR;
    private TableRow sitTR;
    private TableRow standTR;
    private TableRow layTR;
    // Defining the ImageView to set an image with the activity
    private ImageView activityIV;
    // Defining Lists to keep all signals from sensors
    private static List<Float> tacc_x, tacc_y, tacc_z; // total acc.
    private static List<Float> bacc_x, bacc_y, bacc_z; // body component of acc.
    private static List<Float> gyr_x, gyr_y, gyr_z; // gyr. signal
    private static List<Float> all_data; // list for keeping all signals
    protected float[] gravity = new float[3]; // gravity component
    protected float[] filt_acc = new float[3]; // filtered acc. signal
    protected float[] filt_gyr = new float[3]; // filtered gyr. signal
    // Defining the Sensor Manager for acc. and gyr. sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope;
    // Defining the Interpreter for the TensorFlow Lite model
    private Interpreter tflite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Button creation
        Button exit_button = (Button) findViewById(R.id.button_exit);
        // Initializing TextViews with id
        walkTV = (TextView) findViewById(R.id.prob_walk);
        walkupTV = (TextView) findViewById(R.id.prob_walkup);
        walkdownTV = (TextView) findViewById(R.id.prob_walkdown);
        sitTV = (TextView) findViewById(R.id.prob_sit);
        standTV = (TextView) findViewById(R.id.prob_stand);
        layTV = (TextView) findViewById(R.id.prob_lay);
        // Initializing TableRows with id
        walkTR = (TableRow) findViewById(R.id.row_walk);
        walkupTR = (TableRow) findViewById(R.id.row_walkup);
        walkdownTR = (TableRow) findViewById(R.id.row_walkdown);
        sitTR = (TableRow) findViewById(R.id.row_sit);
        standTR = (TableRow) findViewById(R.id.row_stand);
        layTR = (TableRow) findViewById(R.id.row_lay);
        // Initializing the ImageView with id
        activityIV = (ImageView) findViewById(R.id.act_img);
        // Initializing Lists
        tacc_x = new ArrayList<>(); tacc_y = new ArrayList<>(); tacc_z = new ArrayList<>();
        bacc_x = new ArrayList<>(); bacc_y = new ArrayList<>(); bacc_z = new ArrayList<>();
        gyr_x = new ArrayList<>(); gyr_y = new ArrayList<>(); gyr_z = new ArrayList<>();
        all_data = new ArrayList<>();
        // Activating the Sensor Manager for accelerometer and gyroscope
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // Activating the Listener to get signals from sensors with a sampling frequency 50Hz (20000us)
        mSensorManager.registerListener(this, mAccelerometer, 20000);
        mSensorManager.registerListener(this, mGyroscope, 20000);
        // Loading the Interpreter with a CNN model
        try{
            tflite = new Interpreter(loadModelFile());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        // Loading the animation from the SpinKitView
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        // Initializing the ClickListener for the exit button
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Designing a dialog window with answers
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("HAR Application");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("Do you want to close an app?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    // Signals registration
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Accelerometer signals
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            // Low-pass filter with a cutoff 20Hz to reduce noises from the acc. signals
            filt_acc[0] = ConstValues.ALPHA_1 * filt_acc[0] + (1 - ConstValues.ALPHA_1) * event.values[0]/10;
            filt_acc[1] = ConstValues.ALPHA_1 * filt_acc[1] + (1 - ConstValues.ALPHA_1) * event.values[1]/10;
            filt_acc[2] = ConstValues.ALPHA_1 * filt_acc[2] + (1 - ConstValues.ALPHA_1) * event.values[2]/10;
            // Total acc. signals
            tacc_x.add(filt_acc[0]);
            tacc_y.add(filt_acc[1]);
            tacc_z.add(filt_acc[2]);
            // Using low-pass filter to separate gravity from the total acc. signals
            gravity[0] = ConstValues.ALPHA_2 * gravity[0] + (1 - ConstValues.ALPHA_2) * filt_acc[0];
            gravity[1] = ConstValues.ALPHA_2 * gravity[1] + (1 - ConstValues.ALPHA_2) * filt_acc[1];
            gravity[2] = ConstValues.ALPHA_2 * gravity[2] + (1 - ConstValues.ALPHA_2) * filt_acc[2];
            // Body acc. signals (sub gravity forces)
            bacc_x.add(filt_acc[0] - gravity[0]);
            bacc_y.add(filt_acc[1] - gravity[1]);
            bacc_z.add(filt_acc[2] - gravity[2]);
        }
        // Gyroscope signals
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Low-pass filter with a cutoff 20Hz to reduce noise from the gyr. signals
            filt_gyr[0] = ConstValues.ALPHA_1 * filt_gyr[0] + (1 - ConstValues.ALPHA_1) * event.values[0];
            filt_gyr[1] = ConstValues.ALPHA_1 * filt_gyr[1] + (1 - ConstValues.ALPHA_1) * event.values[1];
            filt_gyr[2] = ConstValues.ALPHA_1 * filt_gyr[2] + (1 - ConstValues.ALPHA_1) * event.values[2];
            // Gyr. signals after filtering
            gyr_x.add(filt_gyr[0]);
            gyr_y.add(filt_gyr[1]);
            gyr_z.add(filt_gyr[2]);
        }
        // Using the signals window in size 128
        if (bacc_x.size() >= 128 && bacc_y.size() >= 128 && bacc_z.size() >= 128
                && tacc_x.size() >= 128 && tacc_y.size() >= 128 && tacc_z.size() >= 128 &&
                gyr_x.size() >= 128 && gyr_y.size() >= 128 && gyr_z.size() >= 128) {
            // Signals normalisation
            normalisation();
            // Putting values into all_data
            all_data.addAll(tacc_x.subList(0,128));
            all_data.addAll(tacc_y.subList(0,128));
            all_data.addAll(tacc_z.subList(0,128));
            all_data.addAll(bacc_x.subList(0,128));
            all_data.addAll(bacc_y.subList(0,128));
            all_data.addAll(bacc_z.subList(0,128));
            all_data.addAll(gyr_x.subList(0,128));
            all_data.addAll(gyr_y.subList(0,128));
            all_data.addAll(gyr_z.subList(0,128));
            // Using a model to get results
            giveTheResult(all_data);
            // Clearing all Lists
            clearData();
        }
    }
    @Override // Default parameters for the SensorManager
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, 20000);
        mSensorManager.registerListener(this, mGyroscope, 20000);
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    // Loading the TensorFlow Lite model from the file
    private MappedByteBuffer loadModelFile() throws IOException{
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel  =inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    // Function to normalise values from SignalProcessing.ipynb
    private void normalisation()
    {
        for(int i = 0; i < ConstValues.NUM_SAMPLES; i++)
        {
            tacc_x.set(i,(2*( (tacc_x.get(i) - ConstValues.min_tacc_x)/(ConstValues.max_tacc_x-ConstValues.min_tacc_x))-1));
            tacc_y.set(i,(2*( (tacc_y.get(i) - ConstValues.min_tacc_y)/(ConstValues.max_tacc_y-ConstValues.min_tacc_y))-1));
            tacc_z.set(i,(2*( (tacc_z.get(i) - ConstValues.min_tacc_z)/(ConstValues.max_tacc_z-ConstValues.min_tacc_z))-1));
            bacc_x.set(i,(2*( (bacc_x.get(i) - ConstValues.min_bacc_x)/(ConstValues.max_bacc_x-ConstValues.min_bacc_x))-1));
            bacc_y.set(i,(2*( (bacc_y.get(i) - ConstValues.min_bacc_y)/(ConstValues.max_bacc_y-ConstValues.min_bacc_y))-1));
            bacc_z.set(i,(2*( (bacc_z.get(i) - ConstValues.min_bacc_z)/(ConstValues.max_bacc_z-ConstValues.min_bacc_z))-1));
            gyr_x.set(i,(2*( (gyr_x.get(i) - ConstValues.min_gyr_x)/(ConstValues.max_gyr_x-ConstValues.min_gyr_x))-1));
            gyr_y.set(i,(2*( (gyr_y.get(i) - ConstValues.min_gyr_y)/(ConstValues.max_gyr_y-ConstValues.min_gyr_y))-1));
            gyr_z.set(i,(2*( (gyr_z.get(i) - ConstValues.min_gyr_z)/(ConstValues.max_gyr_z-ConstValues.min_gyr_z))-1));
        }
    }
    // Function to convert all_data to the 2D Float list (128, 9)
    private float[][] sizeConversion(List<Float> all_data) {
        float[][] two_dim_list = new float[128][9];
        for (int counter = 0; counter < all_data.size(); counter ++){
            int integer_division = counter / 128;
            int remainder = counter % 128;
            Float value = all_data.get(counter);
            two_dim_list[remainder][integer_division] = (value != null ? value: Float.NaN);
        }
        return two_dim_list;
    }
    // Function to set values of probabilities in TextViews
    private void propabilities(float[][] results){
        walkTV.setText(String.format("%.02f", results[0][0]));
        walkupTV.setText(String.format("%.02f", results[0][1]));
        walkdownTV.setText(String.format("%.02f", results[0][2]));
        sitTV.setText(String.format("%.02f", results[0][3]));
        standTV.setText(String.format("%.02f", results[0][4]));
        layTV.setText(String.format("%.02f", results[0][5]));
    }
    // Function to set the image with the most likely activity into ImageView
    private void setActivity(float[][] results){
        int index = 0;
        float max = 0;
        // Finding the highest probability and saving the index
        for(int i = 0; i < results[0].length; i++){
            if(results[0][i]>=max){
                index = i;
                max = results[0][i];}
        }
        // Setting the default background color for TextViews
        walkTR.setBackgroundColor(Color.rgb(243,249,249));
        walkupTR.setBackgroundColor(Color.rgb(243,249,249));
        walkdownTR.setBackgroundColor(Color.rgb(243,249,249));
        sitTR.setBackgroundColor(Color.rgb(243,249,249));
        standTR.setBackgroundColor(Color.rgb(243,249,249));
        layTR.setBackgroundColor(Color.rgb(243,249,249));
        // Setting the yellow background color to the most likely activity
        if(index==0){
            activityIV.setImageResource(R.drawable.walking);
            walkTR.setBackgroundColor(Color.rgb(247,255,147));}
        if(index==1){
            activityIV.setImageResource(R.drawable.walkingup);
            walkupTR.setBackgroundColor(Color.rgb(247,255,147));}
        if(index==2){
            activityIV.setImageResource(R.drawable.walkingdown);
            walkdownTR.setBackgroundColor(Color.rgb(247,255,147));}
        if(index==3){
            activityIV.setImageResource(R.drawable.sitting);
            sitTR.setBackgroundColor(Color.rgb(247,255,147));}
        if(index==4){
            activityIV.setImageResource(R.drawable.standing);
            standTR.setBackgroundColor(Color.rgb(247,255,147));}
        if(index==5){
            activityIV.setImageResource(R.drawable.laying);
            layTR.setBackgroundColor(Color.rgb(247,255,147));}
    }
    // Function to run the model and get results
    private void giveTheResult(List<Float> all_data){
        // Changing the size of the all_data into (128,9)
        float[][] two_dim_list = sizeConversion(all_data);
        // Fitting the size of the input data to the model (1,128,9)
        float [][][] input = new float[1][128][9];
        input[0] = two_dim_list;
        // Initializing the output list (1,6)
        float [][] prediction_results = new float[1][6];
        // Running the tf_lite model, the output will be written in prediction_results
        tflite.run(input, prediction_results);
        // Probabilities visualisation
        propabilities(prediction_results);
        setActivity(prediction_results);
    }
    // Function to clear Lists with data from sensors to make a new window with samples
    private void clearData(){
        tacc_x.clear(); tacc_y.clear(); tacc_z.clear();
        bacc_x.clear(); bacc_y.clear(); bacc_z.clear();
        gyr_x.clear(); gyr_y.clear(); gyr_z.clear();
        all_data.clear();
    }
}