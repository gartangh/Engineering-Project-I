package androidintroductie.ugent.be.androidintroductie5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private EditText naamInvoer;
    private TextView halloNaam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        naamInvoer = (EditText) findViewById(R.id.naamInvoer);
        halloNaam = (TextView) findViewById(R.id.halloNaam);

        halloNaam.setText("Hello!");

        naamInvoer.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        halloNaam.setText("Hello " + naamInvoer.getText() + "!");
                        return false;
                    }
                }
        );
    }
}