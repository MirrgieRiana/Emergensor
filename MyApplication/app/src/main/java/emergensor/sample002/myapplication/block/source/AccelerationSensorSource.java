package emergensor.sample002.myapplication.block.source;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.List;

import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.lib.ArrayVector;
import emergensor.sample002.myapplication.lib.Vector;

/**
 * 3軸の加速度を通知する。
 * Vector: x, y, z
 */
public class AccelerationSensorSource extends AbstractSensorSource<Message<Vector<Double>>> {

    private final Activity activity;
    private final boolean ignoreGravity;
    private SensorManager sensorManager;
    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.values.length >= 3) {
                output(new Message<>(
                        sensorEvent.timestamp / 1000,
                        (Vector<Double>) new ArrayVector<>(
                                (double) sensorEvent.values[0],
                                (double) sensorEvent.values[1],
                                (double) sensorEvent.values[2])));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    };

    public AccelerationSensorSource(Activity activity, boolean ignoreGravity) {
        super(activity);
        this.activity = activity;
        this.ignoreGravity = ignoreGravity;
    }

    @Override
    public boolean checkPermission() {
        return true;
    }

    @Override
    protected void initImpl() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void startImpl() {
        List<Sensor> sensors = sensorManager.getSensorList(ignoreGravity ? Sensor.TYPE_LINEAR_ACCELERATION : Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void stopImpl() {
        sensorManager.unregisterListener(sensorEventListener);
    }

}
