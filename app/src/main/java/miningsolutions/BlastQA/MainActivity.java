package miningsolutions.BlastQA;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TableRow;
import android.os.Environment;
import java.io.File;
import java.text.SimpleDateFormat;

import miningsolutions.BlastQA.Settings.SettingsFragment;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    AppData appData; // Application Data Model
    SimpleDateFormat sdf; // Timestamp Format
    AlertDialog.Builder alertDialogBuilder; // Notifications Dialog Builder

    TextView nameText;
    TextView stampText;
    Button settingsButton;

    int REQUEST_CODE_LOGIN = 1;
    int REQUEST_CODE_SELECTLOCATION = 201;
    private final int READ_STORAGE_PERMISSION_REQUEST_CODE = 101;

    FragmentManager fm;
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Set orientation to landscape
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // No autofocus on fields
        setContentView(R.layout.activity_main);

        this.init(); // Check for current user and initialize app
    }

    public void init() {

        appData = new AppData(this.getApplicationContext(),this);

        alertDialogBuilder = new AlertDialog.Builder(this);

        sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        // Check if current user is set, if not call Login activity
        if(appData.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LOGIN);
        }

    }

    public void displaySites() {

        TableRow TR1 = (TableRow) findViewById(R.id.TR1);
        TR1.removeAllViews();

        User currentUser = appData.getCurrentUser();

        TextView nameText = (TextView) findViewById(R.id.NameText);
        TextView stampText = (TextView) findViewById(R.id.DatedText);
        settingsButton = (Button) findViewById(R.id.SettingsButton);

        nameText.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        stampText.setText(appData.getTimeStamp());

        fm = getSupportFragmentManager();

        settingsFragment = new SettingsFragment();

        settingsFragment.setCancelable(false);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsFragment.show(fm, "fragment_settings");
            }
        });

        // Find all files in default device storage location DCIM/Sites folder or custom location from settings
        String path = Environment.getExternalStorageDirectory().toString()+ appData.getPath();

        System.out.println("Retrieving Blast Patterns... Files Path: " + path);

        File directory = new File(path);

        try {
            File[] files;
            files = directory.listFiles();
            int fragCount = 1;

            for (int i = 0; i < files.length; i++) { // Site listing logic

                Uri fileUri = Uri.fromFile(files[i]);

                if(MimeTypeMap.getFileExtensionFromUrl(fileUri.toString()).equals("xlsx")) { // Excel .xlsx file only

                    // Adding a site fragment to a table row
                    BlastPatternFragment site1 = new BlastPatternFragment();
                    Bundle args = new Bundle();
                    args.putString("SiteName", files[i].getName());
                    args.putString("SiteLastModifiedDate", sdf.format(files[i].lastModified()));
                    site1.setArguments(args);

                    if (fragCount <= 4) {
                        fm.beginTransaction().add(R.id.TR1, site1).commitAllowingStateLoss();
                    } else if (fragCount <= 8) {
                        fm.beginTransaction().add(R.id.TR2, site1).commitAllowingStateLoss();
                    } else {
                        appData.Notify("Notification", "Warning: One or many site files may not have been displayed. Maximum allowable sites reached. Please delete files in DCIM/BlastPatterns (Max of 8)", MainActivity.this);
                    }

                    fragCount = fragCount + 1; // Stop, think again. You don't want that List<>. Keep it simple. - DAA

                }
            }
        }
        catch(Exception e) {
            appData.Notify("Notification","No BlastPatterns folder detected. Please create the BlastPatterns folder inside your device's DCIM folder. Restart application and try again.", MainActivity.this);
        }

    }

    // Once the OS returns to this activity from login activity (REQUEST_CODE_LOGIN = 1)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnData) {

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_LOGIN) {

            boolean currentUserSetFlag = returnData.getBooleanExtra("userflag",true);

            if(currentUserSetFlag == true) {

                System.out.println("User has logged in. Name: " + appData.getCurrentUser().getFirstName());

                int readPermission = ActivityCompat.checkSelfPermission(appData.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (readPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            appData.getPERMISSIONS_STORAGE(),
                            READ_STORAGE_PERMISSION_REQUEST_CODE
                    );
                } else {
                    this.displaySites();
                }

            }

        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECTLOCATION) { // Pass it to the settings fragment

            settingsFragment.updatePatternsLocation(this,returnData);

        }

    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case READ_STORAGE_PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    this.displaySites();
                }
                else
                {
                    System.exit(0);
                }

                break;
        }
    }

    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

}
