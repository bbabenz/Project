package info.androidhive.project;

import android.util.Log;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.Callback;

/**
 * Created by B on 20/9/2558.
 */

//TODO Add new sensors type
enum SensorType {
    GYRO,ACCEL, NONE
}

public class NetworkManager {
    //TODO Add new sensors type
    private final int allSensor = 2;
    private boolean isComplete[] = new boolean[allSensor];
    private HashMap<String,Float> allData[] = new HashMap[allSensor];

    private final String IP = "10.1.7.52";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Request.Builder builder = new Request.Builder();

    private static class Holder {
        static final NetworkManager INSTANCE = new NetworkManager();
    }

    public static NetworkManager getInstance() {
        return Holder.INSTANCE;
    }

    public void putData(SensorType type,  HashMap<String,Float> valueMap){

        //save data to array
        switch (type){
            case GYRO :{
                allData[getIndex(SensorType.GYRO)] = new HashMap<String, Float>(valueMap);
                isComplete[getIndex(SensorType.GYRO)] = true;
                break;
            }
            case ACCEL :{
                allData[getIndex(SensorType.ACCEL)] = new HashMap<String, Float>(valueMap);
                isComplete[getIndex(SensorType.ACCEL)] = true;
                break;
            }
        }

        //check if all sensor come
        boolean didAllSensorCome = true;
        for(int i = 0;i < allSensor; i++){
            if(isComplete[i] == false)
                didAllSensorCome = false;
        }

        //if all sensor sent data then post to server
        if(didAllSensorCome)
            postData();
    }

    private boolean postData(){
        String URL = "http://"+ IP +"/sensor_post/post.php?";
        //TODO send data
        for(int index = 0 ;index < allSensor ; index++){
            Iterator it = allData[index].entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                URL += pair.getKey() + "=" + pair.getValue() + "&";
                it.remove(); // avoids a ConcurrentModificationException
            }

        }

        //cut "&" at the final position out of String
        URL = URL.substring(0,URL.length() - 1);
        Log.d("URL", URL);
        Request request = builder.url(URL).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("onFailure", "Error - " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d("onResponse", response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("onResponse", "Error - " + e.getMessage());
                    }
                } else {
                    Log.d("onResponse", "Not Success - code : " + response.code());
                }
            }

        });

        //clear all boolean
        for(int i = 0;i < allSensor; i++){
            isComplete[i] = false;
        }
        return true;
    }

    //TODO Add new sensors type
    private int getIndex(SensorType type){
        switch (type){
            case GYRO :{
                return 0;
            }
            case ACCEL :{
                return 1;
            }
            default: return -1;
        }
    }

    //TODO Add new sensors type
    private SensorType getType(int index){
        switch (index) {
            case 0: {
                return SensorType.GYRO;
            }
            case 1: {
                return SensorType.ACCEL;
            }
            default: return SensorType.NONE;
        }
    }


}

