package com.utils.gdkcorp.p2sapp;

import android.content.res.Configuration;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout coordinator_layout;
    private AppBarLayout appbarlayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private TabLayout tablayout;
    private ViewPager viewPager;
    private NavigationView mDrawer;
    private FragmentManager fmng;
    private ImageView appbar_imgview;
    private ViewPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Log.d("Height",dpHeight+"");
        Log.d("Width",dpWidth+"");

        appbar_imgview = (ImageView) findViewById(R.id.appbar_imgview);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        coordinator_layout= (CoordinatorLayout) findViewById(R.id.root_coordinator);
        appbarlayout= (AppBarLayout) findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        toolbar= (Toolbar) findViewById(R.id.app_bar);
        tablayout= (TabLayout) findViewById(R.id.tab_layout);
        viewPager= (ViewPager) findViewById(R.id.view_pager);
        mDrawer= (NavigationView) findViewById(R.id.navigation_drawer);

        setSupportActionBar(toolbar);

        mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.Drawer_open,R.string.Drawer_Close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        fmng=getSupportFragmentManager();
        mPagerAdapter = new ViewPagerAdapter(fmng);
        viewPager.setAdapter(mPagerAdapter);

        tablayout.setTabsFromPagerAdapter(mPagerAdapter);
        tablayout.setupWithViewPager(viewPager);
        tablayout.getTabAt(0).setIcon(R.drawable.home_selector);
        tablayout.getTabAt(1).setIcon(R.drawable.truck_selector);
        tablayout.getTabAt(2).setIcon(R.drawable.location_selector);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter{

        BlankFragment fragment =null;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BlankFragment.newInstance("","");
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0){
                return "HOME";
            }else if(position==1){
                return "UPDATES";
            }else if(position==2){
                return "TRACKING";
            }
            else{
                return "TAB";
            }
        }
    }
}
