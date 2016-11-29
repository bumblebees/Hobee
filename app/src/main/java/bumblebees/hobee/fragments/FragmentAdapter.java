package bumblebees.hobee.fragments;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter{
    int tabCount =3;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new EventsMainFragment();
            case 1:
                return new EventsBrowseFragment();
            case 2:
                return new EventsHistoryFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0: return "Home";
            case 1: return "Browse";
            case 2: return "History";
            default: return "";
        }
    }
}
