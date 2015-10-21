package info.androidhive.project;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Accelerometer extends Activity {
    TextView textX1, textY1, textZ1,textX2, textY2, textZ2;
    SensorManager sensorManager;
    Sensor sensor1,sensor2;
    float x1,y1,z1,x2,y2,z2;
    public static final int REQUEST_VIDEO = 3;
    VideoView video;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        video = (VideoView)findViewById(R.id.video);
        video.setMediaController(new MediaController(this));


        Button buttonIntent = (Button)findViewById(R.id.buttonIntent);
        buttonIntent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent =
                        new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Accelerometer.this.startActivityForResult(intent, REQUEST_VIDEO); // use video capture
            }


        });


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //call accelerometer sensor
        sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); //call gyroscope sensor

        textX1 = (TextView) findViewById(R.id.textX1);
        textY1 = (TextView) findViewById(R.id.textY1);
        textZ1 = (TextView) findViewById(R.id.textZ1);
        textX2 = (TextView) findViewById(R.id.textX2);
        textY2 = (TextView) findViewById(R.id.textY2);
        textZ2 = (TextView) findViewById(R.id.textZ2);

    }



    public void onResume() {
        super.onResume();

        sensorManager.registerListener(accelListener, sensor1, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroListener, sensor2, SensorManager.SENSOR_DELAY_NORMAL);

    }



    public void onStop() {
        super.onStop();

//        sensorManager.unregisterListener(accelListener);
//        sensorManager.unregisterListener(gyroListener);


    }

    SensorEventListener accelListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor1, int acc) { }

        public void onSensorChanged(SensorEvent event) {
            x1 = event.values[0];
            y1 = event.values[1];
            z1 = event.values[2];

            textX1.setText("X : " + (int)x1);
            textY1.setText("Y : " + (int) y1);
            textZ1.setText("Z : " + (int) z1);

            HashMap<String,Float> data = new HashMap<String, Float>();
            data.put("x_axis",x1);
            data.put("y_axis",y1);
            data.put("z_axis",z1);

            NetworkManager.getInstance().putData(SensorType.ACCEL, data);
        }
    };
    SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor2, int acc) { }

        public void onSensorChanged(SensorEvent event) {
            x2 = event.values[0];
            y2 = event.values[1];
            z2 = event.values[2];

            textX2.setText("X : " + (int)x2+ " rad/s");
            textY2.setText("Y : " + (int)y2+ " rad/s");
            textZ2.setText("Z : " + (int)z2+ " rad/s");

            HashMap<String,Float> data = new HashMap<String, Float>();
            data.put("x_gyro",x2);
            data.put("y_gyro",y2);
            data.put("z_gyro",z2);

            NetworkManager.getInstance().putData(SensorType.GYRO, data);
        }
    };


    public void onActivityResult(int requestCode, int resultCode
            , Intent data) {
        if (requestCode == REQUEST_VIDEO && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            video.setVideoURI(uri);
        }
    }



}