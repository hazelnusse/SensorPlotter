// SensorPlotter
// Copyright (c) 2012, Dale Lukas Peterson <hazelnusse@gmail.com>, Julia Sohnen <jmsohnen@ucdavis.edu>
//
// Permission to use, copy, modify, and/or distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
// REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
// INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
// LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
// OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
// PERFORMANCE OF THIS SOFTWARE.

package edu.project.sensorplotter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
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

	// GUI Widgets
	private Spinner sensorSpinner, sampleRateSpinner;
	private Button StartButton, ClearButton, StopButton;

	// Sensor variables
	private SensorManager sensorMgr = null;
	private int sensorDelay;
	private Sensor sensorType;
	
	// Data writing variables
	private File dataFile = null;
	private FileWriter csvWriter = null;
	private BufferedWriter out = null;
	
	//  Plotter variables
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
	
	// setSensor() is called when sensor is selected from spinner;
	// sets private member variable so SensorListener is registered
	// with the proper sensor.
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
	
	// setSampleRate() is called when sample rate is selected from spinner;
	// sets private member variable so SensorListener is registered
	// with the proper delay.
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
        try {
            dataFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sensor_data.csv");
			csvWriter = new FileWriter(dataFile);
			out = new BufferedWriter(csvWriter);
			out.write("time,x,y,z,sensor_type,sensor_delay\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // Initialize GUI elements and plotting elements.
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
    
    // Configure the plot
	private void configureDataPlot() {
		// setup the  History plot: adapted from androidplot.com example
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

	// configureSpinners() connects the sensor_array string-array in strings.xml 
	// to each spinner so that they have the appropriate choices.
	private void configureSpinners() {
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

	// onClick is called when user clicks spinners or buttons
	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.Start) {
			sensorMgr.registerListener(this, sensorType, sensorDelay);
		} else if (arg0.getId() == R.id.Clear) {
			xHistory.clear();
			yHistory.clear();
			zHistory.clear();
			// update the plot with the cleared history Lists:
	        zHistorySeries.setModel(zHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	        yHistorySeries.setModel(yHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	        xHistorySeries.setModel(xHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	        // redraw the Plots (clears it)
	        sensorDataPlot.redraw();
		} else if (arg0.getId() == R.id.Stop) {
			sensorMgr.unregisterListener(this);
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// Parts of this function are adapted from examples on androidplot.com
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
        try {
        	// Write to csv file the time, sensor values, type, and delay
			out.write(System.nanoTime() + "," +
					  arg0.values[0] + "," +
					  arg0.values[1] + "," +
					  arg0.values[2] + "," +
					  sensorType.getType() + "," +
					  sensorDelay + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
 
        // update the plot with the updated history Lists:
        zHistorySeries.setModel(zHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        yHistorySeries.setModel(yHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        xHistorySeries.setModel(xHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        // redraw the Plots:
        sensorDataPlot.redraw();
	}
	
    @Override
    protected void onStop() {
    	// Unregister listener
    	sensorMgr.unregisterListener(this);
    	try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
        sensorMgr.unregisterListener(this);
    }
}