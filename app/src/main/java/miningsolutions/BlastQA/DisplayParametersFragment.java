package miningsolutions.BlastQA;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class DisplayParametersFragment extends DialogFragment {

    public CheckBox checkBoxID;
    public CheckBox checkBoxDepth;
    public CheckBox checkBoxCharge;
    public CheckBox checkBoxStem;
    public Button confirm;

    int parameterCount;

    public DisplayParametersFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_displayparameters, container, false);

        checkBoxID = (CheckBox) view.findViewById(R.id.checkbox_id);
        checkBoxDepth = (CheckBox) view.findViewById(R.id.checkbox_depth);
        checkBoxCharge = (CheckBox) view.findViewById(R.id.checkbox_charge);
        checkBoxStem = (CheckBox) view.findViewById(R.id.checkbox_stem);
        confirm = (Button) view.findViewById(R.id.confirm);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        checkBoxID.setChecked(args.getBoolean("id"));
        checkBoxDepth.setChecked(args.getBoolean("depth"));
        checkBoxCharge.setChecked(args.getBoolean("charge"));
        checkBoxStem.setChecked(args.getBoolean("stem"));

        parameterCount = 2;

        checkBoxID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                System.out.println(parameterCount);

                if(parameterCount >= 2) {

                    if(b == true) {
                        compoundButton.setChecked(!b);
                    } else {
                        parameterCount = parameterCount - 1;
                    }

                } else {

                    if(b == true) {
                        parameterCount = parameterCount + 1;
                    } else {
                        parameterCount = parameterCount - 1;
                    }
                }

            }
        });

        checkBoxDepth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                System.out.println(parameterCount);

                if(parameterCount >= 2) {

                    if(b == true) {
                        compoundButton.setChecked(!b);
                    } else {
                        parameterCount = parameterCount - 1;
                    }

                } else {

                    if(b == true) {
                        parameterCount = parameterCount + 1;
                    } else {
                        parameterCount = parameterCount - 1;
                    }
                }

            }
        });

        checkBoxCharge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                System.out.println(parameterCount);

                if(parameterCount >= 2) {

                    if(b == true) {
                        compoundButton.setChecked(!b);
                    } else {
                        parameterCount = parameterCount - 1;
                    }

                } else {

                    if(b == true) {
                        parameterCount = parameterCount + 1;
                    } else {
                        parameterCount = parameterCount - 1;
                    }
                }

            }
        });

        checkBoxStem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                System.out.println(parameterCount);

                if(parameterCount >= 2) {

                    if(b == true) {
                        compoundButton.setChecked(!b);
                    } else {
                        parameterCount = parameterCount - 1;
                    }

                } else {

                    if(b == true) {
                        parameterCount = parameterCount + 1;
                    } else {
                        parameterCount = parameterCount - 1;
                    }
                }

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                QAQCActivity activity = (QAQCActivity) getActivity();

                // Set Top Label
                if(checkBoxID.isChecked() == true) {
                    activity.setTopLabel("id");
                } else if(checkBoxDepth.isChecked() == true) {
                    activity.setTopLabel("depth");
                } else if(checkBoxCharge.isChecked() == true) {
                    activity.setTopLabel("charge");
                } else if(checkBoxStem.isChecked() == true) {
                    activity.setTopLabel("stem");
                }

                // Set Bottom Label
                if(checkBoxStem.isChecked() == true) {
                    activity.setBottomLabel("stem");
                } else if(checkBoxCharge.isChecked() == true) {
                    activity.setBottomLabel("charge");
                } else if(checkBoxDepth.isChecked() == true) {
                    activity.setBottomLabel("depth");
                } else if(checkBoxID.isChecked() == true) {
                    activity.setBottomLabel("id");
                }

                activity.getSeries().redraw();

                getDialog().dismiss();

            }
        });

    }

}