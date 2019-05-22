package miningsolutions.BlastQA.Settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import miningsolutions.BlastQA.AppData;
import miningsolutions.BlastQA.R;

public class ViewAndOutputSettingFragment extends DialogFragment {

    public AppData appData;


    private TabLayout tabLayout;
    private ViewPager viewPagerSettings;
    private ViewOutputSettingsPageAdapter adapter;
    private Button cancel;

  //  public SettingsFragment settingsFragment;
   // public FragmentManager fm;

    public ViewAndOutputSettingFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewoutputsettings, container, false);

        appData = AppData.getAppData();
//        fm = getActivity().getSupportFragmentManager();
//        settingsFragment = (SettingsFragment) fm.findFragmentByTag("fragment_settings");

        tabLayout = (TabLayout)view.findViewById(R.id.tab_settings);
        viewPagerSettings = (ViewPager)view.findViewById(R.id.viewPager2);


        adapter = new ViewOutputSettingsPageAdapter(getActivity(), getChildFragmentManager());

        adapter.addFragment(new ViewSettingsFragment(), "Blast Parameters");
        adapter.addFragment(new ViewSettingsFragment(), "Hole Parameters");
        adapter.addFragment(new OutputSettingsFragment(), "Output Setup");
        viewPagerSettings.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPagerSettings);

        cancel = (Button) view.findViewById(R.id.Cancel);



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getDialog().dismiss();
           }
       });




//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(nameValue.getText().length() != 0) {
//
//                    String name = nameValue.getText().toString();
//
//                    boolean existing = false;
//
//                    // Check if custom data already exist
//                    for(SettingsData settingsData: appData.getSettingsList()) {
//                        if(settingsData.getName().equals(name)) {
//                            existing = true;
//                        }
//                    }
//
//                    if(!existing) {
//
//                        // Create custom data
//                        SettingsData newData = new SettingsData(getContext(),name);
//                        appData.addCustomData(newData);
//
//                        // Close dialog
//                        getDialog().dismiss();
//
//                        // Refresh Settings Fragment
//                        fm.beginTransaction().detach(ViewAndOutputSettingFragment.this).attach(ViewAndOutputSettingFragment.this).commit();
//
//                    } else {
//                        appData.Notify("Notification", "Custom data already exist with the same name. Please try again.", getActivity());
//                    }
//
//                } else {
//                    appData.Notify("Notification", "Please enter name description of the data.",getActivity());
//                }
//
//            }
//        });


    }

    private PagerAdapter buildAdapter() {
        return(new ViewOutputSettingsPageAdapter(getActivity(), getChildFragmentManager()));
    }

}