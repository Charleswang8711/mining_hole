package miningsolutions.BlastQA;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.OnDataPointTouchListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import miningsolutions.BlastQA.Settings.SettingsData;

public class QAQCActivity extends AppCompatActivity {

    GraphView graph;
    AppData appData;
    String filename;

    int maxWidth; // In pixels
    int maxHeight; // In pixels

    Map<String, Hole> holes;
    double fileSurfaceRL;
    double fileTargetRL;
    double fileSubDrill;
    double fileDepthTolerance;

    PointsGraphSeries<DataPoint> series;
    double maximumNorthing;
    double maximumEasting;
    double minimumNorthing;
    double minimumEasting;

    ImageView startButton;
    Button showParametersButton;
    Button clearSelectionButton;
    Button saveAndExitButton;
    Button addHoleButton;
    boolean addHoleFlag = false;

    int REQUEST_CODE_SEQUENCE = 3;

    FragmentManager fm;
    DisplayParametersFragment displayParametersFragment;
    AddHoleFragment addHoleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Set orientation to landscape
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // No autofocus on fields

        setContentView(R.layout.activity_qaqc);

        appData = AppData.getAppData();

        graph = (GraphView) findViewById(R.id.GraphView);

        maxWidth = graph.getWidth();
        maxHeight = graph.getHeight();

        Intent intent = getIntent();
        filename = intent.getStringExtra("filename");

        // Set Map Sorting before filling up the map with objects
        holes = new TreeMap<String, Hole>(
                new Comparator<String>() {
                    @Override public int compare(String p1, String p2) { // Ascending order 1 to N

                        // Remove whitespaces
                        p1 = p1.trim();
                        p2 = p2.trim();

                        // Strip ids out of initial letters
                        if(p1.charAt(0) == 'x' || p1.charAt(0) == 'n') {
                            p1 = p1.substring(1);
                        }

                        if(p2.charAt(0) == 'x' || p2.charAt(0) == 'n') {
                            p2 = p2.substring(1);
                        }

                        return Integer.valueOf(p1) - Integer.valueOf(p2);

                    }
                }
        );

        String path = Environment.getExternalStorageDirectory().toString()+ appData.getPath() + filename;
        File file = new File(path);

        boolean result = parseDataFile(file);

