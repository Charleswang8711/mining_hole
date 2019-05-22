package miningsolutions.BlastQA.Settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import miningsolutions.BlastQA.AppData;
import miningsolutions.BlastQA.MainActivity;
import miningsolutions.BlastQA.R;

public class SettingsFragment extends DialogFragment {

    public AppData appData;

    public TextView patternsLocation;
    public Button openLocation;
    public RecyclerView settingsList;
    public RecyclerView.LayoutManager layoutManager;
    public SettingsAdapter mAdapter;
    public Button viewOutputSettings;
    public Button addCustomData;
    public Button removeCustomData;

    public Button closeBtn;


    FragmentManager fm;
    AddCustomDataFragment addCustomDataFragment;
    ViewAndOutputSettingFragment viewAndOutputSettingFragment;

    int REQUEST_CODE_SELECTLOCATION = 201; // Default Request Code for ACTION_OPEN_DOCUMENT_TREE. Do not change.

    public SettingsFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        appData = AppData.getAppData();

        patternsLocation = (TextView) view.findViewById(R.id.PatternsLocationValue);
        openLocation = (Button) view.findViewById(R.id.OpenLocationButton);

        patternsLocation.setText(appData.getPath());

        // Initialize layout views
        settingsList = (RecyclerView) view.findViewById(R.id.SettingsList);
        settingsList.setHasFixedSize(true);

        viewOutputSettings = (Button) view.findViewById(R.id.CheckSettingsBtn);
        addCustomData = (Button) view.findViewById(R.id.AddCustomData);
        removeCustomData = (Button) view.findViewById(R.id.RemoveCustomData);
        closeBtn = view.findViewById(R.id.Close);

        fm = getActivity().getSupportFragmentManager();
        addCustomDataFragment = new AddCustomDataFragment();
        addCustomDataFragment.setCancelable(false);
        viewAndOutputSettingFragment = new ViewAndOutputSettingFragment();
        viewAndOutputSettingFragment.setCancelable(false);

        // Specify layout manager for the list
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        settingsList.setLayoutManager(layoutManager);

        // Specify an adapter with the application data custom settings
        mAdapter = new SettingsAdapter(appData.getSettingsList());
        settingsList.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        openLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call intent on file explorer app
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                getActivity().startActivityForResult(Intent.createChooser(intent,"Choose Location"),REQUEST_CODE_SELECTLOCATION);
            }
        });

        viewOutputSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewAndOutputSettingFragment.show(fm, "fragment_viewandoutputsettings");

            }
        });

        addCustomData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomDataFragment.show(fm, "fragment_addcustomdata");
                mAdapter.notifyDataSetChanged();
            }
        });

        removeCustomData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(appData.getSelectedPosition() != null) {
                    appData.removeCustomData(appData.getSelectedPosition());
                }
                mAdapter.notifyDataSetChanged();

            }
        });


        closeBtn.setOnClickListener((view1) ->{

            this.dismiss();});

    }

    public void updatePatternsLocation(MainActivity mainActivity, Intent returnData) {

        try {

            Uri treeUri = returnData.getData();
            DocumentFile selectedDirectory = DocumentFile.fromTreeUri(getActivity(), treeUri);

            Uri fileUri = selectedDirectory.getUri();
            File directory = new File(fileUri.getPath());
            String[] split = directory.getPath().split(":");

            String filePath = split[2];
            appData.setPath("/" + filePath + "/");
            patternsLocation.setText("/" + filePath + "/");
            mainActivity.displaySites();
            getDialog().dismiss();

        } catch(Exception e) {
            appData.Notify("Error Exception","An error has occurred. Please try again.",getActivity());
        }

    }

}