package androidintroductie.ugent.be.steps;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.NotificationManager;
import android.support.v7.app.NotificationCompat;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;
    private TextView textView10;
    private TextView textView11;
    private EditText editText1;
    private ProgressBar progressBar;

    private int target = 1000;
    private int steps = 0;
    private int run = 0;
    private int max = 0;
    private int total = 0;

    public static final String TAG = MainActivity.class.getName();

    private static final boolean USE_SERVICE = true;

    private boolean accellMeterServiceBound = true;

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

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void onDestroy() {
        if (USE_SERVICE) {
	    	/* take down the service */
            if (accellMeterServiceBound)
                unbindService(accellMeterServiceConnection);
            stopService(new Intent(this, AccellMeterService.class));
        }
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // "Number of steps"
        textView1 = (TextView) findViewById(R.id.textView1);
        // Shows number of steps taken today
        textView2 = (TextView) findViewById(R.id.textView2);
        // Shows average steps taken each day
        textView3 = (TextView) findViewById(R.id.textView3);
        // Displays an anecdote
        textView4 = (TextView) findViewById(R.id.textView4);
        // Shows target, reached or not, how many steps to take till target and lets the user edit the target
        textView5 = (TextView) findViewById(R.id.textView5);
        // Shows total amount of steps since install
        textView6 = (TextView) findViewById(R.id.textView6);
        // Shows amount of km
        textView7 = (TextView) findViewById(R.id.textView7);
        // Shows amount of kCal burnt
        textView8 = (TextView) findViewById(R.id.textView8);
        // Shows percentage of progressbar
        textView9 = (TextView) findViewById(R.id.textView9);
        // Shows amount of fat burned in grams
        textView10 = (TextView) findViewById(R.id.textView10);
        // Shows max amount of steps put in a day
        textView11 = (TextView) findViewById(R.id.textView11);

        // Shows progress in percent
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Sets target
        editText1 = (EditText) findViewById(R.id.editText1);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Notification
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textView1.setText("Steps Today");
        textView2.setText("0");
        // Pick a random anecdote on startup.
        textView4.setText(anecdote());
        // Pick a random anecdote when clicked.
        textView4.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textView4.setText(anecdote());
                    }
                }
        );

        textView6.setText("Total 0");
        // Calculate km
        textView7.setText((steps * 0.75) / 1000.000 + " km");
        // Calculate kCal
        textView8.setText((steps * 3.0) / 100.0 + " kCal");
        // Calculate fat burned
        textView10.setText(steps / 300.0 + " g");
        // Calculate max
        textView11.setText("Max " + max);
        if (steps >= max) {
            max = steps;
            textView11.setText("Max " + max);
        }

        editText1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText1.setText("");
                    }
                }
        );

        // Target > 100 || target < 100000)
        editText1.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        int input = Integer.parseInt(editText1.getText().toString());
                        if (input <= 100000 && input >= 100) {
                            target = input;
                            editText1.setText(String.valueOf(target));
                            int stepsComparedToTarget = target - steps - run;
                            if (stepsComparedToTarget <= 0) {
                                textView5.setText("Congratulations! You have reached your daily target of " + target + " steps!");
                                progressBar.setProgress(100);
                                textView9.setText("100 %");
                                // Notification (doesn't work jet)
                                //Notify("Steps++ Target Reached","Congratulations! You have reached your daily target of " + target + " steps!");
                            } else {
                                textView5.setText("Your target is " + target + " steps, still " + (target - steps - run) + " to go!");
                                progressBar.setProgress(steps * 100 / target);
                                progressBar.setSecondaryProgress(run * 100/ target);
                                textView9.setText((steps + run) / target + " %");
                            }
                        }
                        return false;
                    }
                }
        );

        // Links text with target
        int stepsComparedToTarget = target - steps - run;
        if (stepsComparedToTarget <= 0) {
            textView5.setText("Congratulations! You have reached your daily target of " + target + " steps!");
            progressBar.setProgress(steps * 100/ target);
            progressBar.setSecondaryProgress(run * 100 / target);
            textView9.setText("100 %");
            // Notification (doesn't work jet)
            //Notify("Steps++ Target Reached","Congratulations! You have reached your daily target of " + target + " steps!");
        } else {
            textView5.setText("Your target is " + target + " steps, still " + (target - steps - run) + " to go!");
            progressBar.setProgress(steps * 100 / target);
            progressBar.setSecondaryProgress(run * 100/ target);
            textView9.setText((steps + run) / target + " %");
        }

        // Make a notificationbuilder
        //NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        //int days = 0;
        // When 00:00 o'clock;
        /*
        steps = 0;
        run = 0;
        days++;
         */
        // For the moment
        int days = 1;
        int average = (int)(total * 1.0 / days);
        String average2 = String.valueOf(average);
        if (average < 1000) {
            textView3.setText("Average " + average2);
        } else if (average < 1000000) {
            textView3.setText("Average " + average2.substring(0, average2.length() - 3) + "," + average2.substring(average2.length() - 3));
        } else {
            textView3.setText("Average " + average2.substring(0, average2.length() - 6) + "," + average2.substring(average2.length() - 6, average2.length() - 3) + "," + average2.substring(average2.length()- 3));
        }

        if(USE_SERVICE) {
            Intent i = new Intent(this, AccellMeterService.class);
            Log.e(Util.TAG, "Requesting to bind service");
            accellMeterServiceBound = bindService(i, accellMeterServiceConnection, BIND_AUTO_CREATE);
            startService(i);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("be.ugent.csl.StepsIntent");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                steps = Util.get().getCurrentStepDetector().getSteps();
                run = Util.get().getCurrentStepDetector().getRun();
                total = Util.get().getCurrentStepDetector().getTotal();
                steps += run;
                String steps2 = String.valueOf(steps);
                if (steps < 1000) {
                    textView2.setText(steps2);
                    textView2.setTextSize(128);
                } else if (steps < 10000) {
                    textView2.setText(steps2.substring(0, steps2.length() - 3) + "," + steps2.substring(steps2.length() - 3));
                    textView2.setTextSize(110);
                } else {
                    textView2.setText(steps2.substring(0, steps2.length() - 3) + "," + steps2.substring(steps2.length() - 3));
                    textView2.setTextSize(92);
                }

                String total2 = String.valueOf(total);
                if (total < 1000) {
                    textView6.setText("Total " + total2);
                } else if (total < 1000000) {
                    textView6.setText("Total " + total2.substring(0, total2.length() - 3) + "," + total2.substring(total2.length() - 3));
                } else {
                    String textView6Text = "Total " + total2.substring(0, total2.length() - 6) + "," + total2.substring(total2.length() - 6, total2.length() - 3) + "," + total2.substring(total2.length() - 3);
                    textView6.setText(textView6Text);
                }

            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        Class fragmentClass;
        if (id == R.id.nav_about) {

        } else if (id == R.id.nav_exit) {
            finish();
        } else if (id == R.id.nav_target) {

        } else if (id == R.id.nav_length) {

        } else if (id == R.id.nav_weight) {

        } else if (id == R.id.nav_units) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static String anecdote() {
        String s;
        int number = (int) (0.5 + Math.random() * 40);
        switch(number) {
            case 0:
                s = "An error has occurred, our anecdote generator didn't work!";
                break;
            case 1:
                s = "An average person makes 6,000 steps a day!\nMake sure you have the right shoes,\nor you'll get hurt!";
                break;
            case 2:
                s = "There are 42.195 km in a marathon,\nthat are over 50,000 steps!";
                break;
            case 3:
                s = "It's recommended to make 14,000 steps,\nonly 30 % makes it till 10,000!";
                break;
            case 4:
                s = "A person with a sitting job will only make 2,000 steps on average!\nTime to move!";
                break;
            case 5:
                s = "We move on average 25 % less than 30 years ago!";
                break;
            case 6:
                s = "When you place your heel first when running, you're slowing yourself down.";
                break;
            case 7:
                s = "Even the Chinese leader Deng Xianping tracked his daily steps!";
                break;
            case 8:
                s = "A typical pair of tennis shoes will last 800 km of walking!\nIt’s probably a good idea to buy some!";
                break;
            case 9:
                s = "It takes on 1 hour and 43 minutes of walking to burn a 540-calorie Big Mac!\nBetter start moving!";
                break;
            case 10:
                s = "An average city block is equivalent to 200 steps.";
                break;
            case 11:
                s = "About 18 % of the world suffers from somnambulism or sleepwalking.\nDon’t forget your phone at night to be sure all your efforts are counted!";
                break;
            case 12:
                s = "In 1970, 66 % of children walked to school. Today, only 13 % walk.";
                break;
            case 13:
                s = "Walking reduces the risk of both breast and colon cancer.";
                break;
            case 14:
                s = "Walking on two feet is the thing that distinguishes us from most animals.";
                break;
            case 15:
                s = "One does not simply walk into Mordor.";
                break;
            case 16:
                s = "When someone says \" Go to hell! \",\nthat would take you almost 9 million steps!";
                break;
            case 17:
                s = "Walking to the moon requires over 500 million steps!";
                break;
            case 18:
                s = "To reach the top of the Burj Khalifa you need to climb a stairs containing 2909 steps,\nbetter take the lift!";
                break;
            case 19:
                s = "One lightyear (the distance light travels in one year) contains 12,614,266,670,000,000 steps (= 12 quadrillion steps)!";
                break;
            case 20:
                s = "The recommended body fat percentage for women is 20 - 21 %,\nfor men it's around 15 %.";
                break;
            case 21:
                s = "The longest walk around the world was completed by a former neon sign salesman,\nJean Beliveau.He walked 75,000 km through 64 counties!\n The trip took him 11 years.";
                break;
            case 22:
                s = "Racewalking has been an olympic sport for over 90 years!\nDistances go from 1.5 km untill 100 km.";
                break;
            case 23:
                s = "Given that the world is about 40,000 km in circumference and the average walking rate is 6 km an hour,\nit would take almost a year of nonstop walking to go around our planet!";
                break;
            case 24:
                s = "Humans became bipedal 3 to 6 million years ago.\nOur ancestors did this to better carry goods and use energy more efficient.";
                break;
            case 25:
                s = "Researchers note that that the human backbone was not designed to\nwork in the vertical position of walking on two legs.\nThis is why modern humans suffer from sore backs, slipped discs, arthritis and more!";
                break;
            case 26:
                s = "Researchers at the Université Catholique de\nLouvain showed that you can see if a woman has sex regularly\nby the way she walks!";
                break;
            case 27:
                s = "It's possible to distinguish straight man from homosexual men by the way they walk.\nHomosexuals often sway their hips.";
                break;
            case 28:
                s = "The firsts successful robot to walk had 6 legs!\nAs technology has improved, robots can now walk on 2 feet.\nStill, they don't walk as gracefully as human beings.";
                break;
            case 29:
                s = "Scientists believe that walking originated underwater by\n\"hopping\" air-breathing fish.";
                break;
            case 30:
                s = "Amish men take about 18,425 steps per day! Amish woman take about 14,196!\nthe average American adult takes just about 4,000!\nOne of the reasons there is 27 % less obese in the Amish population.";
                break;
            case 31:
                s = "To burn off one plain M&M candy,\na person would need to walk the entire length of a football field!";
                break;
            case 32:
                s = "Running and walking burns essentially the same amount of calories!\nRunning just gets a person from A to B a lot faster.";
                break;
            case 33:
                s = "Most babies begin to walk around 13 months,\nthrough some may start as early as 9 months and as late as 16 months!";
                break;
            case 34:
                s = "Walking is known as an ambulation.\nThe term \"walk\" stems from the old English word wealcan, or \"to roll\".";
                break;
            case 35:
                s = "Walking reduces the risk of heart attacks, type 2 diabetes and bone fractures!\nAdditionally, brisk walking can reduce stress and depression levels too.";
                break;
            case 36:
                s = "Mortality rates among retired men who walked less than 1.6 km a day,\nare almost twice as high as those who walked more than 3.2 km!";
                break;
            case 37:
                s = "Walking helps prevent osteoporosis.\nResearch show that postmenopausal women who walk around 1.6 km per day\nhave higher whole-body bone than those who don't!";
                break;
            case 38:
                s = "Experts not that when shopping for walking shoes,\nYou should always buy shoes that feel comfortable right away.\nThere is no breaking-in period.";
                break;
            case 39:
                s = "Feet swell during the day.\nIt's important to buy new shoes at the end of the day\nso they will fit best!";
                break;
            case 40:
                s = "The average walking speed for humans is about 6 km per hour!";
                break;
            default:
                s = "Anecdotes will be displayed here.";
        }
        return s;
    }

    //private void Notify(String title, String message) {
    //    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
}