package edu.project.sensorplotter;

import com.androidplot.xy.XYPlot;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View;


public class SensorPlotterActivity extends Activity implements SensorEventListener, View.OnClickListener {

	private Spinner sensor, sampleRate;
	private Button Start, Clear, Stop;
	//private XYPlot sensorDataPlot;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initControls();
    }
    
    private void initControls() {
    	// Spinners
    	sensor = (Spinner) findViewById(R.id.sensor);
    	sampleRate = (Spinner) findViewById(R.id.sampleRate);
    	configureSpinners();
    	// Buttons
    	Start = (Button) findViewById(R.id.Start);
    	Clear = (Button) findViewById(R.id.Clear);
    	Stop = (Button) findViewById(R.id.Stop);
    	configureButtons();
    	// XY Plot
    	//sensorDataPlot = (XYPlot) findViewById(R.id.sensorDataPlot);
    	configureDataPlot();
    }

	private void configureDataPlot() {
		// TODO Auto-generated method stub
		
	}

	private void configureButtons() {
		// TODO Auto-generated method stub
		
	}

	private void configureSpinners() {
		ArrayAdapter<CharSequence> sensorAdapter = ArrayAdapter.createFromResource(
	            this, R.array.sensor_array, android.R.layout.simple_spinner_item);
	    sensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sensor.setAdapter(sensorAdapter);
	    
	    ArrayAdapter<CharSequence> sampleRateAdapter = ArrayAdapter.createFromResource(
	            this, R.array.rate_array, android.R.layout.simple_spinner_item);
	    sampleRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sampleRate.setAdapter(sampleRateAdapter);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}