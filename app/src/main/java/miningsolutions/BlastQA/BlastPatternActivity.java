package miningsolutions.BlastQA;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import miningsolutions.BlastQA.Settings.SettingsData;

public class BlastPatternActivity extends AppCompatActivity {

    AppData appData;
    String sname;

    TextView siteName;
    Button qaqcModeButton;
    Button chargeModeButton;
    Button goBackButton;
    Button infoButton;
    ImageView loadingGIF;

    int REQUEST_CODE_QAQC = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blastpattern);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Set orientation to landscape
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // No autofocus on fields

        appData = AppData.getAppData();

        this.renderMenu();
    }

    public void renderMenu() {

        siteName = (TextView) findViewById(R.id.SiteLabel);
        qaqcModeButton = (Button) findViewById(R.id.QAQCButton);
        chargeModeButton = (Button) findViewById(R.id.ChargeButton);
        goBackButton = (Button) findViewById(R.id.GoBackButton);
        infoButton = (Button) findViewById(R.id.InfoButton);
        loadingGIF = (ImageView) findViewById(R.id.loading_gif);
        loadingGIF.setVisibility(View.GONE);

        Intent intent = getIntent();
        sname = intent.getStringExtra("SiteName");
        siteName.setText(sname);

        qaqcModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingGIF.setVisibility(View.VISIBLE);

                Intent intent = new Intent(BlastPatternActivity.this, QAQCActivity.class);
                intent.putExtra("filename",sname);
                startActivityForResult(intent,REQUEST_CODE_QAQC);

            }
        });

        chargeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activity Result
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent); // Sets the result to be received by onActivityResult in the caller activity.
                finish(); // Returns back to the calling activity.
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.Notify("Information","QAQC Mode - Information about QAQC mode.\nCharge Mode - Information about Charge mode.", BlastPatternActivity.this);
            }
        });

        validateSettingsData();

    }

    @Override
    public void onBackPressed() {
        // Activity Result
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent); // Sets the result to be received by onActivityResult in the caller activity.
        finish(); // Returns back to the calling activity.
    }

    public void validateSettingsData() {

        boolean invalid = false;

        try {

            String path = Environment.getExternalStorageDirectory().toString() + appData.getPath() + sname;
            File file = new File(path);

            //Create Workbook instance holding reference to .xlsx file
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fileInputStream);

            //Get first/desired sheet from the workbook
            Sheet sheet = workbook.getSheetAt(1); // First Sheet on the tab

            int startRow = 12;
            int headerRow = 11;
            int currentCell = 19;

            List<SettingsData> settingsDataList = appData.getSettingsList();
            CellStyle borderedStyle = appData.getBorderedStyle(workbook);

            // Prints the custom data header
            printCustomDataLabel(workbook,sheet);

            // Check if settings data matches custom data in file
            for(SettingsData settingsData: settingsDataList) {

                Cell cell = sheet.getRow(headerRow).getCell(currentCell,Row.CREATE_NULL_AS_BLANK);
                String customData = readStringWithCheck(cell);

                if(settingsData.getName().equals(customData)) { // Matched

                    // Column is valid, skip column
                    currentCell = currentCell + 1;

                } else { // Either we need to write custom data or update it.

                    // Set flag for notification as overwritten
                    invalid = true;

                    // Cell does not matched, we update it with the settings data
                    cell.setCellValue(settingsData.getName());
                    cell.setCellStyle(borderedStyle);

                    // Clear the custom data values
                    clearCustomDataValues(workbook,sheet,startRow,currentCell);

                    // Move to the right
                    currentCell = currentCell + 1;

                }

            }

            // Validate columns until its blank - end of custom data section
            validateRemainingColumns(workbook,sheet,headerRow,currentCell);

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

        if(invalid) {
            appData.Notify("Notification","Settings data does not match custom data in file. Auto-generated custom data in file was overwritten.",this);
        }

    }

    public void printCustomDataLabel(Workbook workbook, Sheet sheet) {

        CellStyle cellStyle = appData.getBorderedStyle(workbook);

        CellStyle blastQACellStyle = workbook.createCellStyle();
        blastQACellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        blastQACellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        Font font = workbook.createFont();
        font.setColor(HSSFColor.RED.index);
        blastQACellStyle.setFont(font);

        // Print custom data header
        Cell customDataLabelCell = sheet.getRow(10).getCell(19);
        customDataLabelCell.setCellValue("Custom Data");
        customDataLabelCell.setCellStyle(cellStyle);

        // Print auto-generated label
        Cell blastQACell = sheet.getRow(10).getCell(20);
        blastQACell.setCellValue("Auto-generated by BlastQA");
        blastQACell.setCellStyle(blastQACellStyle);

    }

    public void validateRemainingColumns(Workbook workbook, Sheet sheet, int currentRow, int currentCell) {

        boolean flag = true;

        int iterRows = 12;
        int IdColumn = 4;

        CellStyle deletedStyle = workbook.createCellStyle();
        deletedStyle.setBorderTop(HSSFCellStyle.BORDER_NONE);
        deletedStyle.setBorderRight(HSSFCellStyle.BORDER_NONE);
        deletedStyle.setBorderBottom(HSSFCellStyle.BORDER_NONE);
        deletedStyle.setBorderLeft(HSSFCellStyle.BORDER_NONE);
        deletedStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        deletedStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        while(flag) {

            Cell cell = sheet.getRow(currentRow).getCell(currentCell,Row.CREATE_NULL_AS_BLANK);

            if(readStringWithCheck(cell).equals("")) {

                flag = false; // End column

            } else {

                cell = sheet.getRow(currentRow).createCell(currentCell);
                cell.setCellType(Cell.CELL_TYPE_BLANK);
                cell.setCellStyle(deletedStyle);

                clearCustomDataValues(workbook,sheet,iterRows,currentCell);

                currentCell = currentCell + 1;
            }

        }

    }

    public void clearCustomDataValues(Workbook workbook, Sheet sheet, int row, int column) {

        try {

            boolean flag = true;
            int iterRows = row;

            CellStyle deletedStyle = workbook.createCellStyle();
            deletedStyle.setBorderTop(HSSFCellStyle.BORDER_NONE);
            deletedStyle.setBorderRight(HSSFCellStyle.BORDER_NONE);
            deletedStyle.setBorderBottom(HSSFCellStyle.BORDER_NONE);
            deletedStyle.setBorderLeft(HSSFCellStyle.BORDER_NONE);
            deletedStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            deletedStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            while (flag) {

                Row currentRow = sheet.getRow(iterRows);

                if (currentRow == null) {
                    currentRow = sheet.createRow(iterRows);
                }

                Cell idCell = currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK);

                if (!readStringWithCheck(idCell).equals("")) {
                    Cell cell = sheet.getRow(iterRows).getCell(column, Row.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("");
                    cell.setCellType(Cell.CELL_TYPE_BLANK);
                    cell.setCellStyle(deletedStyle);
                    iterRows = iterRows + 1;
                } else {
                    flag = false; // End while loop
                }

            }

        }
        catch(Exception e) {
            appData.Notify("Notification", "An error has occurred while trying to clear custom data values. Please check your file and try again",this);
        }

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

    // Once the OS returns to this activity from sequence activity (REQUEST_CODE_SEQUENCE = 3)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnData) {

        // Remove loading image
        loadingGIF.setVisibility(View.GONE);

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_QAQC) {
            appData.Notify("Information","File successfully saved.", this);
        } else {
            appData.Notify("An error has occurred","An error has occurred. Please check your file for any errors. Contact us for more information.",this);
        }

    }

}
