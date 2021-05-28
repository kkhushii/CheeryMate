package hackOn2021.cheerymate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView AppName;
    private Button signin;
    private Button login;
    EditText username,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppName = findViewById(R.id.Appname);

        username = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        final String user = username.getText().toString().trim();
        final String password = pass.getText().toString().trim();

        signin = findViewById(R.id.gsignin);
        login = findViewById(R.id.login);


    }
}