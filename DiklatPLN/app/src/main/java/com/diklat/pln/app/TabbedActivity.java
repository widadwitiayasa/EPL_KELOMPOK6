package com.diklat.pln.app;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.diklat.pln.app.Inbox.ListMessage.InboxFragment;
import com.diklat.pln.app.Pengumuman.PengumumanFragment;
import com.diklat.pln.app.Profile.ProfileFragment;
import com.diklat.pln.app.Subordinate.ListSubordinate.SubordinateFragment;
import com.readystatesoftware.viewbadger.BadgeView;

public class TabbedActivity extends BaseActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public ChangeIpUtils cIputils;
    public boolean activeActivity;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String[] tabsTitles = {"Pengumuman","Profil","Kotak Pesan","List Bawahan"};
    private int[] iconSelected = {R.drawable.ic_notifications_black_24dp,R.drawable.ic_person_black_24dp,R.drawable.ic_email_black_24dp,R.drawable.ic_group_black_24dp};
    public static TabLayout tabLayout;
    BadgeView tabPengumuman;
    BadgeView tabInbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(opcl);
        cIputils = changeIpUtils;
        activeActivity = isActive;
//
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setCustomView(R.layout.badge);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_person_white_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_group_white_24dp);
        tabLayout.getTabAt(2).setCustomView(R.layout.badge_2);

//        startnotificationService();

//        AlarmReceiverLifeLog alarm = new AlarmReceiverLifeLog();
//        alarm.setAlarm(getBaseContext());

        //startnotificationService();
        startService(new Intent(this,NotificationServices.class));

        //wrap your stuff in a componentName
//        ComponentName mServiceComponent = new ComponentName(getBaseContext(), NotificationServices.class);
//// set up conditions for the job
//        JobInfo task = new JobInfo.Builder(101, mServiceComponent)
//                .setPeriodic(10000)
//                .setRequiresCharging(true) // default is "false"
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // Parameter may be "ANY", "NONE" (=default) or "UNMETERED"
//                .build();
//// inform the system of the job
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        jobScheduler.schedule(task);


        String tab;
        Bundle bundle;
        setBadge();
//        if(getIntent().getExtras()!=null){
//            bundle = getIntent().getExtras();
//            tab = bundle.getString("notifpesan");
//            mViewPager.setCurrentItem(Integer.parseInt(tab));
//        }
        setTitle("Pengumuman");
    }
    ViewPager.OnPageChangeListener opcl = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
           setTitle(tabsTitles[position]);
            ImageView imgTab0 = (ImageView)tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tablayout_img);
            ImageView imgTab2 = (ImageView)tabLayout.getTabAt(2).getCustomView().findViewById(R.id.tablayout_img_2);
           if(position==0){
               imgTab0.setImageResource(iconSelected[0]);
               tabLayout.getTabAt(1).setIcon(R.drawable.ic_person_white_24dp);
               imgTab2.setImageResource(R.drawable.ic_email_white_24dp);
               tabLayout.getTabAt(3).setIcon(R.drawable.ic_group_white_24dp);
           }
           else if(position==1){
               imgTab0.setImageResource(R.drawable.ic_notification_white_24dp);
               tabLayout.getTabAt(1).setIcon(iconSelected[1]);
               imgTab2.setImageResource(R.drawable.ic_email_white_24dp);
               tabLayout.getTabAt(3).setIcon(R.drawable.ic_group_white_24dp);
           }
           else if(position==2){
               imgTab0.setImageResource(R.drawable.ic_notification_white_24dp);
               imgTab2.setImageResource(iconSelected[2]);
               tabLayout.getTabAt(3).setIcon(R.drawable.ic_group_white_24dp);
               tabLayout.getTabAt(1).setIcon(R.drawable.ic_person_white_24dp);
           }
           else if(position==3){
               imgTab0.setImageResource(R.drawable.ic_notification_white_24dp);
               tabLayout.getTabAt(3).setIcon(iconSelected[3]);
               tabLayout.getTabAt(1).setIcon(R.drawable.ic_person_white_24dp);
               imgTab2.setImageResource(R.drawable.ic_email_white_24dp);
           }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public class SectionsPagerAdapter extends FragmentPagerAdapter  {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
           switch (position){
               case 0:
                   PengumumanFragment pengumumanFragment = new PengumumanFragment();
                   return pengumumanFragment;
               case 1:
                   ProfileFragment profilePage = new ProfileFragment();
                   return profilePage;
               case 2:
                   InboxFragment inboxPage = new InboxFragment();
                   return inboxPage;
               case 3:
                   SubordinateFragment subordinatePage = new SubordinateFragment();
                   return subordinatePage;
               default: return null;
           }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
    private void setBadge(){
        View tab0 = tabLayout.getTabAt(0).getCustomView();
        View tab2 = tabLayout.getTabAt(2).getCustomView();
        tabPengumuman = new BadgeView(this,tab0);
        tabInbox = new BadgeView(this,tab2);
        tabPengumuman.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        tabInbox.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        tabInbox.show();
        tabPengumuman.show();
    }
    public void updateBadge(String badge, String parameter){
        switch (parameter){
            case "pengumuman":
            {
                if(badge.equals("0"))
                    tabPengumuman.hide();
                else{
                    tabPengumuman.setText(badge);
                    tabPengumuman.show();
                }
                break;
            }
            case "inbox":
            {
                if(badge.equals("0"))
                    tabInbox.hide();
                else
                {
                    tabInbox.show();
                    tabInbox.setText(badge);
                    Log.d("InboxBadge",badge);
                }
            }
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.ip_local:
//                Log.d("URL",Konstanta.URL);
//                Konstanta.URL = Konstanta.IPLOCAL;
//                Log.d("URL",Konstanta.URL);
//                item.setChecked(true);
//                break;
//            case R.id.ip_public:
//                Konstanta.URL = Konstanta.IPPUBLIC;
//                item.setChecked(true);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_menu,menu);
//        return true;
//    }

    private void startnotificationService(){
//        Intent intent = new Intent(getBaseContext(),AlarmReceiverLifeLog.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,60000,300000,pendingIntent);

//        startService(new Intent(getBaseContext(),NotificationServices.class));
//        PackageManager pm  = getPackageManager();
//        ComponentName componentName = new ComponentName(getBaseContext(), AlarmReceiverLifeLog.class);
//        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
        AlarmReceiverLifeLog alaram = new AlarmReceiverLifeLog();
        alaram.setAlarm(getBaseContext());
    }
}
