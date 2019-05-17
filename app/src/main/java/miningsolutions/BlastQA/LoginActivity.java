package miningsolutions.BlastQA;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.Date;

import android.view.WindowManager;

public class LoginActivity extends AppCompatActivity {

    private AppData appData;
    private EditText firstName;
    private EditText lastName;
    private TextView date;
    private Button loginButton;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Set orientation to landscape
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // No autofocus on fields
        setContentView(R.layout.activity_login);

        appData = AppData.getAppData();

        this.renderLogin();
    }

    public void renderLogin() {

        firstName = (EditText) findViewById(R.id.FirstNameField);
        lastName = (EditText) findViewById(R.id.LastNameField);
        date = (TextView) findViewById(R.id.DateField);
        loginButton = (Button) findViewById(R.id.LoginButton);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                // Field Validation
                if( firstName.length() != 0 && lastName.length() != 0 /*&& date.length() != 0*/) {
                    // Set current user
                    User user = appData.createUser(firstName.getText().toString(),lastName.getText().toString());
                    appData.setCurrentUser(user);

                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    month = month + 1;
                    String date = month + "/" + day + "/" + year;
                    appData.setTimeStamp(date);

                    // Activity Result
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("userflag",true);
                    setResult(RESULT_OK,returnIntent); // Sets the result to be received by onActivityResult in the caller activity.
                    finish(); // Returns back to the calling activity.
                } else {
                    appData.Notify("Notification", "All fields are required.",LoginActivity.this);
                }

            }
        });

        /*
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(LoginActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDateSetListener,year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String dt = month + "/" + day + "/" + year;
                date.setText(dt);
            }
        };
        */

        firstName.addTextChangedListener(new TextValidator(firstName) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.toString().length() == 0) {
                    textView.setError("Field is required.");
                }
            }
        });

        lastName.addTextChangedListener(new TextValidator(lastName) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.toString().length() == 0) {
                    textView.setError("Field is required.");
                }
            }
        });

        /*
        date.addTextChangedListener(new TextValidator(date) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.toString().length() == 0) {
                    textView.setError("Field is required.");
                }
            }
        });
        */

    }

}
