package miningsolutions.BlastQA.Settings;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import miningsolutions.BlastQA.R;

public class ViewSettingsRecyclerViewAdapter extends ListAdapter<ViewSettingsModel, RecyclerView.ViewHolder> {

     ViewSettingsRecyclerViewAdapter() {

        super(ModelDiffUtilCallback);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_viewsettings_recycler_item, viewGroup, false);
        return new LineViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ((LineViewHolder) viewHolder).bind(getItem(i));

    }


    class LineViewHolder extends RecyclerView.ViewHolder {
         TextView item;
         TextView sheetName;
         TextView startCell;
         TextView endCell;

         LineViewHolder(@NonNull View itemView) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.Item);
            sheetName = (TextView) itemView.findViewById(R.id.SheetName);
            startCell = (TextView) itemView.findViewById(R.id.StartCell);
            endCell = (TextView) itemView.findViewById(R.id.EndCell);
        }

         void bind(ViewSettingsModel model) {
            item.setText(model.Item);
            sheetName.setText(model.SheetName);
            startCell.setText(model.StartCell);
            endCell.setText(model.EndCell);

        }
    }



    private static final DiffUtil.ItemCallback<ViewSettingsModel> ModelDiffUtilCallback =
            new DiffUtil.ItemCallback<ViewSettingsModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull ViewSettingsModel model, @NonNull ViewSettingsModel t1) {
                    return model.Item.equals(t1.Item);
                }

                @Override
                public boolean areContentsTheSame(@NonNull ViewSettingsModel model, @NonNull ViewSettingsModel t1) {
                    return model.equals(t1);
                }
            };

}
