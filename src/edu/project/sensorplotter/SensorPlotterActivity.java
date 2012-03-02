package edu.project.sensorplotter;

import com.androidplot.xy.XYPlot;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SensorPlotterActivity extends Activity {

	private Spinner sensor, sampleRate;
	private Button Start, Clear, Stop;
	private XYPlot sensorDataPlot;
	
	
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
    	// XY Plot
    	sensorDataPlot = (XYPlot) findViewById(R.id.sensorDataPlot);
    }

	private void configureSpinners() {
		ArrayAdapter sensorAdapter = ArrayAdapter.createFromResource(
	            this, R.array.sensor, android.R.layout.simple_spinner_item);
	    sensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sensor.setAdapter(sensorAdapter);
	    
	    ArrayAdapter sampleRateAdapter = ArrayAdapter.createFromResource(
	            this, R.array.sensor, android.R.layout.simple_spinner_item);
	    sampleRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sampleRate.setAdapter(sensorAdapter);
	}
}