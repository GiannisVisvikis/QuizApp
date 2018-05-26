package noncom.visvikis.giannis.knowbetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class ApiTokenLoader extends AsyncTaskLoader<Object[]>
{

    private Object[] cached = null;


    public ApiTokenLoader(@NonNull Context context)
    {
        super(context);
    }



    @Override
    protected void onStartLoading()
    {
        super.onStartLoading();

        if(cached != null){
            deliverResult(cached);
        }
        else {
            forceLoad();
        }
    }



    @Nullable
    @Override
    public Object[] loadInBackground()
    {
        Object[] result = new Object[2];

        HttpURLConnection con = null;
        StringBuilder sb = null;
        BufferedReader br = null;

        try{

            URL url = new URL(ApiInterface.API_TOKEN_REQUEST_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);

            boolean connectionOk =  (con.getResponseCode() == HttpURLConnection.HTTP_OK);

            if(connectionOk) {

                result[0] = connectionOk;

                sb = new StringBuilder();
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                String token = "";

                try {

                    JSONObject root = new JSONObject(sb.toString());
                    token = root.getString("token");

                    result[1] = token;
                }
                catch (JSONException je) //just in case something goes wrong
                {
                    Log.e("ApiTknLdr/JSNXcptn", je.getMessage());
                }

            }

        }
        catch (SocketTimeoutException sout)
        {
            Log.e("ApiTknLdr/TmOtExc", sout.getMessage());
        }
        catch (MalformedURLException mu)
        {
            Log.e("ApiTknLdr/MlfrmdUrlExc", mu.getMessage());
        }
        catch (IOException io)
        {
            Log.e("ApiTknLdrIOxcptn", io.getMessage());
        }
        finally {

            if(con != null)
                con.disconnect();

            if(br != null){

                try{
                    br.close();
                }
                catch (IOException io)
                {
                    Log.e("ApiTokenLoader", io.getMessage());
                }
            }

        }

        return result ;
    }



    @Override
    public void deliverResult(@Nullable Object[] data)
    {
        if(cached == null)
            cached = data;

        super.deliverResult(data);
    }

}
