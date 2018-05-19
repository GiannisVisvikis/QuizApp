
package noncom.visvikis.giannis.retrofittest;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MenuFragment extends Fragment
{

    private InterFragmentCommunication act;

    private AppCompatSpinner categorySpinner;
    private AppCompatSpinner numQuestionsSpinner;
    private AppCompatSpinner difficultySpinner;

    private AppCompatButton startButton, quitButton;

    private final String NUMBER_QUESTIONS_TAG = "NUMBER_OF_QUESTIONS";
    private final String CATEGORY_QUESTIONS_TAG = "CATEGORY_OF_QUESTIONS";
    private final String DIFFICULTY_QUESTIONS_TAG = "DIFFICULTY_OF_QUESTIONS";

    public static final String ARGS_TAG = "ARGS_TAG";

    private int numberSpinnerIndex = 0;
    private int categorySpinnerIndex = 0;
    private int difficultySpinnerIndex = 0;

    private View root;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {


        if(savedInstanceState != null){
            categorySpinnerIndex = savedInstanceState.getInt(CATEGORY_QUESTIONS_TAG);
            difficultySpinnerIndex = savedInstanceState.getInt(DIFFICULTY_QUESTIONS_TAG);
            numberSpinnerIndex = savedInstanceState.getInt(NUMBER_QUESTIONS_TAG);
        }

        //Depending on drawer layout or not, pick menu layout
        if(getArguments() != null) //small screen, navigation drawer present
        {
            root = inflater.inflate(R.layout.menu_fragment_drawer, container, false);
        }
        else
        {
            root = inflater.inflate(R.layout.menu_fragment, container, false);
        }


        startButton = root.findViewById(R.id.start_button);

        quitButton = root.findViewById(R.id.quit_button);

        numQuestionsSpinner = root.findViewById(R.id.number_of_questions_spinner);
        difficultySpinner = root.findViewById(R.id.difficulty_spinner);
        categorySpinner = root.findViewById(R.id.category_spinner);

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        act = (InterFragmentCommunication) getActivity();


        numQuestionsSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_row, getResources().getStringArray(R.array.num_questions_options)));
        difficultySpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_row, getResources().getStringArray(R.array.difficulties)));
        categorySpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_row, getResources().getStringArray(R.array.categories)));

        categorySpinner.setSelection(categorySpinnerIndex);
        numQuestionsSpinner.setSelection(numberSpinnerIndex);
        difficultySpinner.setSelection(difficultySpinnerIndex);


        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String query = formQuery();
                Log.e("MnuFgrmnt/StrtBttn", "Query is " + query);

                //handle possible api error that will be passed to retrofit instance
                OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor()
                {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException
                    {
                        Request request = chain.request();
                        okhttp3.Response response = chain.proceed(request);

                        if(response.code() == 500){

                            startActivity(new Intent(getActivity(), NoResponseActivity.class));

                        }

                        return response;
                    }
                }).build();



                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(ApiInterface.BASE_URL)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .client(httpClient)
                                        .build();

                ApiInterface apiInterface = retrofit.create(ApiInterface.class);

                Call<ApiResponse> call = apiInterface.getResponse(query);
                call.enqueue(new Callback<ApiResponse>()
                {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
                    {

                        ApiResponse apiResponse = response.body();
                        act.setTheQuiz(apiResponse);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t)
                    {
                        startActivity(new Intent(getActivity(), NoResponseActivity.class));
                        getActivity().finish();
                    }
                });

                if(getArguments() != null) //drawer layout present
                {
                    act.closeTheDrawer();
                }
            }
        });


        quitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().finish();
            }
        });

        // Load an ad into the AdMob banner view.
        if(getArguments() == null) //if not null drawer is present, adview is loaded elsewhere
        {
            //Remember to uncomment in the main activity as well
            //MobileAds.initialize(getActivity(), put the app id from admob here);
            AdView adView = root.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);
        }

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt(DIFFICULTY_QUESTIONS_TAG, difficultySpinner.getSelectedItemPosition());
        outState.putInt(NUMBER_QUESTIONS_TAG, numQuestionsSpinner.getSelectedItemPosition());
        outState.putInt(CATEGORY_QUESTIONS_TAG, categorySpinner.getSelectedItemPosition());

    }



    @Override
    public void onDetach()
    {
        super.onDetach();

        act = null;
    }



    private String formQuery(){

        String result = ApiInterface.BASE_URL;

        String amount = numQuestionsSpinner.getSelectedItem().toString();
        result = result + "?amount=" + amount;

        String token = act.getApiToken();

        if(!token.equalsIgnoreCase(""))
            result = result + "&token=" + token;

        String category = categorySpinner.getSelectedItem().toString();
        String categoryIndex = getCategory(category);
        result = result + "&category=" + categoryIndex;

        String difficulty = difficultySpinner.getSelectedItem().toString();
        //make first letter lower
        difficulty = Character.toLowerCase(difficulty.charAt(0)) + difficulty.substring(1);
        result = result + "&difficulty=" + difficulty;

        result = result + "&type=multiple";

        return result;
    }



    /**
     * Will contain the category index for the api query
     * @param tag
     * @return
     */
    private String getCategory(String tag){

        String result = null;

        switch (tag){

            case "Anime/Manga":
                result = "31";
                break;

            case "Computers":
                result = "18";
                break;

            case "Films":
                result = "11";
                break;

            case "General":
                result = "9";
                break;

            case "Geography":
                result = "22";
                break;

            case "History":
                result = "23";
                break;

            case "Music":
                result = "12";
                break;

            case "Musicals/Theater":
                result = "13";
                break;

            case "Science/Nature":
                result = "17";
                break;

            case "Video Games":
                result = "15";
                break;

            default: //case Any Category
                result = "";
                break;
        }

        return result;
    }


}
