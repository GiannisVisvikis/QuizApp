
package noncom.visvikis.giannis.retrofittest;


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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MenuFragment extends Fragment
{

    private InterFragmentCommunication act;

    private AppCompatSpinner categorySpinner;
    private AppCompatSpinner typeSpinner;
    private AppCompatSpinner numQuestionsSpinner;
    private AppCompatSpinner difficultySpinner;

    private AppCompatButton startButton, quitButton;

    private final String NUMBER_QUESTIONS_TAG = "NUMBER_OF_QUESTIONS";
    private final String CATEGORY_QUESTIONS_TAG = "CATEGORY_OF_QUESTIONS";
    private final String TYPE_QUESTIONS_TAG = "TYPE_OF_QUESTIONS";
    private final String DIFFICULTY_QUESTIONS_TAG = "DIFFICULTY_OF_QUESTIONS";
    private String photoPath = "";

    public static final String ARGS_TAG = "ARGS_TAG";

    private int typeSpinnerIndex = 0;
    private int numberSpinnerIndex = 0;
    private int categorySpinnerIndex = 0;
    private int difficultySpinnerIndex = 0;

    private View root;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {


        if(savedInstanceState != null){
            typeSpinnerIndex = savedInstanceState.getInt(TYPE_QUESTIONS_TAG);
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
        typeSpinner = root.findViewById(R.id.type_spinner);
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
        typeSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_row, getResources().getStringArray(R.array.type_options)));
        difficultySpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_row, getResources().getStringArray(R.array.difficulties)));
        categorySpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_row, getResources().getStringArray(R.array.categories)));

        categorySpinner.setSelection(categorySpinnerIndex);
        typeSpinner.setSelection(typeSpinnerIndex);
        numQuestionsSpinner.setSelection(numberSpinnerIndex);
        difficultySpinner.setSelection(difficultySpinnerIndex);


        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String query = formQuery();
                Log.e("MnuFgrmnt/StrtBttn", "Query is " + query);

                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(ApiInterface.BASE_URL)
                                        .addConverterFactory(GsonConverterFactory.create())
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
                        Log.e("MenuFrgmnt/onClick", "Fuked up");
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
                ((MainActivity) act).finish();
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

        outState.putInt(TYPE_QUESTIONS_TAG, typeSpinner.getSelectedItemPosition());
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
        String[] categoryArray = getCategory(category);
        photoPath = categoryArray[1];
        result = result + "&category=" + categoryArray[0];

        String difficulty = difficultySpinner.getSelectedItem().toString();
        //make first letter lower
        difficulty = Character.toLowerCase(difficulty.charAt(0)) + difficulty.substring(1);
        result = result + "&difficulty=" + difficulty;

        String type = typeSpinner.getSelectedItem().toString();
        if(type.contains("True"))
            type = "boolean";
        else
            type = "multiple";
        result = result + "&type=" + type;

        return result;
    }


    /**
     * Will contain the category index fpr the api query and file names of the photos
     * @param tag
     * @return
     */
    private String[] getCategory(String tag){

        String[] result = new String[2];

        switch (tag){

            case "Animals":
                result[0] = "27";
                result[1] = "animals.jpg";
                break;

            case "Anime/Manga":
                result[0] = "31";
                result[1] = "manga.jpg";
                break;

            case "Arts":
                result[0] = "25";
                result[1] = "art.jpg";
                break;

            case "Board Games":
                result[0] = "16";
                result[1] = "games.jpg";
                break;

            case "Books":
                result[0] = "10";
                result[1] = "books.jpg";
                break;

            case "Cartoons":
                result[0] = "32";
                result[1] = "animations.jpg";
                break;

            case "Celebrities":
                result[0] = "26";
                result[1] = "celebrities.jpg";
                break;

            case "Comics":
                result[0] = "29";
                result[1] = "comics.jpg";
                break;

            case "Computers":
                result[0] = "18";
                result[1] = "computers.jpg";
                break;

            case "Films":
                result[0] = "11";
                result[1] = "film.jpg";
                break;

            case "Gadgets":
                result[0] = "30";
                result[1] = "gadgets.jpg";
                break;

            case "General":
                result[0] = "9";
                result[1] = "knowledge.jpg";
                break;

            case "Geography":
                result[0] = "22";
                result[1] = "geography.jpg";
                break;

            case "History":
                result[0] = "23";
                result[1] = "history.jpg";
                break;

            case "Mathematics":
                result[0] = "19";
                result[1] = "mathematics.jpg";
                break;

            case "Music":
                result[0] = "12";
                result[1] = "music.jpg";
                break;

            case "Musicals/Theater":
                result[0] = "13";
                result[1] = "theatres.jpg";
                break;

            case "Mythology":
                result[0] = "20";
                result[1] = "mythology.jpg";
                break;

            case "Politics":
                result[0] = "24";
                result[1] = "politics";
                break;

            case "Science/Nature":
                result[0] = "17";
                result[1] = "nature.jpg";
                break;

            case "Sports":
                result[0] = "21";
                result[1] = "sports.jpg";
                break;

            case "Television":
                result[0] = "14";
                result[1] = "television.jpg";
                break;

            case "Vehicles":
                result[0] = "28";
                result[1] = "vehicles.jpg";
                break;

            case "Video Games":
                result[0] = "15";
                result[1] = "games.jpg";
                break;

            default: //case Any Category
                result[0] = "";
                result[1] = "knowledge";
                break;
        }

        return result;
    }


}
