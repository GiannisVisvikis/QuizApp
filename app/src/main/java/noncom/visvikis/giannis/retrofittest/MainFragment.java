package noncom.visvikis.giannis.retrofittest;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatTextView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;


public class MainFragment extends Fragment
{

    //will hold the index for orientation changes;
    private final String CURRENT_INDEX_TAG = "CURRENT_INDEX_TAG";
    private final String PLAYING_QUIZ_TAG = "PLAYING_QUIZ_TAG";
    private final String TOTAL_CORRECT_COUNTER = "TOTAL-CORRECT_COUNTER";

    private int currentIndex;
    private int totalCorrect;

    //keep track if orientation change happened in the middle of a game
    private boolean playingQuiz;

    private InterFragmentCommunication act;

    private View root;

    private ImageView questionImage;
    private AppCompatTextView questionTxt;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            currentIndex = 0;
            playingQuiz = false;
            totalCorrect = 0;
        }
        else
        {
            currentIndex = savedInstanceState.getInt(CURRENT_INDEX_TAG);
            playingQuiz = savedInstanceState.getBoolean(PLAYING_QUIZ_TAG);
            totalCorrect = savedInstanceState.getInt(TOTAL_CORRECT_COUNTER);
        }


    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        root = inflater.inflate(R.layout.main_fragment, container, false);

        questionImage = root.findViewById(R.id.question_photo_place);

        questionTxt = root.findViewById(R.id.question_place);

        return root;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        act = (InterFragmentCommunication) getActivity();


        if(playingQuiz)
            setupQuestion(currentIndex);

    }




    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(PLAYING_QUIZ_TAG, playingQuiz);
        outState.putInt(CURRENT_INDEX_TAG, currentIndex);
        outState.putInt(TOTAL_CORRECT_COUNTER, totalCorrect);
    }



    @Override
    public void onDetach()
    {
        super.onDetach();

        act = null;
    }



    /**
     * sets up the question at questionIndex
     * @param questionIndex the index of the quiz question
     *
     */
    public void setupQuestion(int questionIndex)
    {
        if(questionIndex < act.getRetainedFragment().getQuizQuestions().size())
        {
            playingQuiz = true;

            if(questionIndex == 0) //reset the current index if first question
            {
                currentIndex = questionIndex;
                totalCorrect = 0;
            }

            QuizQuestion question = act.getRetainedFragment().getQuizQuestions().get(questionIndex);

            String type = question.getType();


            //Answers could be either multiple or boolean. Add the appropriate answers fragment
            Bundle answerArgs = new Bundle();

            boolean isBinary = type.equalsIgnoreCase("boolean");

            answerArgs.putInt(AnswersFragment.QUESTION_INDEX, questionIndex);
            answerArgs.putBoolean(AnswersFragment.ANSWERS_TYPE, isBinary);

            AnswersFragment answersFragment = new AnswersFragment();
            answersFragment.setArguments(answerArgs);
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.answers_place, answersFragment, "ANSWERS_FRAGMENT").commit();

            fragmentManager.executePendingTransactions();


            String questionFullText = question.getQuestion();

            makeReadable(questionFullText, questionTxt);

            String categoryAssetPic = getCategoryAssetPic(question.getCategory(), "");
            setQuestionImage(categoryAssetPic);

        }
        else
        {
            playingQuiz = false;

            act.showSnackBar(totalCorrect);

            fix colors on question cards
        }

    }



    public void incrementTotalCorrect(){
        totalCorrect ++;
    }


    public void incrementCurrentIndex(){
        currentIndex++;
    }


    public View getCoordinatorView(){
        return root.findViewById(R.id.coordinator);
    }


    public int getCurrentIndex()
    {
        return currentIndex;
    }


    private void setQuestionImage(String pathToAssetsPic)
    {
        try
        {
            Bitmap image = BitmapFactory.decodeStream(getResources().getAssets().open("photos/" + pathToAssetsPic + ".jpg"));
            questionImage.setImageBitmap(image);
        }
        catch (IOException io)
        {
            questionImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.queston_photo_default));
        }
    }



    /**
     * Convert api category string to asset filename for pic corresponding to this category
     * @param category
     * @param result
     * @return
     */
    private String getCategoryAssetPic(String category, String result)
    {
        if(category.length() == 0)
            return Character.toLowerCase(result.charAt(0)) + result.substring(1); //make first letter lower case
        else{
            if (category.charAt(0) == ' ')
                result = "";
            else
                result += category.charAt(0);

            return getCategoryAssetPic(category.substring(1), result);
        }

    }



    /**
     * Removes html entities like &quot;
     * @param toConvert
     * @return
     */
    public void makeReadable(String toConvert, AppCompatTextView toAddTo){

        if (Build.VERSION.SDK_INT >= 24)
        {
            toAddTo.setText(Html.fromHtml(toConvert , Html.FROM_HTML_MODE_LEGACY));
        }
        else
        {
            toAddTo.setText(Html.fromHtml(toConvert));
        }

    }


}
