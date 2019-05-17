package miningsolutions.BlastQA.Settings;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import miningsolutions.BlastQA.AppData;
import miningsolutions.BlastQA.R;

public class AddCustomDataFragment extends DialogFragment {

    public AppData appData;
    public EditText nameValue;
    public Button confirm;
    public SettingsFragment settingsFragment;
    public FragmentManager fm;

    public AddCustomDataFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addcustomdata, container, false);

        confirm = (Button) view.findViewById(R.id.Confirm);
        nameValue = (EditText) view.findViewById(R.id.NameValue);

        appData = AppData.getAppData();
        fm = getActivity().getSupportFragmentManager();
        settingsFragment = (SettingsFragment) fm.findFragmentByTag("fragment_settings");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(nameValue.getText().length() != 0) {

                    String name = nameValue.getText().toString();

                    boolean existing = false;

                    // Check if custom data already exist
                    for(SettingsData settingsData: appData.getSettingsList()) {
                        if(settingsData.getName().equals(name)) {
                            existing = true;
                        }
                    }

                    if(!existing) {

                        // Create custom data
                        SettingsData newData = new SettingsData(getContext(),name);
                        appData.addCustomData(newData);

                        // Close dialog
                        getDialog().dismiss();

                        // Refresh Settings Fragment
                        fm.beginTransaction().detach(AddCustomDataFragment.this).attach(AddCustomDataFragment.this).commit();

                    } else {
                        appData.Notify("Notification", "Custom data already exist with the same name. Please try again.", getActivity());
                    }

                } else {
                    appData.Notify("Notification", "Please enter name description of the data.",getActivity());
                }

            }
        });


    }

}