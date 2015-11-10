package androidintroductie.ugent.be.mockup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // "Number of steps"
        textView = (TextView) findViewById(R.id.textView);
        // steps = number of steps taken today
        // reset when 00:00;
        // Shows number of steps taken today
        textView2 = (TextView) findViewById(R.id.textView2);
        final int steps = 101;
        // Shows average steps taken each day
        textView3 = (TextView) findViewById(R.id.textView3);
        // average = total/days
        // Displays an anecdote
        textView4 = (TextView) findViewById(R.id.textView4);
        // Shows target, reached or not, how many steps to take till target and lets the user edit the target
        textView5 = (TextView) findViewById(R.id.textView5);
        // Shows progress in percent
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // Shows total ammount of steps since install
        textView6 = (TextView) findViewById(R.id.textView6);
        // textView6.setText(textView6.getText() + dayDteps);
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

        textView4.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textView4.setText(anecdote());
                    }
                }
        );

        editText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText("");
                    }
                }
        );
        // final target = (int)editText.getText();
        /*while (target <100 || target > 100000) {
            final target = (int)editText.getText();
          }*/
        // For the moment:
        final int target = 500;
        editText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        String targetString = String.valueOf(target);
                        editText.setText(targetString);
                        int stepsComparedToTarget = target - steps;
                        if (stepsComparedToTarget <= 0) {
                            textView5.setText("Congratulations! You have reached your daily target of " + target + " steps!");
                            progressBar.setProgress(100);
                        } else {
                            textView5.setText("Your target is " + target + " steps, still " + (target - steps) + " to go! Press on the inputfield to edit your goal.");
                            progressBar.setProgress((int) ((steps * 100.0) / target));
                        }
                        return false;
                    }
                }
        );
    }

    public static String anecdote() {
        int number = (int) (0.5 + Math.random() * 6);
        String s;
        switch(number) {
            case 0:
                s = "Even the Chinese leader Deng Xianping tracked his daily steps!";
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
                s = "An error has occurred, our anecdote generator didn't work!";
                break;
            default:
                s = "anecdotes will be displayed here.";
        }
        return s;
    }
}