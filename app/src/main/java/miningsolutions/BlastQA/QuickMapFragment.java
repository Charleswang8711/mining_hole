package miningsolutions.BlastQA;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class QuickMapFragment extends DialogFragment {

    GraphView quickGraph;
    PointsGraphSeries<DataPoint> quickMapSeries;
    double maximumNorthing;
    double maximumEasting;
    double minimumNorthing;
    double minimumEasting;

    public QuickMapFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quickmap, container, false);

        quickGraph = view.findViewById(R.id.quickMap);

        Bundle bundle = getArguments();

        ArrayList<Hole> mapHoles = (ArrayList<Hole>) bundle.getSerializable("quickMapList");
        minimumEasting = bundle.getDouble("minimumEasting");
        maximumEasting = bundle.getDouble("maximumEasting");
        minimumNorthing = bundle.getDouble("minimumNorthing");
        maximumNorthing = bundle.getDouble("maximumNorthing");

        Hole[] holesArr = new Hole[mapHoles.size()];
        holesArr = mapHoles.toArray(holesArr);
        this.sortArray(holesArr);

        quickMapSeries = new PointsGraphSeries<>(holesArr);

        quickGraph.addSeries(quickMapSeries);

        quickMapSeries.toggleCustomTopLabel("id",true);

        int roundedMinimumEasting = getRoundedCoordinate(minimumEasting);
        int roundedMaximumEasting = getRoundedCoordinate(maximumEasting);
        int roundedMinimumNorthing = getRoundedCoordinate(minimumNorthing);
        int roundedMaximumNorthing = getRoundedCoordinate(maximumNorthing);

        quickGraph.getViewport().setMinX(roundedMinimumEasting - 2);
        quickGraph.getViewport().setMaxX(roundedMaximumEasting + 10);

        quickGraph.getViewport().setMinimalViewport(roundedMinimumEasting - 2, roundedMaximumEasting + 10, roundedMinimumNorthing, roundedMaximumNorthing);

        quickGraph.getGridLabelRenderer().setNumHorizontalLabels(8);
        quickGraph.getGridLabelRenderer().setNumVerticalLabels(6);

        quickGraph.getViewport().setScrollable(true);
        quickGraph.getViewport().setScrollableY(true);

        quickGraph.getViewport().setScalable(true);
        quickGraph.getViewport().setScalableY(false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void sortArray(DataPoint[] dataPoints) {

        // Quicksort Algorithm
        quickSort(dataPoints,0,dataPoints.length-1);

    }

    public void quickSort(DataPoint arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex-1);
            quickSort(arr, partitionIndex+1, end);
        }
    }

    private int partition(DataPoint arr[], int begin, int end) {
        double pivot = arr[end].getX();
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if (arr[j].getX() <= pivot) {
                i++;

                DataPoint swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        DataPoint swapTemp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = swapTemp;

        return i+1;
    }

    public int getRoundedCoordinate(double coordinate) {

        StringBuilder x = new StringBuilder(Integer.toString((int)coordinate));

        x.setCharAt(x.length()-1, '0');

        int minAdjustedEasting = Integer.valueOf(x.toString());

        return minAdjustedEasting;
    }

}