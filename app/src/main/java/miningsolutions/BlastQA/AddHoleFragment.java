package miningsolutions.BlastQA;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.TreeMap;

import miningsolutions.BlastQA.Settings.SettingsData;

public class AddHoleFragment extends DialogFragment {

    public AppData appData;
    public TreeMap<String,Hole> holes;
    public String newID;
    public TextView ID;
    public TextView Northing;
    public TextView Easting;
    public EditText DesignDiameter;
    public EditText DesignDepth;
    public EditText DesignDipAngle;
    public EditText DesignChargeWeight;
    public EditText DesignStemmingColumn;
    public Button confirm;

    int REQUEST_CODE_SEQUENCE = 3;

    public AddHoleFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addhole, container, false);

        ID = view.findViewById(R.id.NewID);
        Northing = view.findViewById(R.id.NewNorthing);
        Easting = view.findViewById(R.id.NewEasting);
        DesignDiameter = view.findViewById(R.id.DesignDiameterValue);
        DesignDepth = view.findViewById(R.id.DesignDepthValue);
        DesignChargeWeight = view.findViewById(R.id.DesignChargeWeightValue);
        DesignDipAngle = view.findViewById(R.id.DesignDipAngleValue);
        DesignStemmingColumn = view.findViewById(R.id.DesignStemmingColumnValue);

        confirm = view.findViewById(R.id.Confirm);
        appData = AppData.getAppData();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bee = getArguments();
        if(bee.getSerializable("holesMap") != null) {
            holes = (TreeMap<String, Hole>) bee.getSerializable("holesMap");
            newID = String.valueOf("n"+ (holes.size() + 1));
            ID.setText("New ID: "+newID);

            DecimalFormat df = new DecimalFormat("#.###");

            Easting.setText(df.format(bee.getDouble("x")));
            Northing.setText(df.format(bee.getDouble("y")));
        }

        DesignDiameter.addTextChangedListener(new TextValidator(DesignDiameter) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.toString().length() == 0) {
                    textView.setError("Field is required.");
                }
            }
        });

        DesignDepth.addTextChangedListener(new TextValidator(DesignDepth) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.toString().length() == 0) {
                    textView.setError("Field is required.");
                }
            }
        });

        DesignChargeWeight.addTextChangedListener(new TextValidator(DesignChargeWeight) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.toString().length() == 0) {
                    textView.setError("Field is required.");
                }
            }
        });

        DesignDipAngle.addTextChangedListener(new TextValidator(DesignDipAngle) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.toString().length() == 0) {
                    textView.setError("Field is required.");
                }
            }
        });

        DesignStemmingColumn.addTextChangedListener(new TextValidator(DesignStemmingColumn) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.toString().length() == 0) {
                    textView.setError("Field is required.");
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                QAQCActivity activity = (QAQCActivity) getActivity();

                if( Northing.length() != 0 && Easting.length() != 0 && DesignDiameter.length() != 0 && DesignDepth.length() != 0 && DesignChargeWeight.length() != 0 && DesignDipAngle.length() != 0 && DesignStemmingColumn.length() != 0) {
                    Hole newHole = new Hole(newID,Double.valueOf(Easting.getText().toString()),Double.valueOf(Northing.getText().toString()));
                    newHole.setDesignDiameter(Double.valueOf(DesignDiameter.getText().toString()));
                    newHole.setDesignDepth(Double.valueOf(DesignDepth.getText().toString()));
                    newHole.setDesignChargeWeight(Double.valueOf(DesignChargeWeight.getText().toString()));
                    newHole.setDesignStemmingColumn(Double.valueOf(DesignStemmingColumn.getText().toString()));
                    newHole.setDesignDipAngle(Double.valueOf(DesignDipAngle.getText().toString()));

                    for(SettingsData settingsData: appData.getSettingsList()) {

                        if(settingsData.getDatatype().equals("Numeric")) {
                            newHole.setCustomData(settingsData.getName(),0.0);
                        } else {
                            newHole.setCustomData(settingsData.getName(),"false");
                        }

                    }

                    newHole.setRedrillRequired(""); // Sets the color to blue
                    holes.put(newHole.getId(),newHole);

                    int adjustment = 11; // adjustment for header rows to last row of holes
                    saveNewHole(holes.get(newHole.getId()), holes.size() + adjustment);

                    Hole[] holesArr = new Hole[holes.values().size()];
                    holesArr = holes.values().toArray(holesArr);
                    activity.sortArray(holesArr);

                    activity.getSeries().resetData(holesArr);
                    activity.getSeries().redraw();

                    getDialog().dismiss();

                } else {
                    appData.Notify("Notification", "All fields are required.",activity);
                }

            }
        });

    }

    public void saveNewHole(Hole hole, int row) {

        try {

            String path = Environment.getExternalStorageDirectory().toString() + appData.getPath() + ((QAQCActivity)getActivity()).filename;
            File file = new File(path);

            // Create Workbook instance holding reference to .xlsx file
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fileInputStream);

            // Get first/desired sheet from the workbook
            Sheet sheet = workbook.getSheetAt(1); // First Sheet on the tab

            CellStyle cellStyle = appData.getBorderedStyle(workbook);

            // Create new row if getRow returns null, since we don't have a MissingPolicy for this function.
            Row newRow = sheet.getRow(row);
            if(newRow == null) {
                newRow = sheet.createRow(row);
            }

            // Set the height of the row
            sheet.getRow(row).setHeightInPoints((float) 24.75);

            // Set new hole object's row number
            hole.setRowNumber(newRow.getRowNum());

            // Initial Details
            sheet.getRow(row).createCell(Hole.COLUMN_ID).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_ID).setCellValue(hole.getId());

            sheet.getRow(row).createCell(Hole.COLUMN_NORTHING).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_NORTHING).setCellValue(hole.getNorthing());

            sheet.getRow(row).createCell(Hole.COLUMN_EASTING).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_EASTING).setCellValue(hole.getEasting());

            sheet.getRow(row).createCell(Hole.COLUMN_ELEVATION).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_ELEVATION).setCellValue(hole.getElevation());

            sheet.getRow(row).createCell(Hole.COLUMN_DESIGN_DEPTH).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_DESIGN_DEPTH).setCellValue(hole.getDesignDepth());

            sheet.getRow(row).createCell(Hole.COLUMN_DESIGN_DIAMETER).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_DESIGN_DIAMETER).setCellValue(hole.getDesignDiameter());

            sheet.getRow(row).createCell(Hole.COLUMN_DESIGN_DIP_ANGLE).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_DESIGN_DIP_ANGLE).setCellValue(hole.getDesignDipAngle());

            sheet.getRow(row).createCell(Hole.COLUMN_DESIGN_STEMMING_COLUMN).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_DESIGN_STEMMING_COLUMN).setCellValue(hole.getDesignStemmingColumn());

            sheet.getRow(row).createCell(Hole.COLUMN_DESIGN_CHARGE_WEIGHT).setCellStyle(cellStyle);
            sheet.getRow(row).getCell(Hole.COLUMN_DESIGN_CHARGE_WEIGHT).setCellValue(hole.getDesignChargeWeight());

            sheet.getRow(row).createCell(Hole.COLUMN_RECALCULATED_CHARGE_WEIGHT).setCellStyle(cellStyle);

            sheet.getRow(row).createCell(Hole.COLUMN_ACTUAL_DEPTH).setCellStyle(cellStyle);

            sheet.getRow(row).createCell(Hole.COLUMN_ACTUAL_DIAMETER).setCellStyle(cellStyle);

            sheet.getRow(row).createCell(Hole.COLUMN_ACTUAL_DIP_ANGLE).setCellStyle(cellStyle);

            sheet.getRow(row).createCell(Hole.COLUMN_ACTUAL_CHARGE_WEIGHT).setCellStyle(cellStyle);

            sheet.getRow(row).createCell(Hole.COLUMN_COLLAR_PIPE).setCellStyle(cellStyle);

            sheet.getRow(row).createCell(Hole.COLUMN_REDRILL_REQUIRED).setCellStyle(cellStyle);

            sheet.getRow(row).createCell(Hole.COLUMN_IMPORTANT_NOTES).setCellStyle(cellStyle);

            // We close file stream before opening a file output stream. Can't have both stream opened at the same file.
            fileInputStream.close();

            // Open file output stream for writing to the file.
            FileOutputStream fileOutput = new FileOutputStream(file);
            workbook.write(fileOutput);
            fileOutput.close();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            appData.Notify("Notification", "An error has occurred while trying to save data to your file. Please check your file and try again.",getActivity());
        }

    }

}