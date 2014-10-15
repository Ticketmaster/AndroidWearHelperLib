package com.twotoasters.androidwearhelpers.library.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class CompassTracker extends SensorTrackerBase {

    // Heading calculation vars
    private float[] gravity;
    private float[] geomagnetic;
    private float[] orientation;

    //Listener for heading changes
    private OnHeadingChangedListener listener;

    public CompassTracker(Context context) {
        super(context, new int[] {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_MAGNETIC_FIELD});
    }

    /**
     * Sets a listener for receiving callbacks as changes in the compass heading are detected.
     * @param listener
     */
    public void setOnHeadingChangedListener(OnHeadingChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onRegister() {
        orientation = new float[3];
    }

    @Override
    protected void onUnregister() {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values;
        if (gravity != null && geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                SensorManager.getOrientation(R, orientation);
                int azimuth = (int) Math.round(Math.toDegrees(orientation[0]));
                if (listener != null) listener.onHeadingChanged(azimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //no-op
    }

    public interface OnHeadingChangedListener {
        /**
         * Called when the compass heading changes.
         * @param heading The heading in degrees, where 0 == magnetic north
         */
        void onHeadingChanged(int heading);
    }
}
