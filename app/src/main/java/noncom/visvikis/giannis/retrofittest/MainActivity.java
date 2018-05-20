

package noncom.visvikis.giannis.retrofittest;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;



public class MainActivity extends AppCompatActivity implements InterFragmentCommunication, ProviderInstaller.ProviderInstallListener
{

    private final String RETAINED_FRAGMENT_TAG = "RETAINED_FRAGMENT_TAG";
    private final String MENU_FRAGMENT_TAG = "MENU_FRAGMENT_TAG";
    private final String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG";
    private final String DRAWER_OPEN_TAG = "DRAWER_OPEN_TAG";
    private final String DRAWER_PRESENT_TAG = "DRAWER_PRESENT_TAG";
    private final String API_TOKEN_TAG = "API_TOKEN_TAG";

    private static final int ERROR_DIALOG_REQUEST_CODE = 0;

    private boolean mRetryProviderInstall;

    private final int TOKEN_LOADER_CODE = 1;
    private final int RESET_TOKEN_LOADER_CODE = 3;
    private final int CONNECTION_LOADER_CODE = 2;

    private String apiToken = ""; //start empty, will change inside onCreate;

    private boolean userSeenDrawer = false;
    private boolean isDrawerPresent = false;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private RetainedFragment retainedFragment;
    private MenuFragment menuFragment;
    private MainFragment mainFragment;

    //Pre lollipop throw Exception SSL handshake. Try to update. If successful, then this gets set to true
    private boolean updatedSecurity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        //check for internet connection
        checkInternetConnection();

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

            mainFragment = new MainFragment();

            fragmentTransaction.add(R.id.menu_frag_place, menuFragment, MENU_FRAGMENT_TAG);
            fragmentTransaction.add(R.id.main_frag_place, mainFragment, MAIN_FRAGMENT_TAG);

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
            apiToken = savedInstanceState.getString(API_TOKEN_TAG);

            updatedSecurity = savedInstanceState.getBoolean("SECURITY_DOWNLOADED");
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


        //set up the Retrofit



        if(isDrawerPresent) //the adview belongs here in drawer layout. In menu fragment otherwise
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
        outState.putString(API_TOKEN_TAG, apiToken);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            outState.putBoolean("SECURITY_DOWNLOADED", updatedSecurity);
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
    public void closeTheDrawer()
    {
        if(isDrawerPresent && mDrawerLayout.isDrawerOpen(mNavigationView))
            mDrawerLayout.closeDrawer(mNavigationView);
    }



    @Override
    public void openTheDrawer()
    {
        if(isDrawerPresent && !mDrawerLayout.isDrawerOpen(mNavigationView))
            mDrawerLayout.openDrawer(mNavigationView);
    }



    @Override
    public String getApiToken()
    {
        return this.apiToken;
    }


    @Override
    public MenuFragment getMenuFragment()
    {
        return this.menuFragment;
    }


    @Override
    public MainFragment getMainFragment()
    {
        return this.mainFragment;
    }



    @Override
    public void resetTheToken(final String query)
    {
        getSupportLoaderManager().restartLoader(RESET_TOKEN_LOADER_CODE, null, new LoaderManager.LoaderCallbacks<Void>()
        {
            @NonNull
            @Override
            public Loader<Void> onCreateLoader(int id, @Nullable Bundle args)
            {
                return new ApiTokenResetLoader(getApplicationContext(), apiToken);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Void> loader, Void data)
            {
                //token reset. request questions from api
                menuFragment.makeRetrofitCall(query);
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Void> loader)
            {

            }
        });
    }



