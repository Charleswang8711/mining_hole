package miningsolutions.BlastQA;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HoleDiagramFragment extends Fragment {

    TextView SurfaceRL;
    TextView TargetRL;
    TextView StemmingColumn;
    TextView SubDrill;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup parent, Bundle b) {

        View view = li.inflate(R.layout.fragment_holediagram, parent, false);

        SurfaceRL = (TextView) view.findViewById(R.id.SurfaceRL);
        TargetRL = (TextView) view.findViewById(R.id.TargetRL);
        StemmingColumn = (TextView) view.findViewById(R.id.StemmingColumn);
        SubDrill = (TextView) view.findViewById(R.id.SubDrill);

        return view;
    }

    public void updateDiagram(double surfaceRL, double targetRL, double stem, double subDrill) {

        SurfaceRL.setText(String.valueOf(surfaceRL) + " mRL");
        TargetRL.setText(String.valueOf(targetRL) + " mRL");
        StemmingColumn.setText(String.valueOf(stem) + " Stem");
        SubDrill.setText(String.valueOf(subDrill) + " Sub");

    }

}
