package edu.project.sensorplotter;

import java.util.Arrays;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;


public class SensorPlotterActivity extends Activity implements SensorEventListener, View.OnClickListener {

	private Spinner sensorSpinner, sampleRateSpinner;
	private Button StartButton, ClearButton, StopButton;

	private SensorManager sensorMgr = null;
	private int sensorDelay;
	private Sensor sensorType;
	
//	// plotter variables
	private static int HISTORY_SIZE = 120;            // number of points to plot in history
	private XYPlot sensorDataPlot = null;
    private SimpleXYSeries xHistorySeries = null;
    private SimpleXYSeries yHistorySeries = null;
    private SimpleXYSeries zHistorySeries = null;
    private LinkedList<Number> xHistory;
    private LinkedList<Number> yHistory;
    private LinkedList<Number> zHistory;
 
    {
        xHistory = new LinkedList<Number>();
        yHistory = new LinkedList<Number>();
        zHistory = new LinkedList<Number>();
 
        xHistorySeries = new SimpleXYSeries("X");
        yHistorySeries = new SimpleXYSeries("Y");
        zHistorySeries = new SimpleXYSeries("Z");
    }
	
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
    	sensorDataPlot = (XYPlot) findViewById(R.id.sensorDataPlot);
    	configureDataPlot();
    }

	private void configureDataPlot() {
//		// TODO Auto-generated method stub
//        // setup the  History plot:
		//sensorDataPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
		sensorDataPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
		sensorDataPlot.addSeries(xHistorySeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK, null));
		sensorDataPlot.addSeries(yHistorySeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(100, 200, 100), Color.BLACK, null));
		sensorDataPlot.addSeries(zHistorySeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.BLACK, null));
		sensorDataPlot.setDomainStepValue(5);
		sensorDataPlot.setTicksPerRangeLabel(3);
		sensorDataPlot.setDomainLabel("Sample Index");
		sensorDataPlot.getDomainLabelWidget().pack();
		sensorDataPlot.setRangeLabel("Sensor Output");
		sensorDataPlot.getRangeLabelWidget().pack();
		sensorDataPlot.disableAllMarkup();

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
			xHistory.clear();
			yHistory.clear();
			zHistory.clear();
			// update the plot with the updated history Lists:
	        zHistorySeries.setModel(zHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	        yHistorySeries.setModel(yHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	        xHistorySeries.setModel(xHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	        // redraw the Plots:
	        sensorDataPlot.redraw();
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
        // update instantaneous data:
        Number[] series1Numbers = {arg0.values[0], arg0.values[1], arg0.values[2]};
 
        // get rid the oldest sample in history:
        if (xHistory.size() > HISTORY_SIZE) {
            xHistory.removeFirst();
            yHistory.removeFirst();
            zHistory.removeFirst();
        }
 
        // add the latest history sample:
        xHistory.addLast(arg0.values[0]);
        yHistory.addLast(arg0.values[1]);
        zHistory.addLast(arg0.values[2]);
 
        // update the plot with the updated history Lists:
        zHistorySeries.setModel(zHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        yHistorySeries.setModel(yHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        xHistorySeries.setModel(xHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
 
        // redraw the Plots:
        sensorDataPlot.redraw();
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