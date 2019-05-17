package miningsolutions.BlastQA;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BlastPatternFragment extends Fragment {

    TextView name;
    TextView date;
    Button startButton;

    int REQUEST_CODE_SITE = 2;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup parent, Bundle b) {

        View view = li.inflate(R.layout.fragment_blastpattern, parent, false);

        name = (TextView) view.findViewById(R.id.SiteName);
        date = (TextView) view.findViewById(R.id.SiteDate);
        startButton = (Button) view.findViewById(R.id.StartButton);

        Bundle args = getArguments();
        name.setText(args.getString("SiteName"));
        date.setText(args.getString("SiteLastModifiedDate"));

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Starting site " + name.getText());

                Intent intent = new Intent(getActivity(), BlastPatternActivity.class);
                intent.putExtra("SiteName",name.getText());
                intent.putExtra("SiteLastModifiedDate", date.getText());
                startActivityForResult(intent, REQUEST_CODE_SITE);

            }
        });

        return view;
    }

}
