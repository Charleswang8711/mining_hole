package miningsolutions.BlastQA.Settings;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import miningsolutions.BlastQA.R;

public class ViewSettingsFragment extends Fragment {

    private View rootView;
    private List<ViewSettingsModel> modelList;
    private ViewSettingsRecyclerViewAdapter Recycleradapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_viewsettings, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        Recycleradapter = new ViewSettingsRecyclerViewAdapter();
        recyclerView.setAdapter(Recycleradapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateSheetParams();


    }
    private void populateSheetParams()
    {
        modelList = new ArrayList<>();

        for(int i = 0; i<10; i++)
        {
            StringBuilder base = new StringBuilder("item");
            ViewSettingsModel Model = new ViewSettingsModel(base.append(String.valueOf(i)).toString(),base.append(String.valueOf(i)).toString(),base.append(String.valueOf(i)).toString(),base.append(String.valueOf(i)).toString(),0);
            modelList.add(Model);
        }

        Recycleradapter.submitList(modelList);

    }


}
