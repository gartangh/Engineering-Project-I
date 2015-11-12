package androidintroductie.ugent.be.mockup;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;
    private EditText editText;
    private ProgressBar progressBar;
    private int target = 1000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // "Number of steps"
        textView = (TextView) findViewById(R.id.textView);
        // Shows number of steps taken today
        textView2 = (TextView) findViewById(R.id.textView2);
        // Shows average steps taken each day
        textView3 = (TextView) findViewById(R.id.textView3);
        // Displays an anecdote
        textView4 = (TextView) findViewById(R.id.textView4);
        // Shows target, reached or not, how many steps to take till target and lets the user edit the target
        textView5 = (TextView) findViewById(R.id.textView5);
        // Shows progress in percent
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // Shows total amount of steps since install
        textView6 = (TextView) findViewById(R.id.textView6);
        // Shows amount of km
        textView7 = (TextView) findViewById(R.id.textView7);
        // Shows amount of kCal burnt
        textView8 = (TextView) findViewById(R.id.textView8);
        // Shows percentage of progressbar
        textView9 = (TextView) findViewById(R.id.textView9);
        // Sets target.
        editText = (EditText) findViewById(R.id.editText);

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

        //int steps = 0;
        // Track a step
        /*
                step++;
                total++;
        */
        // For the moment
        final int steps = 1020;
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

        // Calculate km
        textView7.setText((int)(steps * 0.75) / 1000.000 + "km");
        // Calculate kCal
        textView8.setText((steps * 30) / 1000.0 + "kCal");

        // For the moment
        int total = 1234567;
        String total2 = String.valueOf(total);
        if (total < 1000) {
            textView6.setText("Total " + total2);
        } else if (total < 1000000) {
            textView6.setText("Total " + total2.substring(0, total2.length() - 3) + "," + total2.substring(total2.length() - 3));
        } else {
            String textView6Text = "Total " + total2.substring(0, total2.length() - 6) + "," + total2.substring(total2.length() - 6, total2.length() - 3) + "," + total2.substring(total2.length() - 3);
            textView6.setText(textView6Text);
        }

        // Links text with target
        int stepsComparedToTarget = target - steps;
        if (stepsComparedToTarget <= 0) {
            textView5.setText("Congratulations! You have reached your daily target of " + target + " steps!");
            progressBar.setProgress(100);
            // Notification (doesn't work jet)
            Notify("Steps++ Target Reached","Congratulations! You have reached your daily target of " + target + " steps!");
        } else {
            textView5.setText("Your target is " + target + " steps, still " + (target - steps) + " to go! Press on the inputfield to edit your goal. (Your progress is shown in the pink bar below.)");
            progressBar.setProgress((int) ((steps * 100.0) / target));
        }

        // Target >100 || target < 100000)
        editText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        int input = Integer.parseInt(editText.getText().toString());
                        if (input <= 100000 && input >= 100) {
                            target = input;
                            editText.setText(String.valueOf(target));
                            int stepsComparedToTarget = target - steps;
                            if (stepsComparedToTarget <= 0) {
                                textView5.setText("Congratulations! You have reached your daily target of " + target + " steps!");
                                progressBar.setProgress(100);
                                // Notification (doesn't work jet)
                                Notify("Steps++ Target Reached","Congratulations! You have reached your daily target of " + target + " steps!");
                            } else {
                                textView5.setText("Your target is " + target + " steps, still " + (target - steps) + " to go!\nPress on the inputfield to edit your goal.\n(Your progress is shown in the pink bar below.)");
                                progressBar.setProgress((int) ((steps * 100.0) / target));
                            }
                        }
                        return false;
                    }
                }
        );

        // Make a notificationbuilder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        // Set percentage of progressbar
        textView9.setText(progressBar.getProgress() + " %");

        editText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText("");
                    }
                }
        );

        //int days = 0;
        // When 00:00 o'clock;
        /*
        steps = 0;
        days++;
         */
        // For the moment
        int days = 356;
        int average = (int)(total * 1.0 / days);
        String average2 = String.valueOf(average);
        if (average < 1000) {
            textView3.setText("Average " + average2);
        } else if (average < 1000000){
            textView3.setText("Average " + average2.substring(0, average2.length() - 3) + "," + average2.substring(average2.length() - 3));
        } else {
            textView3.setText("Average " + average2.substring(0, average2.length() - 6) + "," + average2.substring(average2.length() - 6, average2.length() - 3) + "," + average2.substring(average2.length()- 3));
        }
    }

    private static String anecdote() {
        int number = (int) (0.5 + Math.random() * 19);
        String s;
        switch(number) {
            case 0:
                s = "An error has occurred, our anecdote generator didn't work!";
                break;
            case 1:
                s = "An average person makes 4,321 steps a day! Make sure you have the right shoes, or you'll get hurt!";
                break;
            case 2:
                s = "There are 42,195 km in a marathon, that are over 50,000 steps!";
                break;
            case 3:
                s = "It's recommended to make 14,000, only 30 % makes it till 10,000!";
                break;
            case 4:
                s = "A person with a sitting job will only make 2,000 steps on average! Time to move!";
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
                s = "A typical pair of tennis shoes will last 500 miles of walking, it’s probably a good idea to buy some!";
                break;
            case 9:
                s = "It would take, on average, 1 hour and 43 minutes of walking to burn off a 540-calorie Big Mac, so start moving!";
                break;
            case 10:
                s = "An average city block is equivalent to 200 steps";
                break;
            case 11:
                s = "Sleepwalking is called somnambulism. About 18% of the world suffers from somnambulism. So, don’t forget your phone at night to be sure all your efforts are counted!";
                break;
            case 12:
                s = "In 1970, 66% of children walked to school. Today, only 13 % walk.";
                break;
            case 13:
                s = "Walking reduces the risk of both breast and colon cancer.";
                break;
            case 14:
                s = "Walking on two feet is the thing that distinguishes us from animals.";
                break;
            case 15:
                s = "One does not simply walk into Mordor.";
                break;
            case 16:
                s = "When someone says \" Go to hell! \", that would take you almost 9 million steps!";
                break;
            case 17:
                s = "Walking to the moon requires over 500 million steps!";
                break;
            case 18:
                s = "To reach the top of the Burj Khalifa you need to climb a stairs containing 2909 steps, better take the lift!";
                break;
            case 19:
                s = "One lightyear (the distance light travels in one year) contains 12,614,266,670,000,000 steps (= 12 trillion steps)!";
            default:
                s = "anecdotes will be displayed here.";
        }
        return s;
    }

    private void Notify(String title, String message) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
}