    /**
     * Check for internet connection. If not present, start another activity asking to activate it.
     * If present, check for SDK info. If pre lollipop apply patch from google play services for secure connection
     * Finally reques token from api to start downloading the quiz questions
     */
    private void checkInternetConnection()
    {

        getSupportLoaderManager().restartLoader(CONNECTION_LOADER_CODE, null, new android.support.v4.app.LoaderManager.LoaderCallbacks<Boolean>()
        {
            @NonNull
            @Override
            public android.support.v4.content.Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args)
            {
                return new ConnectivityCheckLoader(MainActivity.this);
            }

            @Override
            public void onLoadFinished(@NonNull android.support.v4.content.Loader<Boolean> loader, Boolean data)
            {

                if(data && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP || updatedSecurity) )
                {
                    if(apiToken.equalsIgnoreCase(""))
                        requestToken();
                }
                //APPLY SECURITY PATCH FOR PRE LOLLIPOP DEVICES
                else if(data && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && !updatedSecurity)
                {
                    Log.e("PATCH_REQUEST", "requesting patch");
                    ProviderInstaller.installIfNeededAsync(getApplicationContext(), MainActivity.this);
                }
                else if(!data) // no active connection
                {
                    Intent noWifiIntent = new Intent(getApplicationContext(), NoConnectionActivity.class);
                    startActivity(noWifiIntent);
                    MainActivity.this.finish();
                }
            }

            @Override
            public void onLoaderReset(@NonNull android.support.v4.content.Loader<Boolean> loader)
            {

            }
        });
    }




    @Override
    public void setTheQuiz(ApiResponse response, String query)
    {
        /*
        Log.e("Response code is : ", response.getResponseCode());

        Log.e("==========//===========", "==========//==========");

        for(QuizQuestion question : response.getQuestions())
        {
            Log.e("Type : ",  question.getCategory());
            Log.e("Question is : ",  question.getQuestion());
            Log.e("Correct answer is : ", question.getCorrectAnswer());

            Log.e("==========//===========", "==========//==========");
        }

        */

        String responseCode = response.getResponseCode();

        if (responseCode.equalsIgnoreCase("4") || response.getResponseCode().equalsIgnoreCase("1"))
        {
            //show a dialog and inform the user that the questions will recycle themselves
            ResetDialog resetDialog = new ResetDialog();
            Bundle args = new Bundle();
            args.putString("QUERY", query);
            resetDialog.setArguments(args);

            resetDialog.show(getSupportFragmentManager(), "RESET_DIALOG");

        }
        else if (responseCode.equalsIgnoreCase("0"))
        {
            //TODO success, setup the quiz

        }


    }




    /**
     * Asynchronous call to the api for token to prevent same questions asked again and again
     */
    private void requestToken()
    {
        //request TOKEN. Checked already whether I got one yet or not

        getSupportLoaderManager().initLoader(TOKEN_LOADER_CODE, null, new android.support.v4.app.LoaderManager.LoaderCallbacks<Object[]>()
        {
            @NonNull
            @Override
            public android.support.v4.content.Loader<Object[]> onCreateLoader(int id, @Nullable Bundle args)
            {
                return new ApiTokenLoader(MainActivity.this);
            }

            @Override
            public void onLoadFinished(@NonNull android.support.v4.content.Loader<Object[]> loader, Object[] data)
            {
                if(data[0] != null) // if it is null, the connection was not successful, something went wrong
                {
                    apiToken = (String) data[1];

                    Log.e("TOKEN", apiToken);
                }
                else
                {
                    Intent noResponseIntent = new Intent(getApplicationContext(), NoResponseActivity.class);
                    startActivity(noResponseIntent);
                    MainActivity.this.finish();
                }

            }

            @Override
            public void onLoaderReset(@NonNull android.support.v4.content.Loader<Object[]> loader)
            {

            }
        });

    }



    /**
     * Security patch applied successfully for pre lollipop device. Ask for api token
     */
    @Override
    public void onProviderInstalled()
    {
        updatedSecurity = true;

        //now start interaction with API
        if(apiToken.equalsIgnoreCase(""))
            requestToken();

    }



    @Override
    public void onProviderInstallFailed(int i, Intent intent)
    {

        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        if (availability.isUserResolvableError(i)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            availability.showErrorDialogFragment(
                    this, i,
                    ERROR_DIALOG_REQUEST_CODE,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // The user chose not to take the recovery action
                            onProviderInstallerNotAvailable();
                        }
                    });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GoogleApiAvailability.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which will cause the fragment to delay until
            // onPostResume.
            mRetryProviderInstall = true;
        }
    }



    /**
     * On resume, check to see if we flagged that we need to reinstall the
     * provider.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mRetryProviderInstall) {
            // We can now safely retry installation.
            ProviderInstaller.installIfNeededAsync(this, this);
        }

        mRetryProviderInstall = false;
    }


    /**
     * attempts to apply security patch failed
     */
    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.

        Intent patchFailedIntent = new Intent(this, PatchFailedActivity.class);
        startActivity(patchFailedIntent);
        this.finish();

    }







}



