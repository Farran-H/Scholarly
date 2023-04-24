package com.example.myapplication;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MapScreen extends AppCompatActivity {

    private ImageView imageView;
    private Matrix matrix;
    private float[] matrixValues;
    private float prevX;
    private float prevY;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen); // Replace with your own layout

        // Find the ImageView in the layout
        imageView = findViewById(R.id.mapImage); // Replace with the ID of your ImageView

        // Set the large image in the ImageView
        imageView.setImageResource(R.drawable.map_of_ul); // Replace with your own image resource

        matrix = new Matrix();
        matrixValues = new float[9];

        // Set touch listener on ImageView
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Calculate the distance moved
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;

                        // Apply the translation to the matrix
                        matrix.postTranslate(dx, dy);

                        // Update the ImageView with the new matrix
                        imageView.setImageMatrix(matrix);

                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        prevX = 0;
                        prevY = 0;
                        break;
                }
                return true;
            }
        });
    }
}
