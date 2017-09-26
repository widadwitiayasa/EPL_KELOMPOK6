package com.diklat.pln.app.Inbox.ListMessage;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;

import com.diklat.pln.app.BaseActivity;
import com.diklat.pln.app.ChangeIpUtils;
import com.diklat.pln.app.IjinTengahHari;
import com.diklat.pln.app.R;

public class ActivityPerijinan extends BaseActivity {

    public ChangeIpUtils cIputils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perijinan);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs_perijinan);
        ViewPager pager = (ViewPager) findViewById(R.id.container_perijinan);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Form Ajukan Ijin");
        cIputils = changeIpUtils;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:finish();break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {
        public TabsPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);

        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 : {
                    return new IjinHariPenuh();
                }
                case 1: {
                    return new IjinTengahHari();
                }
                default: return null;
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "Ijin/Cuti";
                case 1: return "Lupa Absen";
                default: return null;
            }
        }
    }
}
