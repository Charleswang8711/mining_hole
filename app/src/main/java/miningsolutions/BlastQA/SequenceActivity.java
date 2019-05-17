package miningsolutions.BlastQA;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import miningsolutions.BlastQA.Settings.SettingsData;

public class SequenceActivity extends AppCompatActivity {

    AppData appData;
    ArrayList<Hole> holes;
    ArrayList<Hole> allHoles;

    String filename;
    double surfaceRL;
    double targetRL;
    double subDrill;
    double depthTolerance;

    TextView holeId;
    TextView designDiameter;
    EditText actualDiameter;
    TextView designDepth;
    EditText actualDepth;
    TextView designDipAngle;
    EditText actualDipAngle;
    EditText collarPipe;
    TextView redrillRequired;
    Button validationColor;
    EditText importantNotes;

    int currentIndex;
    Button moreButton;
    Button nextButton;
    Button previousButton;
    Button returnMapButton;
    Button quickMapButton;

    List<SettingsData> settingsDataList;

    Button noHoleButton;
    Button brokenGroundButton;
    Button wetHoleButton;
    Button overrideRedrillButton;

    TableLayout quickButtonsTable;
    List<Button> quickButtons = new ArrayList<>();
    TableRow row1;
    TableRow row2;
    TableRow row3;
    TableRow row4;
    TableRow row5;

    TextView seqCurrent;
    TextView seqTo;

    FragmentManager fm;
    HoleDiagramFragment holeDiagramFragment;
    MoreFragment moreFragment;

    QuickMapFragment quickMapFragment;
    double maximumNorthing;
    double maximumEasting;
    double minimumNorthing;
    double minimumEasting;

    private final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 314;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Set orientation to landscape
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // No autofocus on fields

        setContentView(R.layout.activity_sequence);

        appData = AppData.getAppData();

