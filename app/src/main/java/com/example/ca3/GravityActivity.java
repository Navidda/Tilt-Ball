package com.example.ca3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GravityActivity extends AppCompatActivity implements SensorEventListener {
    TiltBallView tiltBallView;
    private SensorManager sensorManager;
    private Sensor gravitySensor;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Gravity");
        tiltBallView = new TiltBallView(this);
        setContentView(tiltBallView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            tiltBallView.onSensorEvent(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class TiltBallView extends View {
        private static final int CIRCLE_RADIUS = 70; //pixels
        private static final float mooS = 0.15f;
        private static final float mooK = 0.1f;

        private Paint paint;
        private float x, y, vx, vy;
        private float[] gravity;
        private int viewWidth;
        private int viewHeight;

        public TiltBallView(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.BLACK);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            viewWidth = w;
            viewHeight = h;
            x = w / 2;
            y = h / 2;
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            updateCoordinates();
            canvas.drawCircle((int) x, (int) y, CIRCLE_RADIUS, paint);
            invalidate();

        }

        private void validateCoordinates() {
            if (x <= CIRCLE_RADIUS) {
                x = CIRCLE_RADIUS;
                vx *= -0.8;
            }
            if (x >= viewWidth - CIRCLE_RADIUS) {
                x = viewWidth - CIRCLE_RADIUS;
                vx *= -0.8;
            }
            if (y <= CIRCLE_RADIUS) {
                y = CIRCLE_RADIUS;
                vy *= -0.8;
            }
            if (y >= viewHeight - CIRCLE_RADIUS) {
                y = viewHeight - CIRCLE_RADIUS;
                vy *= -0.8;
            }

        }
        
        private void updateCoordinates() {
            float v = (float) Math.hypot(vx, vy);
            if (Math.abs(v) > 0.1) {
                float fk = gravity[2] * mooK;
                vx += -gravity[0] - fk * vx / v;
                vy += gravity[1] - fk * vy / v;
            }
            else {
                float fs = gravity[2] * mooS;
                float fxy = (float) Math.hypot(gravity[0], gravity[1]);
                if (fxy < fs)
                    return;
                vx += -gravity[0];
                vy += gravity[1];
            }

            x += vx;
            y += vy;
            validateCoordinates();
        }

        public void onSensorEvent(SensorEvent event) {
            //
            gravity = event.values;
        }
    }
}
