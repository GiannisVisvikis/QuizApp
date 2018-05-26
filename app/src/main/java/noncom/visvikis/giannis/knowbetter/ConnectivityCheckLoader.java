package noncom.visvikis.giannis.knowbetter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

public class ConnectivityCheckLoader extends AsyncTaskLoader<Boolean>
{


    private Context context;

    public ConnectivityCheckLoader(@NonNull Context context)
    {
        super(context);
        this.context = context;
    }


    @Override
    protected void onStartLoading()
    {
        super.onStartLoading();

        forceLoad();
    }

    @Nullable
    @Override
    public Boolean loadInBackground()
    {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }


}