        this.init();
    }

    public void init() {

        // Initialize layout elements
        holeId = (TextView) findViewById(R.id.IDValue);
        designDiameter = (TextView) findViewById(R.id.DesignDiameterValue);
        actualDiameter = (EditText) findViewById(R.id.ActualDiameterValue);
        designDepth = (TextView) findViewById(R.id.DesignDepthValue);
        actualDepth = (EditText) findViewById(R.id.ActualDepthValue);
        designDipAngle = (TextView) findViewById(R.id.DesignDipAngleValue);
        actualDipAngle = (EditText) findViewById(R.id.ActualDipAngleValue);
        collarPipe = (EditText) findViewById(R.id.CollarPipeValue);
        redrillRequired = (TextView) findViewById(R.id.RedrillRequiredValue);
        validationColor = (Button) findViewById(R.id.ValidationColor);
        importantNotes = (EditText) findViewById(R.id.ImportantNotesValue);

        moreButton = (Button) findViewById(R.id.MoreButton);
        nextButton = (Button) findViewById(R.id.NextButton);
        previousButton = (Button) findViewById(R.id.PreviousButton);
        returnMapButton = (Button) findViewById(R.id.ReturnMapButton);
        quickMapButton = (Button) findViewById(R.id.QuickMapButton);

        noHoleButton = (Button) findViewById(R.id.NoHoleButton);
        wetHoleButton = (Button) findViewById(R.id.WetHoleButton);
        brokenGroundButton = (Button) findViewById(R.id.BrokenGroundButton);
        overrideRedrillButton = (Button) findViewById(R.id.OverrideRedrill);

        quickButtonsTable = (TableLayout) findViewById(R.id.QuickButtons);
        row1 = (TableRow) findViewById(R.id.Row1);
        row2 = (TableRow) findViewById(R.id.Row2);
        row3 = (TableRow) findViewById(R.id.Row3);
        row4 = (TableRow) findViewById(R.id.Row4);
        row5 = (TableRow) findViewById(R.id.Row5);
        this.initQuickButtons();

        // Get passed holes list from caller activity
        Intent intent = getIntent();
        holes = (ArrayList<Hole>) intent.getSerializableExtra("holesList");
        filename = intent.getStringExtra("sitefile");
        surfaceRL = intent.getDoubleExtra("surfaceRL", 0.0);
        targetRL = intent.getDoubleExtra("targetRL", 0.0);
        subDrill = intent.getDoubleExtra("subDrill", 0.0);
        depthTolerance = intent.getDoubleExtra("depthTolerance",0.0);

        allHoles = (ArrayList<Hole>) intent.getSerializableExtra("quickMapList");
        minimumEasting = intent.getDoubleExtra("minimumEasting", 0.0);
        maximumEasting = intent.getDoubleExtra("maximumEasting", 0.0);
        minimumNorthing = intent.getDoubleExtra("minimumNorthing", 0.0);
        maximumNorthing = intent.getDoubleExtra("maximumNorthing", 0.0);

        // Initialize Hole Diagram fragment
        fm = getSupportFragmentManager();
        holeDiagramFragment = (HoleDiagramFragment) fm.findFragmentById(R.id.HoleDiagram);
        moreFragment = new MoreFragment();
        quickMapFragment = new QuickMapFragment();

        Bundle quickMapBundle = new Bundle();
        quickMapBundle.putSerializable("quickMapList", allHoles);
        quickMapBundle.putDouble("minimumEasting", minimumEasting);
        quickMapBundle.putDouble("maximumEasting", maximumEasting);
        quickMapBundle.putDouble("minimumNorthing", minimumNorthing);
        quickMapBundle.putDouble("maximumNorthing", maximumNorthing);
        quickMapFragment.setArguments(quickMapBundle);

        // Display starting hole from sequence
        currentIndex = 0;
        this.displayHole(holes.get(currentIndex));

        seqCurrent = (TextView) findViewById(R.id.SequenceCurrentId);
        seqTo = (TextView) findViewById(R.id.SequenceTo);

        seqCurrent.setText(String.valueOf(currentIndex+1));
        seqTo.setText(String.valueOf(holes.size()));

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreFragment.show(fm, "fragment_more");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Save data in .xlsx file here
                int writePermission = ActivityCompat.checkSelfPermission(appData.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (writePermission != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            SequenceActivity.this,
                            appData.getPERMISSIONS_STORAGE(),
                            WRITE_STORAGE_PERMISSION_REQUEST_CODE
                    );
                } else {
                    saveToFile(holes.get(currentIndex));
                }

                if(currentIndex != holes.size()-1) {
                    currentIndex = currentIndex + 1;
                    seqCurrent.setText(String.valueOf(currentIndex+1));
                    SequenceActivity.this.displayHole(holes.get(currentIndex));
                } else {
                    appData.Notify("Notification","Reached end of sequence.",SequenceActivity.this);
                }

            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentIndex != 0) {
                    currentIndex = currentIndex - 1;
                    seqCurrent.setText(String.valueOf(currentIndex+1));
                    SequenceActivity.this.displayHole(holes.get(currentIndex));
                } else {
                    appData.Notify("Notification","Reached start of sequence.",SequenceActivity.this);
                }

            }
        });

        returnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Return holes
                Intent returnIntent = new Intent();
                returnIntent.putExtra("updatedHoles",holes);
                setResult(RESULT_OK,returnIntent); // Sets the result to be received by onActivityResult in the caller activity.
                finish(); // Returns back to the calling activity.

            }
        });

        quickMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quickMapFragment.show(fm, "fragment_quickmap");
            }
        });

        overrideRedrillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Hole hole = holes.get(currentIndex);

                if(hole.getOverrideRedrill()) {

                    hole.setOverrideRedrill(false);
                    overrideRedrillButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

                    DecimalFormat decimalFormat = new DecimalFormat("###.##");

                    double roundedDepthValue = Double.valueOf(decimalFormat.format(getFieldValueWithCheck(actualDepth.getText().toString())));
                    double differenceValue = Double.valueOf(decimalFormat.format(Math.abs(hole.getDesignDepth() - roundedDepthValue)));

                    if (differenceValue <= depthTolerance) {
                        redrillRequired.setText("false");
                        redrillRequired.setTextColor(Color.GREEN);
                        validationColor.setBackgroundColor(Color.GREEN);
                    } else {
                        redrillRequired.setText("true");
                        redrillRequired.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
                        validationColor.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                    }

                } else {

                    hole.setOverrideRedrill(true);
                    overrideRedrillButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));

                    redrillRequired.setText("true");
                    redrillRequired.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
                    validationColor.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));

                }
            }

        });

        actualDepth.addTextChangedListener(new TextValidator(actualDepth) {
            @Override
            public void validate(TextView textView, String text) {

                Hole currentHole = holes.get(currentIndex);

                // The difference between design Depth and the value in the actual depth field
                if(!actualDepth.getText().toString().isEmpty()) {

                    if(currentHole.getOverrideRedrill()) { // We check if redrill override is true, then we set redrill required to true.

                        redrillRequired.setText("true");
                        redrillRequired.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
                        validationColor.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));

                    } else { // Do normally

                        DecimalFormat decimalFormat = new DecimalFormat("###.##");

                        double roundedDepthValue = Double.valueOf(decimalFormat.format(getFieldValueWithCheck(actualDepth.getText().toString())));
                        double differenceValue = Double.valueOf(decimalFormat.format(Math.abs(currentHole.getDesignDepth() - roundedDepthValue)));

                        if (differenceValue <= depthTolerance) {
                            redrillRequired.setText("false");
                            redrillRequired.setTextColor(Color.GREEN);
                            validationColor.setBackgroundColor(Color.GREEN);
                        } else {
                            redrillRequired.setText("true");
                            redrillRequired.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
                            validationColor.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                        }

                    }

                }

            }
        });

    }

    public void displayHole(Hole currentHole) {

        holeId.setText(String.valueOf(currentHole.getId()));
        designDiameter.setText(String.valueOf(currentHole.getDesignDiameter()));
        designDepth.setText(String.valueOf(currentHole.getDesignDepth()));
        designDipAngle.setText(String.valueOf(currentHole.getDesignDipAngle()));

        // Default value of actual depth is set to blank
        actualDepth.setText(getHoleValueWithCheck(currentHole.getActualDepth(), null));

        // Double default values of 0.0 is set as design value.
        actualDiameter.setText(getHoleValueWithCheck(currentHole.getActualDiameter(), currentHole.getDesignDiameter()));
        actualDipAngle.setText(getHoleValueWithCheck(currentHole.getActualDipAngle(), currentHole.getDesignDipAngle()));
        collarPipe.setText(getHoleValueWithCheck(currentHole.getCollarPipe(), 0.0));

        // String values
        importantNotes.setText(currentHole.getImportantNotes());

        double differenceValue = Math.abs(currentHole.getDesignDepth() - currentHole.getActualDepth());

        // We check if redrill override is true, then we set redrill required to true.
        // Needs placing the button besides the required redrill field
        if(currentHole.getOverrideRedrill()) {

            redrillRequired.setText("true");
            redrillRequired.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
            validationColor.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));

        } else { // Do normally

            if (differenceValue <= depthTolerance) {
                redrillRequired.setText("false");
                redrillRequired.setTextColor(Color.GREEN);
                validationColor.setBackgroundColor(Color.GREEN);
            } else {
                redrillRequired.setText("true");
                redrillRequired.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
                validationColor.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            }

        }

        if(currentHole.getOverrideRedrill()) {
            overrideRedrillButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else {
            overrideRedrillButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        }

        for(Button button: quickButtons) { // We refresh the state of each of the quick buttons according to the value saved.

            if(currentHole.getCustomData(button.getText().toString()).equals("true")) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            } else {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            }

        }

        // Update hole diagram fragment
        holeDiagramFragment.updateDiagram(surfaceRL,targetRL,currentHole.getDesignStemmingColumn(),subDrill);

        // Send bundle to more fragment
        Bundle moreBundle = new Bundle();
        moreBundle.putSerializable("CurrentHole",currentHole);
        moreBundle.putString("filename",filename);
        moreFragment.setArguments(moreBundle);

    }

    public void saveToFile(Hole hole) {

        try {

            String path = Environment.getExternalStorageDirectory().toString()+ appData.getPath() + filename;
            File file = new File(path);

            //Create Workbook instance holding reference to .xlsx file
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fileInputStream);

            //Get first/desired sheet from the workbook
            Sheet sheet = workbook.getSheetAt(1); // First Sheet on the tab

            CellStyle cellStyle = appData.getBorderedStyle(workbook);

            // Update hole objects from field values
            hole.setActualDepth(getFieldValueWithCheck(actualDepth.getText().toString()));
            hole.setActualDiameter(getFieldValueWithCheck(actualDiameter.getText().toString()));
            hole.setActualDipAngle(getFieldValueWithCheck(actualDipAngle.getText().toString()));
            hole.setCollarPipe(getFieldValueWithCheck(collarPipe.getText().toString()));
            hole.setImportantNotes(importantNotes.getText().toString());

            if(hole.getOverrideRedrill() == true) {
                hole.setRedrillRequired("true");
            } else {
                hole.setRedrillRequired(redrillRequired.getText().toString());
            }

            // Direct access via stored row number in hole object
            int row = hole.getRowNumber();

            // In here we skip write process if value is 0.0 (double's null value) to make things faster instead of checking the values.
            if(hole.getActualDepth() != 0.0) {
                sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DEPTH).setCellStyle(cellStyle);
                sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DEPTH).setCellValue(hole.getActualDepth());
            }

            if(hole.getActualDiameter() != 0.0) {
                sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DIAMETER).setCellStyle(cellStyle);
                sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DIAMETER).setCellValue(hole.getActualDiameter());

            }

            if(hole.getActualDipAngle() != 0.0) {
                sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DIP_ANGLE).setCellStyle(cellStyle);
                sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DIP_ANGLE).setCellValue(hole.getActualDipAngle());
            }

            if(hole.getCollarPipe() != 0.0) {
                sheet.getRow(row).getCell(Hole.COLUMN_COLLAR_PIPE).setCellStyle(cellStyle);
                sheet.getRow(row).getCell(Hole.COLUMN_COLLAR_PIPE).setCellValue(hole.getCollarPipe());
            }

            sheet.getRow(row).getCell(Hole.COLUMN_REDRILL_REQUIRED).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_REDRILL_REQUIRED).setCellValue(hole.getRedrillRequired());

            sheet.getRow(row).getCell(Hole.COLUMN_IMPORTANT_NOTES).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_IMPORTANT_NOTES).setCellValue(hole.getImportantNotes());

            if(hole.getOverrideRedrill() == true) {
                sheet.getRow(row).getCell(18).setCellValue("Overridden");
            }

            int customDataColumn = 19; // Start is column 19

            // Settings Custom Data
            for(SettingsData settingsData: appData.getSettingsList()) {
                if(settingsData.getDatatype().equals("Boolean")) {

                    Cell stringCell = sheet.getRow(row).createCell(customDataColumn);
                    stringCell.setCellType(Cell.CELL_TYPE_STRING);
                    stringCell.setCellStyle(cellStyle);

                    stringCell.setCellValue((String)hole.getCustomData(settingsData.getName()));

                }
                customDataColumn = customDataColumn + 1;
            }

            // We close file stream before opening a file output stream. Can't have both stream opened at the same file.
            fileInputStream.close();

            // Open file output stream for writing to the file.
            FileOutputStream fileOutput = new FileOutputStream(file);
            workbook.write(fileOutput);
            fileOutput.close();
            workbook.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String readStringWithCheck(Cell cell) {

        String cellValue = "";

        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_STRING: cellValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC: cellValue = String.valueOf(Math.round(cell.getNumericCellValue()));
                break;
            case Cell.CELL_TYPE_FORMULA: cellValue = String.valueOf(Math.round(cell.getNumericCellValue()));
                break;
        }

        return cellValue;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case WRITE_STORAGE_PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    saveToFile(holes.get(currentIndex));
                }
                else
                {
                    System.exit(0);
                }

                break;
        }
    }

    public String getHoleValueWithCheck(Double value, Double defaultValue) {
        if(value == 0.0) {
            if(defaultValue != null) {
                return defaultValue.toString();
            } else {
                return null;
            }
        } else {
            return value.toString();
        }
    }

    public double getFieldValueWithCheck(String value) {
        if(value.equals("")) {
            return 0.0;
        } else {
            return Double.valueOf(value);
        }
    }

    public void initQuickButtons() {

        settingsDataList = appData.getSettingsList();

        for(int i = 0; i<=4; i++) {

            if(settingsDataList.size() != 0 && i <= settingsDataList.size()-1) {

                SettingsData settingsData = settingsDataList.get(i);

                if(settingsData != null && settingsDataList.get(i).getDatatype() == "Boolean") {

                    System.out.println("Last i: "+ i + " " + quickButtonsTable.getChildCount());

                    // Create widgets based on settingsData
                    Button button = new Button(getApplicationContext());

                    button.setTag("quickBtn-"+i);
                    button.setText(settingsDataList.get(i).getName());
                    button.setMinWidth(90);
                    button.setMaxWidth(90);
                    button.setMinHeight(150);
                    button.setMaxHeight(150);
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Hole currentHole = holes.get(currentIndex);

                            if(currentHole.getCustomData(settingsData.getName()).equals("true")) {
                                currentHole.setCustomData(settingsData.getName(),"false");
                                button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                            } else {
                                currentHole.setCustomData(settingsData.getName(),"true");
                                button.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            }
                        }
                    });

                    switch(i) {
                        case 0: row1.addView(button);
                            break;
                        case 1: row2.addView(button);
                            break;
                        case 2: row3.addView(button);
                            break;
                        case 3: row4.addView(button);
                            break;
                        case 4: row5.addView(button);
                            break;
                    }

                    quickButtons.add(button);

                }

            }

        }

    }

    public void onMoreFragmentSave() {

        for(Button button: quickButtons) { // We refresh the state of each of the quick buttons according to the value saved.

            Hole currentHole = holes.get(currentIndex);

            if(currentHole.getCustomData(button.getText().toString()).equals("true")) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            } else {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            }
        }

    }

}
