
package noncom.visvikis.giannis.retrofittest;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;




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
                //TODO Handle click, start a request via retrofit, get the questions, produce the adapter and set it to the main fragment's recycler view through intecommunication interface



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

}
