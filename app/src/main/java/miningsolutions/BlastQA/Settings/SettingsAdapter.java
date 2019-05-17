package miningsolutions.BlastQA.Settings;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import miningsolutions.BlastQA.AppData;
import miningsolutions.BlastQA.R;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.DataHolder> {

    private List<SettingsData> mDataset;
    private RadioGroup radioGroup;
    private AppData appData;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SettingsAdapter(List<SettingsData> myDataset) {
        mDataset = myDataset;
        appData = AppData.getAppData();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SettingsAdapter.DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_settingsdata, parent, false);

        DataHolder vh = new DataHolder(view);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DataHolder holder, int position) {

        SettingsData sdata = mDataset.get(position);

        TextView desc = (TextView) holder.view.findViewById(R.id.Description);
        radioGroup = (RadioGroup) holder.view.findViewById(R.id.RadioSettings);
        RadioButton numeric = (RadioButton) holder.view.findViewById(R.id.Numeric);
        RadioButton bool = (RadioButton) holder.view.findViewById(R.id.Boolean);

        desc.setText(sdata.getName());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButton) {

                RadioButton rb=(RadioButton)holder.view.findViewById(radioButton);

                // Todo: Should add dialog to confirm change here stating that the values will be erased.
                // See: BlastPatternActivity.validateSettingsData()

                if(rb.getText().toString().equals("Numeric")) {
                    sdata.setDatatype("Numeric");
                    sdata.setDatatypeChanged(true);

                } else {
                    sdata.setDatatype("Boolean");
                    sdata.setDatatypeChanged(true);
                }

            }
        });

        if(sdata.getDatatype().equals("Numeric")) {
            numeric.setChecked(true);
        } else {
            bool.setChecked(true);
        }

        if(appData.getSelectedPosition() == null) {
            appData.setSelectedPosition(position);
        }

        holder.itemView.setBackgroundColor(appData.getSelectedPosition() == position ? Color.GREEN : Color.TRANSPARENT);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class DataHolder extends RecyclerView.ViewHolder {

        public View view;

        public DataHolder(View view) {
            super(view);
            this.view = view;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Below line is just like a safety check, because sometimes holder could be null,
                    // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && appData.getSelectedPosition() != null) {

                        // Updating old as well as new positions
                        notifyItemChanged(appData.getSelectedPosition());
                        appData.setSelectedPosition(getAdapterPosition());
                        notifyItemChanged(appData.getSelectedPosition());

                    }
                }
            });

        }

    }

}