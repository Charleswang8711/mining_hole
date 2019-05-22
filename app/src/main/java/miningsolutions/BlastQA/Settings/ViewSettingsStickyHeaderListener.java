package miningsolutions.BlastQA.Settings;

import android.view.View;

public interface ViewSettingsStickyHeaderListener {

    Integer getHeaderPositionForItem(Integer itemPosition);

    Integer getHeaderLayout(Integer headerPosition);

    void bindHeaderData(View header, Integer headerPosition);

    Boolean isHeader(Integer itemPosition);
}
