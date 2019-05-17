package miningsolutions.BlastQA;

import java.text.DecimalFormat;

import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.util.HashMap;

public class Hole extends DataPoint implements Serializable {

    // Static Columns Configurations
    public static final int COLUMN_NORTHING = 1;
    public static final int COLUMN_EASTING = 2;
    public static final int COLUMN_ELEVATION = 3;
    public static final int COLUMN_ID = 4;
    public static final int COLUMN_DESIGN_DEPTH = 5;
    public static final int COLUMN_DESIGN_DIAMETER = 6;
    public static final int COLUMN_DESIGN_DIP_ANGLE = 7;
    public static final int COLUMN_DESIGN_STEMMING_COLUMN = 8;
    public static final int COLUMN_DESIGN_CHARGE_WEIGHT = 9;
    public static final int COLUMN_RECALCULATED_CHARGE_WEIGHT = 10;
    public static final int COLUMN_ACTUAL_DEPTH = 11;
    public static final int COLUMN_ACTUAL_DIAMETER = 12;
    public static final int COLUMN_ACTUAL_DIP_ANGLE = 13;
    public static final int COLUMN_ACTUAL_CHARGE_WEIGHT = 14;
    public static final int COLUMN_COLLAR_PIPE = 15;
    public static final int COLUMN_REDRILL_REQUIRED = 16;
    public static final int COLUMN_IMPORTANT_NOTES = 17;

    // Location Variables
    String id;
    Integer rowNumber;

    // Hole Properties
    double easting;
    double northing;
    double elevation;
    double designDiameter;
    double actualDiameter;
    double designDepth;
    double actualDepth;
    double designChargeWeight;
    double recalculatedChargeWeight;
    double actualChargeWeight;
    double designStemmingColumn;
    double designDipAngle;
    double actualDipAngle;
    double collarPipe;
    String importantNotes;
    String redrillRequired;
    boolean overrideRedrill;

    // Custom Data
    private HashMap<String, Object> customData; // Object can be a double field or a boolean button

    public Hole(String inId, double easting, double northing) {

        // DataPoint Attributes
        super(easting,northing); // initialize x and y in DataPoint parent class
        super.addCustomData("id",inId);

        // Hole Attributes
        id = inId;
        this.easting = easting;
        this.northing = northing;

        // Custom Data Hash Map
        customData = new HashMap<>();
    }

    public void setId(String newId) {
        this.id = newId;
    }

    public String getId() {
        return id;
    }

    public double getEasting() {
        return easting;
    }

    public void setEasting(double easting) {
        this.easting = easting;
    }

    public double getNorthing() {
        return northing;
    }

    public void setNorthing(double northing) {
        this.northing = northing;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getElevation() {
        return elevation;
    }

    public double getDesignDiameter() {
        return designDiameter;
    }

    public void setDesignDiameter(double designDiameter) {
        this.designDiameter = designDiameter;
    }

    public double getActualDiameter() {
        return actualDiameter;
    }

    public void setActualDiameter(double actualDiameter) { this.actualDiameter = actualDiameter; }

    public double getDesignDepth() {
        return designDepth;
    }

    public void setDesignDepth(double designDepth) {
        super.addCustomData("depth", designDepth);
        this.designDepth = designDepth;
    }

    public double getActualDepth() {
        return actualDepth;
    }

    public void setActualDepth(double actualDepth) {
        this.actualDepth = actualDepth;
    }

    public double getDesignChargeWeight() {
        return designChargeWeight;
    }

    public void setDesignChargeWeight(double designChargeWeight) {
        DecimalFormat df = new DecimalFormat("#.##");
        super.addCustomData("charge", Double.valueOf(df.format(designChargeWeight)));
        this.designChargeWeight = designChargeWeight;
    }

    public double getRecalculatedChargeWeight() {
        return recalculatedChargeWeight;
    }

    public void setRecalculatedChargeWeight(double recalculatedChargeWeight) {
        this.recalculatedChargeWeight = recalculatedChargeWeight;
    }

    public double getActualChargeWeight() {
        return actualChargeWeight;
    }

    public void setActualChargeWeight(double actualChargeWeight) {
        this.actualChargeWeight = actualChargeWeight;
    }

    public double getDesignStemmingColumn() {
        return designStemmingColumn;
    }

    public void setDesignStemmingColumn(double designStemmingColumn) {
        super.addCustomData("stem", designStemmingColumn);
        this.designStemmingColumn = designStemmingColumn;
    }

    public double getDesignDipAngle() {
        return designDipAngle;
    }

    public void setDesignDipAngle(double designDipAngle) {
        this.designDipAngle = designDipAngle;
    }

    public double getActualDipAngle() {
        return actualDipAngle;
    }

    public void setActualDipAngle(double actualDipAngle) {
        this.actualDipAngle = actualDipAngle;
    }

    public double getCollarPipe() {
        return collarPipe;
    }

    public void setCollarPipe(double collarPipe) {
        this.collarPipe = collarPipe;
    }

    public String getImportantNotes() {
        return importantNotes;
    }

    public void setImportantNotes(String importantNotes) {
        this.importantNotes = importantNotes;
    }

    public String getRedrillRequired() {
        return redrillRequired;
    }

    public void setRedrillRequired(String redrillRequired) {
        this.redrillRequired = redrillRequired;
    }

    public boolean getOverrideRedrill() {
        return overrideRedrill;
    }

    public void setOverrideRedrill(boolean overrideRedrill) {
        this.overrideRedrill = overrideRedrill;
    }

    public void setCustomData(String name, Object value) {
        customData.remove(name);
        customData.put(name,value);
    }

    public Object getCustomData(String name) {
        return customData.get(name);
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

}
