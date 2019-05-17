package miningsolutions.BlastQA;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.Image;
import android.os.Environment;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import miningsolutions.BlastQA.Settings.SettingsData;

public class AppData extends Activity {

    private Context context;
    private static AppData appData;
    private SharedPreferences sharedPreferences; // Shared preferences storage
    private static AlertDialog.Builder alertDialogBuilder; // Notifications Dialog Builder

    private User currentUser;
    private String timeStamp;
    private String path;
    private List<SettingsData> settingsDataList;
    private Integer selectedPosition;

    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public AppData(Context inContext, Activity activity) {

        // Initialize variables
        context = inContext;
        appData = this;
        sharedPreferences = activity.getSharedPreferences("blastpatternlocation",Context.MODE_PRIVATE);

        // Set Patterns Location
        path = sharedPreferences.getString("blastpatternlocation","/DCIM/BlastPatterns/");

        settingsDataList = new ArrayList<>();

        /*
        SettingsData brokenGround = new SettingsData(getContext(),"Broken Ground");
        brokenGround.setDatatype("boolean");
        SettingsData wetHole = new SettingsData(getContext(),"Wet Hole");
        wetHole.setDatatype("boolean");
        SettingsData noHole = new SettingsData(getContext(), "No Hole");
        noHole.setDatatype("boolean");

        customDataList.add(brokenGround);
        customDataList.add(wetHole);
        customDataList.add(noHole);
        */

    }

    public static AppData getAppData() {
        return appData;
    }

    public Context getContext() {
        return context;
    }


    public User createUser(String firstName, String lastName) {
        User user = new User(firstName,lastName);
        return user;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {

        // Update runtime path variable
        this.path = path;

        // Write to shared preferences storage
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("blastpatternlocation", path);
        editor.commit();
    }

    public List<SettingsData> getSettingsList() {
        return settingsDataList;
    }

    public void setSettingsList(List<SettingsData> customDataList) {
        this.settingsDataList = customDataList;
    }

    public void addCustomData(SettingsData newData) {
        this.settingsDataList.add(newData);
    }

    public void removeCustomData(int position) {
        this.settingsDataList.remove(position);
    }

    public String[] getPERMISSIONS_STORAGE() {
        return PERMISSIONS_STORAGE;
    }

    public void setPERMISSIONS_STORAGE(String[] PERMISSIONS_STORAGE) {
        this.PERMISSIONS_STORAGE = PERMISSIONS_STORAGE;
    }

    public void Notify(String title, String message, Activity activity) {
        alertDialogBuilder = new AlertDialog.Builder(activity);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    public Integer getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(Integer selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public boolean isPathValid(String inPath) {

        boolean valid = true;

        try {
            File file = new File(inPath); // Test File Directory Path
        } catch(Exception e) {
            valid = false;
        }

        return valid;

    }

    public CellStyle getBorderedStyle(Workbook workbook) {

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return cellStyle;

    }

}
