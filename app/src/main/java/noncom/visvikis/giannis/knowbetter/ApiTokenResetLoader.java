package noncom.visvikis.giannis.knowbetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiTokenResetLoader extends AsyncTaskLoader<Void>
{


    String token;


    public ApiTokenResetLoader(@NonNull Context context, String token)
    {
        super(context);

        this.token = token;
    }


    @Override
    protected void onStartLoading()
    {
        super.onStartLoading();

        forceLoad();
    }


    @Nullable
    @Override
    public Void loadInBackground()
    {

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {

            URL url = new URL("https://opentdb.com/api_token.php?command=reset&token=" + token);

            urlConnection = (HttpURLConnection) url.openConnection();

            inputStream = urlConnection.getInputStream();
        }
        catch (MalformedURLException mUrl)
        {
            Log.e("ApiTknRstLdr", mUrl.getMessage());
        }
        catch (IOException ioe)
        {
            Log.e("ApiTknRstLdr", ioe.getMessage());
        }
        finally {

            if (inputStream != null)
            {
                try {
                    inputStream.close();
                }
                catch (IOException io){}
            }

            if(urlConnection != null)
            {
                urlConnection.disconnect();
            }

        }

        return null;
    }


}
