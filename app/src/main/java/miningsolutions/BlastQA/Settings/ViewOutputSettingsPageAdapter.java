package miningsolutions.BlastQA.Settings;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewOutputSettingsPageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    Context ctxt=null;

    ViewOutputSettingsPageAdapter(Context ctxt,FragmentManager fm) {
        super(fm);
        this.ctxt=ctxt;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        try{
            super.restoreState(state, loader);
        }catch (NullPointerException e){
            // null caught
        }
    }
}