        if(result == true) {

            Hole[] holesArr = new Hole[holes.size()];
            holesArr = holes.values().toArray(holesArr);
            this.sortArray(holesArr);

            series = new PointsGraphSeries<>(holesArr);

            graph.addSeries(series);

            // Initialize Display Parameters
            fm = getSupportFragmentManager();
            displayParametersFragment = new DisplayParametersFragment();
            addHoleFragment = new AddHoleFragment();

            series.toggleCustomTopLabel("id",true);
            series.toggleCustomBottomLabel("depth",true);

            // Default Parameters
            Bundle parametersBundle = new Bundle();
            parametersBundle.putBoolean("id", true);
            parametersBundle.putBoolean("depth", true);
            parametersBundle.putBoolean("charge", false);
            parametersBundle.putBoolean("stem", false);
            displayParametersFragment.setArguments(parametersBundle);

            int roundedMinimumEasting = getRoundedCoordinate(minimumEasting);
            int roundedMaximumEasting = getRoundedCoordinate(maximumEasting);
            int roundedMinimumNorthing = getRoundedCoordinate(minimumNorthing);
            int roundedMaximumNorthing = getRoundedCoordinate(maximumNorthing);

            graph.getViewport().setMinX(roundedMinimumEasting - 2);
            graph.getViewport().setMaxX(roundedMaximumEasting + 10);

            graph.getViewport().setMinimalViewport(roundedMinimumEasting - 2, roundedMaximumEasting + 10, roundedMinimumNorthing, roundedMaximumNorthing);

            graph.getGridLabelRenderer().setNumHorizontalLabels(8);
            graph.getGridLabelRenderer().setNumVerticalLabels(6);

            graph.getViewport().setScrollable(true);
            graph.getViewport().setScrollableY(true);
            graph.getViewport().setScalable(true);
            graph.getViewport().setScalableY(false);

            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint, double x, double y) {

                    if(QAQCActivity.this.addHoleFlag) {
                        QAQCActivity.this.addHoleFlag = false;
                        QAQCActivity.this.addHoleButton.setText("Add Hole");

                        Bundle bee = new Bundle();
                        bee.putDouble("x",x);
                        bee.putDouble("y",y);
                        bee.putSerializable("holesMap",(TreeMap)holes);

                        addHoleFragment.setArguments(bee);
                        addHoleFragment.show(fm, "fragment_addhole");
                    } else {
                        if (dataPoint != null) {
                            Hole hole = (Hole) dataPoint;
                            manageHole(hole);
                        }
                    }

                }

            });

            series.setOnDataPointTouchListener(new OnDataPointTouchListener() {
                @Override
                public void onTouch(Series series, DataPointInterface dataPoint) {
                    Hole touchedPoint = (Hole) dataPoint;

                    if(!QAQCActivity.this.getSeries().getSelectionList().contains(touchedPoint)) {
                        QAQCActivity.this.getSeries().addSelection(touchedPoint);
                        touchedPoint.setColor(Color.RED);
                    }

                    QAQCActivity.this.getSeries().toggleLinePoints(true); // enables drawing lines on selected points
                    QAQCActivity.this.getSeries().toggleSequenceLabel(true);
                    QAQCActivity.this.getSeries().redraw();
                }
            });

        }

        startButton = (ImageView) findViewById(R.id.StartButton);
        showParametersButton = (Button) findViewById(R.id.ShowParametersButton);
        clearSelectionButton = (Button) findViewById(R.id.ClearSelectionButton);
        saveAndExitButton = (Button)findViewById(R.id.ExitSaveButton);
        addHoleButton = (Button) findViewById(R.id.AddHoleButton);

        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        startButton.setBackgroundResource(R.drawable.start_button_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) startButton.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // New intent passing the holes list to sequence activity
                Intent intent = new Intent(QAQCActivity.this,SequenceActivity.class);
                intent.putExtra("sitefile", filename);
                intent.putExtra("surfaceRL", fileSurfaceRL);
                intent.putExtra("targetRL", fileTargetRL);
                intent.putExtra("subDrill", fileSubDrill);
                intent.putExtra("depthTolerance", fileDepthTolerance);

                ArrayList<Object> holesList = new ArrayList<>(Arrays.asList(holes.values().toArray()));

                if(QAQCActivity.this.getSeries().getSelectionList().size() == 0) {
                    intent.putExtra("holesList", holesList);
                } else {
                    intent.putExtra("holesList", QAQCActivity.this.getSeries().getSelectionList());
                }

                intent.putExtra("quickMapList", holesList);
                intent.putExtra("minimumEasting", minimumEasting);
                intent.putExtra("maximumEasting", maximumEasting);
                intent.putExtra("minimumNorthing", minimumNorthing);
                intent.putExtra("maximumNorthing", maximumNorthing);

                startActivityForResult(intent,REQUEST_CODE_SEQUENCE);

            }
        });

        showParametersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayParameters();
            }
        });

        clearSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<DataPoint> selectedPoints = QAQCActivity.this.getSeries().getSelectionList();

                for(DataPoint point: selectedPoints) {
                    QAQCActivity.this.setHoleColorValidation((Hole)point);
                }

                QAQCActivity.this.getSeries().getSelectionList().clear();

                QAQCActivity.this.getSeries().redraw();
            }
        });

        addHoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!addHoleFlag) {
                    ((Button) view).setText("Cancel");
                    addHoleFlag = true;
                    appData.Notify("Notification","Tap a point in the area.",QAQCActivity.this);
                } else {
                    ((Button) view).setText("Add Hole");
                    addHoleFlag = false;
                }

            }
        });

        saveAndExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean operationError = false;

                try {

                    String path = Environment.getExternalStorageDirectory().toString()+ appData.getPath() + filename;
                    File file = new File(path);

                    //Create Workbook instance holding reference to .xlsx file
                    FileInputStream fileInputStream = new FileInputStream(file);
                    Workbook workbook = WorkbookFactory.create(fileInputStream);

                    //Get first/desired sheet from the workbook
                    Sheet sheet = workbook.getSheetAt(1); // First Sheet on the tab

                    CellStyle cellStyle = appData.getBorderedStyle(workbook);

                    for(Map.Entry<String,Hole> entry : holes.entrySet()) { // Saving each hole data via direct row access

                        Hole hole = entry.getValue();

                        int row = hole.getRowNumber();

                        // In here we skip write process if value is 0.0 (double's null value) to make things faster instead of checking the values.
                        if(hole.getActualDepth() != 0.0) {
                            sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DEPTH).setCellValue(hole.getActualDepth());
                            sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DEPTH).setCellStyle(cellStyle);
                        }

                        if(hole.getActualDiameter() != 0.0) {
                            sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DIAMETER).setCellValue(hole.getActualDiameter());
                            sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DIAMETER).setCellStyle(cellStyle);
                        }

                        if(hole.getActualDipAngle() != 0.0) {
                            sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DIP_ANGLE).setCellValue(hole.getActualDipAngle());
                            sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_DIP_ANGLE).setCellStyle(cellStyle);
                        }

                        if(hole.getActualChargeWeight() != 0.0) {
                            sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_CHARGE_WEIGHT).setCellValue(hole.getActualChargeWeight());
                            sheet.getRow(row).getCell(Hole.COLUMN_ACTUAL_CHARGE_WEIGHT).setCellStyle(cellStyle);
                        }

                        if(hole.getCollarPipe() != 0.0) {
                            sheet.getRow(row).getCell(Hole.COLUMN_COLLAR_PIPE).setCellValue(hole.getCollarPipe());
                            sheet.getRow(row).getCell(Hole.COLUMN_COLLAR_PIPE).setCellStyle(cellStyle);
                        }

                        sheet.getRow(row).getCell(Hole.COLUMN_REDRILL_REQUIRED).setCellValue(hole.getRedrillRequired());
                        sheet.getRow(row).getCell(Hole.COLUMN_REDRILL_REQUIRED).setCellStyle(cellStyle);

                        sheet.getRow(row).getCell(Hole.COLUMN_IMPORTANT_NOTES).setCellValue(hole.getImportantNotes());
                        sheet.getRow(row).getCell(Hole.COLUMN_IMPORTANT_NOTES).setCellStyle(cellStyle);

                        // Blank Columns
                        if(hole.getOverrideRedrill() == true) {
                            sheet.getRow(row).getCell(18).setCellValue("Overridden");
                        }

                        /* Set/Get Custom Data */
                        int customDataColumn = 19; // Start is column 19

                        for(SettingsData settingsData: appData.getSettingsList()) {

                            if(settingsData.getDatatype().equals("Numeric")) {

                                Cell numericCell = sheet.getRow(row).createCell(customDataColumn);

                                double value = (double) hole.getCustomData(settingsData.getName());

                                if(value == 0.0) {
                                    numericCell.setCellType(Cell.CELL_TYPE_BLANK);
                                    numericCell.setCellStyle(cellStyle);
                                    numericCell.setCellValue("");
                                } else {
                                    numericCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    numericCell.setCellStyle(cellStyle);
                                    numericCell.setCellValue((double)hole.getCustomData(settingsData.getName()));
                                }

                            } else {

                                Cell stringCell = sheet.getRow(row).createCell(customDataColumn);
                                stringCell.setCellType(Cell.CELL_TYPE_STRING);
                                stringCell.setCellStyle(cellStyle);

                                stringCell.setCellValue((String)hole.getCustomData(settingsData.getName()));

                            }

                            customDataColumn = customDataColumn + 1;

                        }

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
                    operationError = true;
                    e.printStackTrace();
                }

                if(operationError == false) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK,returnIntent); // Sets the result to be received by onActivityResult in the caller activity.
                    finish(); // Returns back to the calling activity.
                } else {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED,returnIntent); // Sets the result to be received by onActivityResult in the caller activity.
                    finish(); // Returns back to the calling activity.
                }

            }
        });
    }

    public void manageHole(Hole hole) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this); // Notifications Dialog Builder
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setTitle("Manage Hole");
        alertDialog.setMessage("Hole ID: " + hole.getId() + "\n\nEasting: " + hole.getEasting() + "\nNorthing: " + hole.getNorthing());

        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Open Hole",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // New intent passing the holes list to sequence activity
                Intent intent = new Intent(QAQCActivity.this,SequenceActivity.class);

                ArrayList<Object> allHoles = new ArrayList<>(Arrays.asList(QAQCActivity.this.holes.values().toArray()));

                ArrayList<Hole> selectedPoint = new ArrayList<>();
                selectedPoint.add(hole);

                intent.putExtra("holesList", selectedPoint);

                intent.putExtra("sitefile", filename);
                intent.putExtra("surfaceRL", fileSurfaceRL);
                intent.putExtra("targetRL", fileTargetRL);
                intent.putExtra("subDrill", fileSubDrill);
                intent.putExtra("depthTolerance", fileDepthTolerance);

                intent.putExtra("quickMapList", allHoles);
                intent.putExtra("minimumEasting", minimumEasting);
                intent.putExtra("maximumEasting", maximumEasting);
                intent.putExtra("minimumNorthing", minimumNorthing);
                intent.putExtra("maximumNorthing", maximumNorthing);

                startActivityForResult(intent,REQUEST_CODE_SEQUENCE);

            }
        });

        alertDialog.show();
    }

    public boolean parseDataFile(File file) {

        try {
            // Parse .xlsx file
            FileInputStream fileStream = new FileInputStream(file);

            //Create Workbook instance holding reference to .xlsx file
            Workbook workbook = WorkbookFactory.create(fileStream);

            //Get first/desired sheet from the workbook
            Sheet sheet = workbook.getSheetAt(1); // First Sheet on the tab

            /* Multithreading might be needed here for loading the points async into the scatter diagram */

            // Create hole objects through iteration
            boolean flag = true;
            int currentRow = 12;

            // Set file global values
            fileSurfaceRL = readDoubleWithCheck(sheet.getRow(6).getCell(2));
            fileTargetRL = readDoubleWithCheck(sheet.getRow(7).getCell(2));
            fileSubDrill = readDoubleWithCheck(sheet.getRow(4).getCell(2));
            fileDepthTolerance = readDoubleWithCheck(sheet.getRow(8).getCell(2));

            // Initialize values for comparison
            minimumNorthing = readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_NORTHING));
            minimumEasting = readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_EASTING));
            maximumNorthing = readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_NORTHING));
            maximumEasting = readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_EASTING));

            double northing;
            double easting;
            String holeId;

            while(flag) {

                Row row = sheet.getRow(currentRow);

                if(row == null) {
                    row = sheet.createRow(currentRow);
                }

                Cell cell = row.getCell(Hole.COLUMN_ID,Row.CREATE_NULL_AS_BLANK);

                // Apache POI XSSF have different cell types eventhough you only see a blank cell.
                if (!readStringWithCheck(cell).equals("")) {

                    // Create hole

                    holeId = readStringWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_ID));
                    northing = readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_NORTHING));
                    easting = readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_EASTING));

                    // We can take the min/max easting and northing to find the appropriate origin
                    if(northing >= maximumNorthing) {
                        maximumNorthing = northing;
                    }

                    if(easting >= maximumEasting) {
                        maximumEasting = easting;
                    }

                    if(northing < minimumNorthing) {
                        minimumNorthing = northing;
                    }

                    if(easting < minimumEasting) {
                        minimumEasting = easting;
                    }

                    // Set hole properties

                    Hole hole = new Hole(holeId,easting,northing);
                    hole.setRowNumber(sheet.getRow(currentRow).getRowNum());
                    hole.setElevation(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_ELEVATION)));
                    hole.setDesignDepth(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_DESIGN_DEPTH)));
                    hole.setDesignDiameter(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_DESIGN_DIAMETER)));
                    hole.setDesignDipAngle(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_DESIGN_DIP_ANGLE)));
                    hole.setDesignStemmingColumn(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_DESIGN_STEMMING_COLUMN)));
                    hole.setDesignChargeWeight(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_DESIGN_CHARGE_WEIGHT)));
                    hole.setRecalculatedChargeWeight(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_RECALCULATED_CHARGE_WEIGHT)));

                    hole.setActualDepth(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_ACTUAL_DEPTH)));
                    hole.setActualDiameter(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_ACTUAL_DIAMETER)));
                    hole.setActualDipAngle(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_ACTUAL_DIP_ANGLE)));
                    hole.setActualChargeWeight(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_ACTUAL_CHARGE_WEIGHT)));
                    hole.setCollarPipe(readDoubleWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_COLLAR_PIPE)));
                    hole.setRedrillRequired(readStringWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_REDRILL_REQUIRED)));
                    hole.setImportantNotes(readStringWithCheck(sheet.getRow(currentRow).getCell(Hole.COLUMN_IMPORTANT_NOTES)));

                    if(readStringWithCheck(sheet.getRow(currentRow).getCell(18)).equals("Overridden")) {
                        hole.setOverrideRedrill(true);
                    } else {
                        hole.setOverrideRedrill(false);
                    }

                    // Set Custom Data
                    int customDataColumn = 19; // Custom data starts at column 19

                    for(SettingsData settingsData: appData.getSettingsList()) {

                        // Set value for custom data, null is handled by the datatype checkers
                        if(settingsData.getDatatype().equals("Numeric")) {

                            // Set hole custom data value
                            Cell numericCell = sheet.getRow(currentRow).getCell(customDataColumn);

                            // Check if custom data has been changed to another datatype
                            hole.setCustomData(settingsData.getName(), readDoubleWithCheck(numericCell));

                        } else {
                            hole.setCustomData(settingsData.getName(),readStringWithCheck(sheet.getRow(currentRow).getCell(customDataColumn)));
                        }

                        // Move to the right
                        customDataColumn = customDataColumn + 1;
                    }

                    this.setHoleColorValidation(hole);

                    // Store holes into an array list holes
                    holes.put(hole.getId(),hole);

                    // We traverse down until blank cell
                    currentRow = currentRow + 1;

                } else {

                    flag = false; // End loop

                }

            }

            fileStream.close();
            workbook.close();

            return true;

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public double calculateEastingPerPixel(double inEasting) {

        double adjustedEasting = Double.parseDouble(Double.toString(inEasting));

        double eastingPixel = (maxWidth) / adjustedEasting;

        return eastingPixel;

    }

    public double calculateNorthingPerPixel(double inNorthing) {

        double adjustedNorthing = Double.parseDouble(Double.toString(inNorthing));

        double northingPixel = (maxHeight) / adjustedNorthing;

        return northingPixel;

    }

    public String readStringWithCheck(Cell cell) {

        String cellValue;

        if(cell != null) {
            switch(cell.getCellType()) {
                case Cell.CELL_TYPE_STRING: cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC: cellValue = String.valueOf(Math.round(cell.getNumericCellValue()));
                    break;
                case Cell.CELL_TYPE_FORMULA: cellValue = String.valueOf(Math.round(cell.getNumericCellValue()));
                    break;
                default: cellValue = "";
            }
        } else {
            cellValue = "";
        }

        return cellValue;
    }

    public double readDoubleWithCheck(Cell cell) {

        double cellValue;

        if(cell != null) {
            switch(cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    if(cell.getStringCellValue().equals("") || cell.getStringCellValue().equals("true") || cell.getStringCellValue().equals("false")) {
                        cellValue = 0.0;
                    } else {
                        cellValue = Double.valueOf(cell.getStringCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_NUMERIC: cellValue = Double.valueOf(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA: cellValue = Double.valueOf(cell.getNumericCellValue());
                    break;
                default: cellValue = 0.0;
            }
        } else {
            cellValue = 0.0;
        }

        return cellValue;
    }

    public int getRoundedCoordinate(double coordinate) {

        StringBuilder x = new StringBuilder(Integer.toString((int)coordinate));

        x.setCharAt(x.length()-1, '0');

        int minAdjustedEasting = Integer.valueOf(x.toString());

        return minAdjustedEasting;
    }

    public void sortArray(DataPoint[] dataPoints) {

        // Quicksort Algorithm
        quickSort(dataPoints,0,dataPoints.length-1);

    }

    public void quickSort(DataPoint arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex-1);
            quickSort(arr, partitionIndex+1, end);
        }
    }

    private int partition(DataPoint arr[], int begin, int end) {
        double pivot = arr[end].getX();
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if (arr[j].getX() <= pivot) {
                i++;

                DataPoint swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        DataPoint swapTemp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = swapTemp;

        return i+1;
    }

    public void displayParameters() {
        displayParametersFragment.show(fm, "fragment_displayparameters");
    }

    public void setTopLabel(String parameter) {
        series.toggleCustomTopLabel(parameter,true);
    }

    public void setBottomLabel(String parameter) {
        series.toggleCustomBottomLabel(parameter,true);
    }

    public PointsGraphSeries getSeries() {
        return series;
    }

    public void setHoleColorValidation(Hole h) {
        switch(h.getRedrillRequired()) {
            case "true": h.setColor(getResources().getColor(android.R.color.holo_orange_light));
                break;
            case "false": h.setColor(getResources().getColor(android.R.color.holo_green_light));
                break;
            default: h.setColor(0xff0077cc);
                break;
        }
    }

    // Once the OS returns to this activity from sequence activity (REQUEST_CODE_SEQUENCE = 3)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnData) {

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_SEQUENCE) {

            // We update the maps holes
            ArrayList<Hole> updatedHoles = (ArrayList<Hole>) returnData.getSerializableExtra("updatedHoles");

            // We clear GraphViews selection lists for re-updating
            // We do not clear selections if we are just opening a hole. size == 1
            QAQCActivity.this.getSeries().clearSelections();

            for(Hole h: updatedHoles) {

                this.setHoleColorValidation(h);
                holes.put(h.getId(),h);

                if(updatedHoles.size() != holes.size()) { // For selected list only
                    if(!QAQCActivity.this.getSeries().getSelectionList().contains(h)) {
                        switch(h.getRedrillRequired()) {
                            case "true": h.setColor(getResources().getColor(android.R.color.holo_orange_light));
                                break;
                            case "false": h.setColor(getResources().getColor(android.R.color.holo_green_light));
                                break;
                            default: h.setColor(Color.RED);
                                break;
                        }
                        QAQCActivity.this.getSeries().addSelection(h);
                    }
                }
            }

            Hole[] holesArr = new Hole[holes.values().size()];
            holesArr = holes.values().toArray(holesArr);
            this.sortArray(holesArr);

            series.resetData(holesArr);
            series.redraw();
        }

    }
}
