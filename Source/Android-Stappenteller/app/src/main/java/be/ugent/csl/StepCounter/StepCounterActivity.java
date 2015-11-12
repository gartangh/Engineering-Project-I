package be.ugent.csl.StepCounter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

/*
 * @author Andy Georges
 * @author Bart Coppens
 * @author Christophe Foket
 * 
 * This class implements the (only) activity of the application.
 * Here we define the actions that need to be taken when something
 * happens to each of the UI components and to update the various
 * UI fields when stuff happens.
 * 
 * Note to students. It is indicated where you need to add or change
 * code. If you add extra code, feel free to do so, but add it at the bottom of the 
 * class! Otherwise you might generate merge conflicts with patches
 * we make available at a later stage in the project.
 */
public class StepCounterActivity extends Activity {
	
	public static final String TAG = StepCounterActivity.class.getName();
	
	private static final boolean USE_SERVICE = true;
	
	/* UI items. You will need to attach these to the corresponding
	 * item in the main.xml layout you defined. Note that it is
	 * not necessary for these variables to be declared here, they
	 * could also be declared e.g., in the onCreate() method, IF 
	 * they need not be accessed from elsewhere. However, for 
	 * clarity, we chose to declare them as object fields.
	 */
	
	/* buttons */
	private Button quitButton;
	private Button logButton;
	private Button clearButton;

	/* checkboxes */
    private CheckBox logDataCheckBox;
    
	/* seekbar for the logging rate */
	private SeekBar rateMultiplierBar;
	
	/* spinner for detector selection */
	private Spinner detectorSpinner;
	
	/* text fields */
	private TextView sampleRateText;
	private TextView traceLinesText;
	private TextView numberOfStepsText;
	private TextView numberOfStepsText2;
	
	/* message input field */
	private EditText messageEditText;

	private int counter = 0;

	/*
	 * Interaction with the Service that gathers sensor data.
	 */
	private boolean accellMeterServiceBound = false;

	private ServiceConnection accellMeterServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Util.get().setService(((AccellMeterService.LocalBinder)service).getService());
			Log.i(Util.TAG, "Service connection established");
		}

		public void onServiceDisconnected(ComponentName className) {
			Util.get().setService(null);			
			Log.i(Util.TAG, "Service connection removed");
		}
	};

	public void onDestroy() {
		if (USE_SERVICE) {
	    	/* take down the service */
			if (accellMeterServiceBound)
				unbindService(accellMeterServiceConnection);
			stopService(new Intent(this, AccellMeterService.class));
		}
		super.onDestroy();
	}

     /* This function is called when the activity is created.
     * 
     * Here, you need to add code for several things.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* We indicate here that we will be using the layout defined in main.xml
         * which has been translated into R.java.
         */
        setContentView(R.layout.main);

        /* PRACTICUM 3.
         * Opgave 2: a. Geef deze widgets een gepaste waarde door te refereren
         *              naar de juiste ID's uit de layout.
         *           b. Zorg ervoor dat de juiste luisteraars gedefinieerd
         *              worden en dat je ook de passende stappen onderneemt
         *              opdat de UI consistent blijft en het resultaat van 
         *              actie weergegeven wordt. Je kunt hiervoor anonieme 
         *              objecten gebruiken waarvoor je de juiste methoden 
         *              implementeert.
         */
        
        /* Buttons */
        /* The closing button */
        quitButton = (Button)findViewById(R.id.quit);

		// Close program
		quitButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}
		);

		clearButton = (Button)findViewById(R.id.clearButton);
		clearButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Util.get().getLogger().clearLogFile();
					}
				}
		);

       	/* The log button */
       	logButton = (Button)findViewById(R.id.logButton);
		logButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Util.get().getLogger().setLogPrefix(messageEditText.getText().toString());
					}
				}
		);

       	/* Checkboxes */
       	logDataCheckBox = (CheckBox)findViewById(R.id.logDataCheckBox);

		// Start or stop logging data
		logDataCheckBox.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							Log.i(Util.TAG, "Log Data ON");
							logDataCheckBox.setText("Log Data ON");
						} else {
							Log.i(Util.TAG, "Log Data OFF");
							logDataCheckBox.setText("Log Data OFF");
						}
						Util.get().getLogger().setLogging(isChecked);
					}
				}
		);

        /* Text Views */
		sampleRateText = (TextView) findViewById(R.id.sampleRateText);
		traceLinesText = (TextView)findViewById(R.id.traceLinesText);
		// Shows amount of steps taken
		numberOfStepsText = (TextView)findViewById(R.id.numberOfStepsText);
		// Displays: Number Of Steps
		numberOfStepsText2 = (TextView)findViewById(R.id.numberOfStepsText2);

        /* Input field for the message */
        messageEditText = (EditText)findViewById(R.id.messageEditText);

		// Message disapears when clicked
		messageEditText.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						messageEditText.setText("");
					}
				}
		);

		// message is shown after: your message:
		messageEditText.setOnEditorActionListener(
				new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						String message = "Your message:\n" + messageEditText.getText();
						messageEditText.setText(message);
						return false;
					}
				}
		);

        /* seekbar */
        rateMultiplierBar = (SeekBar)findViewById(R.id.rateMultiplierBar);

		rateMultiplierBar.setProgress(0);
		sampleRateText.setText("Sample Rate");

		rateMultiplierBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				String s;
				switch ((int) rateMultiplierBar.getProgress()) {
					case 0:
						s = "Normal: 5 Hz";
						Util.get().setRate(SampleRate.NORMAL);
						break;
					case 1:
						s = "UI: 15 Hz";
						Util.get().setRate(SampleRate.UI);
						break;
					case 2:
						s = "Game 50 Hz";
						Util.get().setRate(SampleRate.GAME);
						break;
					case 3:
						s = "Fastest: 100 Hz";
						Util.get().setRate(SampleRate.FASTEST);
						break;
					default:
						s = "Normal: 5 Hz";
				}
				sampleRateText.setText(s);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// animation on sampleRateText?
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// animation on sampleRateText?
			}
		});

        /* Drop down menu */
        detectorSpinner = (Spinner) findViewById(R.id.detectorList);

        ArrayAdapter<CharSequence> detectorAdapter = ArrayAdapter.createFromResource(this, R.array.detector_array, android.R.layout.simple_spinner_item);
        detectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        detectorSpinner.setAdapter(detectorAdapter);
        detectorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String detectorName = parent.getItemAtPosition(pos).toString();
				Util.get().setStepDetector(detectorName);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
        });

        if(USE_SERVICE) {
	       	/*
	       	 * This binds the service that obtains sensor data
	       	 */
	       	Intent i = new Intent(this, AccellMeterService.class);
	       	/* First we bind to the service to be able to talk to it through the local binding */
	       	accellMeterServiceBound = bindService(i, accellMeterServiceConnection, BIND_AUTO_CREATE);
	       	
	       	/* We also need to start the service explicitly, with the same intent to make sure
	       	 * the service keeps running even when the activity loses focus.
	       	 */
	       	startService(i);
        }
        
		IntentFilter filter = new IntentFilter();
		filter.addAction("be.ugent.csl.StepCounterIntent");

		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				/*
				 * Opgave: verwerk hier de intent en stel het aantal stappen in.
				 */
				// TODO
				counter++;
				numberOfStepsText.setText(String.valueOf(counter));

			}
		};
		registerReceiver(receiver, filter);
    }
}