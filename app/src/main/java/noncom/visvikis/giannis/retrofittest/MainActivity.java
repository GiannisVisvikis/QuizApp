

package noncom.visvikis.giannis.retrofittest;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity implements InterFragmentCommunication
{

    private final String API_TOKEN_REQUEST_URL = "https://opentdb.com/api_token.php?command=request";

    private final String RETAINED_FRAGMENT_TAG = "RETAINED_FRAGMENT_TAG";
    private final String MENU_FRAGMENT_TAG = "MENU_FRAGMENT_TAG";
    private final String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG";
    private final String DRAWER_OPEN_TAG = "DRAWER_OPEN_TAG";
    private final String DRAWER_PRESENT_TAG = "DRAWER_PRESENT_TAG";

    private boolean userSeenDrawer = false;
    private boolean isDrawerPresent = false;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private RetainedFragment retainedFragment;
    private MenuFragment menuFragment;
    private MainFragment mainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        isDrawerPresent = root.getTag() != null;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null){

            //TODO instantiate a retained fragment to keep the adapter used by the app in orientation changes. Get it from there if null and set it to the recycler view
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            menuFragment = new MenuFragment();

            if(isDrawerPresent)
            {
                Bundle menuArgs = new Bundle();
                menuArgs.putString(MenuFragment.ARGS_TAG, "whatever");
                menuFragment.setArguments(menuArgs);
            }

            //mainFragment = new MainFragment();

            fragmentTransaction.add(R.id.menu_frag_place, menuFragment, MENU_FRAGMENT_TAG);
            //fragmentTransaction.add(R.id.main_frag_place, mainFragment, MAIN_FRAGMENT_TAG);

            retainedFragment = new RetainedFragment();
            fragmentTransaction.add(retainedFragment, RETAINED_FRAGMENT_TAG);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

        }
        else{
            retainedFragment = (RetainedFragment) fragmentManager.findFragmentByTag(RETAINED_FRAGMENT_TAG);
            menuFragment = (MenuFragment) fragmentManager.findFragmentByTag(MENU_FRAGMENT_TAG);
            mainFragment = (MainFragment) fragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG);

            userSeenDrawer = savedInstanceState.getBoolean(DRAWER_OPEN_TAG);
            isDrawerPresent = savedInstanceState.getBoolean(DRAWER_PRESENT_TAG);
        }


        if(isDrawerPresent)//small screen mode, navigation drawer present. Set the drawer and handle start click here, handle in menu fragment otherwise
        {

            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

            mNavigationView = findViewById(R.id.nav_view);
            mDrawerLayout = findViewById(R.id.drawer_layout);

            mDrawerLayout.openDrawer(mNavigationView);

            mActionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
            {
                public void onDrawerClosed(View view)
                {
                    //Toast.makeText(getApplicationContext(), "Drawer is closed", Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView)
                {

                    userSeenDrawer = true;
                    //Toast.makeText(getApplicationContext(), "Drawer is open", Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();
                }
            };

            mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);


            if(!userSeenDrawer)
                mDrawerLayout.openDrawer(mNavigationView);

        }


        if(isDrawerPresent) //the adview belongs here in drawer. In menu fragment otherwise
        {
            //Remember to uncomment in the menu fragment as well
            //MobileAds.initialize(getActivity(), put the app id from admob here);

            // Load an ad into the AdMob banner view.
            AdView adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);
        }

    }




    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState)
    {
        super.onPostCreate(savedInstanceState, persistentState);

        mActionBarDrawerToggle.syncState();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(DRAWER_OPEN_TAG, userSeenDrawer);
        outState.putBoolean(DRAWER_PRESENT_TAG, isDrawerPresent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed()
    {

        if (isDrawerPresent && !mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.openDrawer(mNavigationView);
        }
        else {
            super.onBackPressed();
        }
    }


    @Override
    public RecyclerView.Adapter getTheAdapter()
    {
        return retainedFragment.getTheAdapter();
    }

    @Override
    public RetainedFragment getRetainedFragment(){
        return retainedFragment;
    }


    @Override
    public void closeTheDrawer(){
        mDrawerLayout.closeDrawer(mNavigationView);
    }


}
