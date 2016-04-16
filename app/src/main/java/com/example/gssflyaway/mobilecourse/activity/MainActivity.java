package com.example.gssflyaway.mobilecourse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amap.api.navi.AMapNavi;
import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.fragment.MainFragment;
import com.example.gssflyaway.mobilecourse.fragment.ReservationFragment;
import com.example.gssflyaway.mobilecourse.model.ParkModel;
import com.example.gssflyaway.mobilecourse.model.UserModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @Bind(R.id.fab)
    public FloatingActionButton fab;
    @Bind(R.id.navigation)
    public NavigationView navigationView;

    public CircleImageView mAvatar;
    public TextView mUsername;

    private int currentSelected;  // 当前选中的导航菜单id



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupToolbar(toolbar);
        setupFloatingActionButton(fab);
        setupDrawerLayout();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new MainFragment());
        transaction.commit();

        init();

        //点击登录
        View headerView = navigationView.getHeaderView(0);
        CircleImageView  LoginIma=(CircleImageView)headerView.findViewById(R.id.profile_image);
        if(LoginIma==null) {
            Log.e("err", "login is null");
        }
        else {
            LoginIma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent t = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(t);
                    AMapNavi.getInstance(getApplicationContext()).startNavi(AMapNavi.GPSNaviMode);
//                    Intent t = new Intent(getApplicationContext(), AMapActivity.class);
//                    startActivity(t);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UserModel.getInstance().isLogin(getApplicationContext())){
            String token = UserModel.getInstance().getToken(getApplicationContext());
            UserModel.getInstance().obGetUserInfo(token)
                    .subscribe(new Subscriber<Map>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Map map) {
                            String avatar = map.get(UserModel.AVATAR).toString();
                            String userName = map.get(UserModel.USERNAME).toString();
                            ImageLoader.getInstance().displayImage(avatar, mAvatar);
                            mUsername.setText(userName);
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar(Toolbar toolbar){
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupFloatingActionButton(FloatingActionButton fab){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReservationActivity.class);
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    private void init(){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void setupDrawerLayout(){
        currentSelected = R.id.index;
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        mAvatar = (CircleImageView) headerView.findViewById(R.id.profile_image);
        mUsername = (TextView) headerView.findViewById(R.id.username);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        if(id == currentSelected)
            return true;
        currentSelected = id;
        switch (id){
            case R.id.index:
                item.setChecked(true);
                fragment = new MainFragment();
                break;
            case R.id.reservation:
                item.setChecked(true);
                fragment = new ReservationFragment();
                break;
            case R.id.navi:
                Intent t = new Intent(getApplicationContext(), AMapActivity.class);
                startActivity(t);
                return false;
        }
        if(fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            mDrawerLayout.closeDrawers();
        }
        return false;
    }
}
