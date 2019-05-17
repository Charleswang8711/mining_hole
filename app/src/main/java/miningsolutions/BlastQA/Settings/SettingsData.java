package miningsolutions.BlastQA.Settings;

import android.content.Context;
import android.view.View;

public class SettingsData extends View {

    Context context;
    String name;
    String datatype;
    boolean changed;

    public SettingsData(Context context, String name) {
        super(context);
        this.context = context;
        this.name = name;

        // Default datatype and changed flag
        datatype = "Numeric";
        changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public boolean isDatatypeChanged() {
        return changed;
    }

    public void setDatatypeChanged(boolean changed) {
        this.changed = changed;
    }

}
