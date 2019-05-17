package miningsolutions.BlastQA;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import miningsolutions.BlastQA.Settings.SettingsData;

public class MoreFragment extends DialogFragment {

    public AppData appData;
    List<SettingsData> settingsDataList;
    String filename;
    Hole hole;

    TableLayout booleanTable;
    TableLayout numericTable;
    Button saveButton;

    Map<String,EditText> numericSettingsDataField = new HashMap<>();

    public MoreFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more, container, false);
        appData = AppData.getAppData();

        booleanTable = (TableLayout) view.findViewById(R.id.BooleanTable);
        numericTable = (TableLayout) view.findViewById(R.id.NumericTable);
        saveButton = (Button) view.findViewById(R.id.SaveButton);

        settingsDataList = appData.getSettingsList();
        int numNumericRows = settingsDataList.size() / 1;
        int numBooleanRows = settingsDataList.size() / 2;

        // Create the number of rows for the settings data
        for(int i = 0; i <= numNumericRows; i++) {
            TableRow row = new TableRow(getActivity().getApplicationContext());
            numericTable.addView(row,i);
        }

        // Create the number of rows for the settings data
        for(int i = 0; i <= numBooleanRows; i++) {
            TableRow row = new TableRow(getActivity().getApplicationContext());
            row.setOrientation(TableRow.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            booleanTable.addView(row,i);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        hole = (Hole)bundle.getSerializable("CurrentHole");
        filename = bundle.getString("filename");

        populateMoreTables();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String path = Environment.getExternalStorageDirectory().toString()+ appData.getPath() + filename;
                    File file = new File(path);

                    //Create Workbook instance holding reference to .xlsx file
                    FileInputStream fileInputStream = new FileInputStream(file);
                    Workbook workbook = WorkbookFactory.create(fileInputStream);

                    //Get first/desired sheet from the workbook
                    Sheet sheet = workbook.getSheetAt(1); // First Sheet on the tab

                    int currentRow = 12;
                    int IDColumn = 4;

                    int customDataColumn = 19;

                    boolean flag = true;

                    while(flag) {

                        Hole hole =  MoreFragment.this.hole;

                        if (hole.getId().equals(readStringWithCheck(sheet.getRow(currentRow).getCell(IDColumn)))) { // Hole ID Matched

                            System.out.println("Matched found. Updating Data..."); // Need to change this logic to hole.getRowNumber() instead

                            CellStyle cellStyle = workbook.createCellStyle();
                            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

                            for(SettingsData settingsData: appData.getSettingsList()) {
                                if(settingsData.getDatatype().equals("Numeric")) {

                                    Cell numericCell = sheet.getRow(currentRow).createCell(customDataColumn);
                                    numericCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    numericCell.setCellStyle(cellStyle);

                                    EditText numericField = numericSettingsDataField.get(settingsData.getName());

                                    hole.setCustomData(settingsData.getName(),getFieldValueWithCheck(numericField.getText().toString()));
                                    numericCell.setCellValue(getFieldValueWithCheck(numericField.getText().toString()));

                                } else {

                                    Cell stringCell = sheet.getRow(currentRow).createCell(customDataColumn);
                                    stringCell.setCellType(Cell.CELL_TYPE_STRING);
                                    stringCell.setCellStyle(cellStyle);

                                    stringCell.setCellValue((String)hole.getCustomData(settingsData.getName()));

                                }
                                customDataColumn = customDataColumn + 1;
                            }

                            flag = false;

                        }

                        currentRow = currentRow + 1;

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

                ((SequenceActivity)getActivity()).onMoreFragmentSave(); // Refresh Quick Buttons

                // Dismiss fragment
                getDialog().dismiss();
            }
        });

    }

    public void populateMoreTables(){

        // Reset count
        int numericCount = 0;
        int booleanCount = 0;

        for(SettingsData settingsData: settingsDataList) {

            // Add settings data into its appropriate table
            if(settingsData.getDatatype().equals("Numeric")) { // Numeric Field Values

                // Create widgets based on settingsData
                TextView label = new TextView(getActivity().getApplicationContext());
                label.setText(settingsData.getName());
                label.setWidth(520);
                label.setMinWidth(520);
                label.setMaxWidth(520);
                label.setTextSize(16);
                label.setPadding(0,0,50,0);

                EditText field = new EditText(getActivity().getApplicationContext());
                field.setWidth(150);
                field.setMinWidth(150);
                field.setMaxWidth(150);
                field.setTextSize(16);
                field.setSingleLine(true);
                field.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                field.setImeOptions(EditorInfo.IME_ACTION_DONE);

                double value = (double)hole.getCustomData(settingsData.getName());

                if(value == 0.0) {
                    field.setText("0.0");
                } else {
                    field.setText(String.valueOf(value));
                }

                TableRow row = (TableRow) numericTable.getChildAt(numericCount);

                // Reset settings data placement rows
                if(label.getParent() != null) { // Initial render, settings data parent view is null
                    ((ViewGroup)label.getParent()).removeView(label);
                }

                if(field.getParent() != null) { // Initial render, settings data parent view is null
                    ((ViewGroup)field.getParent()).removeView(field);
                }

                // Place settings data view
                row.addView(label);
                row.addView(field);

                numericSettingsDataField.put(settingsData.getName(),field);

                numericCount = numericCount + 1;

            } else { // True/False Buttons

                // Create widgets based on settingsData
                Button button = new Button(getActivity().getApplicationContext());

                button.setText(settingsData.getName());
                button.setMinWidth(90);
                button.setMaxWidth(90);
                button.setMinHeight(150);
                button.setMaxHeight(150);

                if(hole.getCustomData(settingsData.getName()).toString().equals("true")) {
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                } else {
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                }

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(hole.getCustomData(settingsData.getName()).equals("true")) {
                            hole.setCustomData(settingsData.getName(),"false");
                            button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                        } else {
                            hole.setCustomData(settingsData.getName(),"true");
                            button.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        }

                    }
                });

                int index = booleanCount / 2;
                TableRow row = (TableRow) booleanTable.getChildAt(index);

                // Reset settings data placement rows
                if(button.getParent() != null) { // Initial render, settings data parent view is null
                    ((ViewGroup)button.getParent()).removeView(settingsData);
                }

                // Place settings data view
                row.addView(button);
                booleanCount = booleanCount + 1;
            }

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

    public double getFieldValueWithCheck(String value) {
        if(value.equals("")) {
            return 0.0;
        } else {
            return Double.valueOf(value);
        }
    }

}