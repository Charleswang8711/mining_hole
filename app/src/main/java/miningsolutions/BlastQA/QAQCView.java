package miningsolutions.BlastQA;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class QAQCView extends View {

    Context context;
    int maxWidth = 1920; // In pixels
    int maxHeight = 1024; // In pixels

    List<Hole> holes = new ArrayList<Hole>();
    Hole origin;
    double maximumNorthing;
    double maximumEasting;
    double minimumNorthing;
    double minimumEasting;
    double northingPerPixel;
    double eastingPerPixel;
    int zoomFactor = 30;

    public QAQCView(Context context, AttributeSet attrs) {
        super(context);
        this.context = context;
    }

    @Override
    public void onDraw(Canvas canvas) {

            //ImageView view = (ImageView) findViewById(R.id.imageView);
        /*
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Add to list<Holes>

                return false;
            }
        });
        */
        try {
                // Parse .xlsx file
            String path = Environment.getExternalStorageDirectory().toString()+"/DCIM/BlastPatterns/SN217.xlsx";
            File file = new File(path);
            FileInputStream fileStream = new FileInputStream(file);

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(fileStream);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(1); // First Sheet on the tab

            /* Multithreading might be needed here for loading asyncronously the points into the scatter diagram */

            // Create hole objects through iteration
            int currentRow = 12;
            int currentCell = 4;
            boolean flag = true;

            // Initialize values for comparison
            minimumNorthing = readDoubleWithCheck(sheet.getRow(12).getCell(1));
            minimumEasting = readDoubleWithCheck(sheet.getRow(12).getCell(2));
            maximumNorthing = readDoubleWithCheck(sheet.getRow(12).getCell(1));
            maximumEasting = readDoubleWithCheck(sheet.getRow(12).getCell(2));

            while(flag) {

                String holeId = null;

                // Apache POI XSSF have different cell types eventhough you only see a blank cell.
                if (!isCellEmpty(sheet.getRow(currentRow).getCell(4))) {

                    holeId = readStringWithCheck(sheet.getRow(currentRow).getCell(4));

                    double northing = readDoubleWithCheck(sheet.getRow(currentRow).getCell(1));
                    double easting = readDoubleWithCheck(sheet.getRow(currentRow).getCell(2));

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

                    // Create hole
                    Hole hole = new Hole(holeId,northing,easting);

                    // Store holes into an array list holes
                    holes.add(hole);

                    currentRow = currentRow + 1; // We traverse down until blank cell

                } else {

                    flag = false; // End loop

                }

            }

            // Set origin based on the minimum coordinates + some adjustment distance
            double adjustmentDistance = 0.0; // Usually 0.0 to 1.0
            origin = new Hole("origin", minimumNorthing,minimumEasting);

            fileStream.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Render Drawable UI
        Paint black = new Paint();
        black.setColor(Color.BLACK);
        black.setStrokeWidth(2);
        canvas.drawLine(50, 20, 50, 980, black);
        canvas.drawLine(50, 980, 1890, 980, black);

        eastingPerPixel = this.calculateEastingPerPixel(maximumEasting);
        northingPerPixel = this.calculateNorthingPerPixel(maximumNorthing);
        /*
        double adjustedEasting = Double.parseDouble(Double.toString(origin.getEasting()).substring(3));
        double adjustedNorthing = Double.parseDouble(Double.toString(origin.getNorthing()).substring(4));

        System.out.println("Origin CX: " + String.valueOf(eastingPerPixel * adjustedEasting));
        System.out.println("Origin CY: " + String.valueOf(northingPerPixel * adjustedNorthing));

        int cx = (int)(eastingPerPixel * adjustedEasting);
        int cy = (int)(northingPerPixel * adjustedNorthing);
        */

        // Render origin point
       // canvas.drawCircle(cx,cy,10,black); // This has to be a view object with onTouchListener()


        for(Hole h: holes) {
            this.placeHole(canvas,h);
        }

    }

    public double calculateEastingPerPixel(double inEasting) {

        double adjustedEasting = Double.parseDouble(Double.toString(inEasting).substring(3));

        double eastingPixel = (maxWidth) / adjustedEasting;

        return eastingPixel;

    }

    public double calculateNorthingPerPixel(double inNorthing) {

        double adjustedNorthing = Double.parseDouble(Double.toString(inNorthing).substring(4));

        double northingPixel = (maxHeight) / adjustedNorthing;

        return northingPixel;

    }

    public void placeHole(Canvas canvas, Hole hole) {

        double adjustedEasting = Double.parseDouble(Double.toString(hole.getEasting()).substring(4));
        double adjustedNorthing = Double.parseDouble(Double.toString(hole.getNorthing()).substring(5));

        int cx = (int)(adjustedEasting * (eastingPerPixel+zoomFactor));
        int cy = (int)(adjustedNorthing * (northingPerPixel+zoomFactor));

        int adjustedCX = cx + 50;
        int adjustedCY = maxHeight - (cy+60);

        Paint black = new Paint();
        black.setColor(Color.BLACK);
        canvas.drawCircle(adjustedCX,adjustedCY,10,black);
    }
    /*
    public double calculateEastingCX() {

        // Using Hypotenuse, Adjacent and Opposite

    }

    public double calculateNorthingCY() {

        // Using Hypotenuse, Adjacent and Opposite

    }
    */

    /* Deprecated
    public double findDistanceFromOrigin(Hole q) {

        double x2 = Math.pow(q.getEasting(),2);
        double y2 = Math.pow(q.getNorthing(),2);
        double distance = Math.sqrt(x2+y2);

        return distance;

    }
    */

    public boolean isCellEmpty(final XSSFCell cell) {
        if (cell == null) { // use row.getCell(x, Row.CREATE_NULL_AS_BLANK) to avoid null cells
            return true;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            return true;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().trim().isEmpty()) {
            return true;
        }

        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC && Double.valueOf(cell.getNumericCellValue()) == null) {
            return true;
        }

        return false;
    }

    public String readStringWithCheck(Cell cell) {

        String cellValue = "";

        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_STRING: cellValue = cell.getStringCellValue();
                                        break;
            case Cell.CELL_TYPE_NUMERIC: cellValue = String.valueOf(cell.getNumericCellValue());
                                        break;
        }

        return cellValue;
    }

    public double readDoubleWithCheck(Cell cell) {

        double cellValue = 0.0;

        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_STRING: cellValue = Double.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC: cellValue = Double.valueOf(cell.getNumericCellValue());
                break;
        }

        return cellValue;
    }

}
