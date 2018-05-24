package noncom.visvikis.giannis.retrofittest;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Random;


public class MainFragment extends Fragment
{

    //will hold the index for orientation changes;
    private final String CURRENT_INDEX_TAG = "CURRENT_INDEX_TAG";
    private final String PLAYING_QUIZ_TAG = "PLAYING_QUIZ_TAG";
    private final String TOTAL_CORRECT_COUNTER = "TOTAL-CORRECT_COUNTER";
    private int currentIndex;
    private int totalCorrect;

    private View root;

    //keep track if orientation change happened in the middle of a game
    private boolean playingQuiz;

    private InterFragmentCommunication act;

    private ImageView questionImage;
    private AppCompatTextView questionTxt;
    private AppCompatTextView answer1, answer2, answer3, answer4;

    private CardView answerCard1, answerCard2, answerCard3, answerCard4;

    private AppCompatTextView[] answerTxtViews;
    private CardView[] answerCards;


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

        root = inflater.inflate(R.layout.main_fragment_multiple, container, false);

        questionImage = root.findViewById(R.id.question_photo_place);

        questionTxt = root.findViewById(R.id.question_place);

        answer1 = root.findViewById(R.id.answer1_txt);
        answer2 = root.findViewById(R.id.answer2_txt);
        answer3 = root.findViewById(R.id.answer3_txt);
        answer4 = root.findViewById(R.id.answer4_txt);

        answerTxtViews = new AppCompatTextView[]{answer1, answer2, answer3, answer4};

        answerCard1 = root.findViewById(R.id.answer1_card);
        answerCard2 = root.findViewById(R.id.answer2_card);
        answerCard3 = root.findViewById(R.id.answer3_card);
        answerCard4 = root.findViewById(R.id.answer4_card);

        answerCards = new CardView[]{answerCard1, answerCard2, answerCard3, answerCard4};


        //TODO define a function that will return the assets pic by auestion category

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

            Random random = new Random();

            //select an index at random to put the correct answer in place
            int correctIndex = random.nextInt(answerTxtViews.length);
            //will point to the incorrect answer inside question.getFalseAnswers
            int falseIndex = 0;
            //will point to index at cardViews array. Whenever this gets to correct index, a listener will be added for correct choice
            int generalIndex = 0;


            for(AppCompatTextView answerTxtView : answerTxtViews)
            {

                //make color black again from previous answer
                answerTxtView.setTextColor(Color.BLACK);

                if(answerTxtView == answerTxtViews[correctIndex])
                {   //if this is the position selected at random for the correct answer set the correct answer
                    final AppCompatTextView correctTextView = answerTxtViews[generalIndex];

                    String rightAnswer = question.getCorrectAnswer();
                    makeReadable(rightAnswer, correctTextView);

                    //set the listener for correct choice to the cardview that holds the text view displaying the correct answer
                    CardView correctCard = answerCards[generalIndex];
                    correctCard.setEnabled(true); //might be disabled from previous game
                    correctCard.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            act.getRetainedFragment().playSound(R.raw.success);

                            correctTextView.setTextColor(Color.GREEN);

                            try
                            {
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException ie){}

                            totalCorrect++;
                            currentIndex++;
                            setupQuestion(currentIndex);
                        }
                    });


                }
                else
                {   //set a false answer

                    String falseAnswer = question.getFalseAnswers().get(falseIndex);
                    makeReadable(falseAnswer, answerTxtView);

                    final AppCompatTextView falseTextView = answerTxtViews[generalIndex];

                    //set the listener for false answer
                    CardView falseCard = answerCards[generalIndex];
                    falseCard.setEnabled(true);
                    falseCard.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            act.getRetainedFragment().playSound(R.raw.wrong_answer);
                            falseTextView.setTextColor(Color.RED);

                            try
                            {
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException ie){}

                            currentIndex++;
                            setupQuestion(currentIndex);
                        }
                    });

                    falseIndex++;
                }

                generalIndex++;
            }

            String questionFullText = question.getQuestion();

            makeReadable(questionFullText, questionTxt);

            String categoryAssetPic = getCategoryAssetPic(question.getCategory(), "");
            setQuestionImage(categoryAssetPic);

        }
        else
        {
            playingQuiz = false;

            //Disable cards

            for(int index=0; index<answerCards.length; index++)
            {
                answerCards[index].setEnabled(false);
            }

            act.showSnackBar(totalCorrect);

            see to null pointer at MenuFragment line 320 getContext when wifi lenovo shut off from inactivity
            //TODO fix colors
        }

    }



    public View getCoordinatorView(){
        return root.findViewById(R.id.coordinator);
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
    private void makeReadable(String toConvert, AppCompatTextView toAddTo){

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
