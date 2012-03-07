package edu.project.sensorplotter;

import com.androidplot.xy.XYPlot;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View;


public class SensorPlotterActivity extends Activity implements SensorEventListener, View.OnClickListener {

	private Spinner sensorSpinner, sampleRateSpinner;
	private Button StartButton, ClearButton, StopButton;
	//private XYPlot sensorDataPlot;
	private SensorManager sensorMgr = null;
	private int sensorDelay;
	private Sensor sensorType;
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	      if (parent.getCount() == 7) {
	    	  // Sensor guy
	    	  setSensor(id);
	      } else if (parent.getCount() == 4) {
	    	  // Sample rate
	    	  setSampleRate(id);
	      }
	    }

	    public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}
	
	private void setSensor(long id) {
		switch ((int) id) {
		case 0:
			sensorType = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			break;
		case 1:
			sensorType = sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY);
			break;
		case 2:
			sensorType = sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			break;
		case 3:
			sensorType = sensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			break;
		case 4:
			sensorType = sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			break;
		case 5:
			sensorType = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			break;
		case 6:
			sensorType = sensorMgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			break;
		}
	}
		
	private void setSampleRate(long id) {
		switch ((int) id) {
		case 0:
			sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
			break;
		case 1:
			sensorDelay = SensorManager.SENSOR_DELAY_GAME;
			break;
		case 2:
			sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
			break;
		case 3:
			sensorDelay = SensorManager.SENSOR_DELAY_UI;
			break;
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        initControls();
        
    }
    
    private void initControls() {
    	sensorType = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
    	// Spinners
    	sensorSpinner = (Spinner) findViewById(R.id.sensor);
    	sampleRateSpinner = (Spinner) findViewById(R.id.sampleRate);
    	configureSpinners();
    	// Buttons
    	StartButton = (Button) findViewById(R.id.Start);
    	StartButton.setOnClickListener(this);
    	
    	ClearButton = (Button) findViewById(R.id.Clear);
    	ClearButton.setOnClickListener(this);
    	
    	StopButton = (Button) findViewById(R.id.Stop);
    	StopButton.setOnClickListener(this);
    	
    	// XY Plot
    	//sensorDataPlot = (XYPlot) findViewById(R.id.sensorDataPlot);
    	configureDataPlot();
    }

	private void configureDataPlot() {
		// TODO Auto-generated method stub
		
	}


	private void configureSpinners() {
		// This code connects the sensor_array string-array in strings.xml 
		// to each spinner so that they have the appropriate choices.
		ArrayAdapter<CharSequence> sensorAdapter = ArrayAdapter.createFromResource(
	            this, R.array.sensor_array, android.R.layout.simple_spinner_item);
	    sensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sensorSpinner.setAdapter(sensorAdapter);
	    sensorSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	    
	    ArrayAdapter<CharSequence> sampleRateAdapter = ArrayAdapter.createFromResource(
	            this, R.array.rate_array, android.R.layout.simple_spinner_item);
	    sampleRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sampleRateSpinner.setAdapter(sampleRateAdapter);
	    sampleRateSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.Start) {
			sensorMgr.registerListener(this, sensorType, sensorDelay);
		} else if (arg0.getId() == R.id.Clear) {
			// clear button stuff
		} else if (arg0.getId() == R.id.Stop) {
			sensorMgr.unregisterListener(this);
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    protected void onStop() {
    	sensorMgr.unregisterListener(this);
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
        sensorMgr.unregisterListener(this);
    }

}