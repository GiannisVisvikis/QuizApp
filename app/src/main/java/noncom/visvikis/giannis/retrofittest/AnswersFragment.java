package noncom.visvikis.giannis.retrofittest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

public class AnswersFragment extends Fragment
{

    public static final String ANSWERS_TYPE = "ANSWERS_TYPE";
    public static final String QUESTION_INDEX = "QUESTION_INDEX";

    private View root;

    private int questionIndex;
    private boolean isBinary;

    private InterFragmentCommunication act;


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        act = (InterFragmentCommunication) getActivity();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        isBinary = getArguments().getBoolean(ANSWERS_TYPE);

        if(savedInstanceState ==  null)
            questionIndex = getArguments().getInt(QUESTION_INDEX);
        else
            questionIndex = savedInstanceState.getInt(QUESTION_INDEX);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        if(isBinary)
            root = inflater.inflate(R.layout.boolean_answers_layout, container, false);
        else
            root = inflater.inflate(R.layout.multiple_answers_layout, container, false);

        setupAnswers();

        return root;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt(QUESTION_INDEX, questionIndex);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        act = null;
    }




    private void setupAnswers(){

        CardView[] answerCards;
        AppCompatTextView[] answerTxtViews;

        if(isBinary)
        {
            AppCompatTextView answer1 = root.findViewById(R.id.answer1_txt);
            AppCompatTextView answer2 = root.findViewById(R.id.answer2_txt);

            answerTxtViews = new AppCompatTextView[]{answer1, answer2};

            CardView answerCard1 = root.findViewById(R.id.answer1_card);
            CardView answerCard2 = root.findViewById(R.id.answer2_card);

            answerCards = new CardView[]{answerCard1, answerCard2};

        }
        else
        {
            AppCompatTextView answer1 = root.findViewById(R.id.answer1_txt);
            AppCompatTextView answer2 = root.findViewById(R.id.answer2_txt);
            AppCompatTextView answer3 = root.findViewById(R.id.answer3_txt);
            AppCompatTextView answer4 = root.findViewById(R.id.answer4_txt);

            answerTxtViews = new AppCompatTextView[]{answer1, answer2, answer3, answer4};

            CardView answerCard1 = root.findViewById(R.id.answer1_card);
            CardView answerCard2 = root.findViewById(R.id.answer2_card);
            CardView answerCard3 = root.findViewById(R.id.answer3_card);
            CardView answerCard4 = root.findViewById(R.id.answer4_card);

            answerCards = new CardView[]{answerCard1, answerCard2, answerCard3, answerCard4};
        }


        //Assign random positions to true and false answers
        assignAnswerPlaces(answerTxtViews, answerCards);


    }




    private void assignAnswerPlaces(AppCompatTextView[] answerTxtViews, final CardView[] answerCards)
    {

        if(questionIndex < act.getRetainedFragment().getQuizQuestions().size())
        {
            Random random = new Random();

            final QuizQuestion question = act.getRetainedFragment().getQuizQuestions().get(questionIndex);

            //select an index at random to put the correct answer in place
            int correctIndex = random.nextInt(answerTxtViews.length);
            //will point to the incorrect answer inside question.getFalseAnswers
            int falseIndex = 0;
            //will point to index at cardViews array. Whenever this gets to correct index, a listener will be added for correct choice
            int generalIndex = 0;


            for (AppCompatTextView answerTxtView : answerTxtViews) {

                //make color black again from previous answer
                answerTxtView.setTextColor(Color.BLACK);

                if (answerTxtView == answerTxtViews[correctIndex]) {   //if this is the position selected at random for the correct answer set the correct answer
                    final AppCompatTextView correctTextView = answerTxtViews[generalIndex];

                    String rightAnswer = question.getCorrectAnswer();
                    act.getMainFragment().makeReadable(rightAnswer, correctTextView);

                    //set the listener for correct choice to the cardview that holds the text view displaying the correct answer
                    final CardView correctCard = answerCards[generalIndex];
                    correctCard.setEnabled(true); //might be disabled from previous game/question
                    correctCard.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            correctTextView.setTextColor(Color.GREEN);
                            act.getRetainedFragment().playSound(R.raw.success);
                            disableCards(answerCards);

                            act.incrementCorrectAnswers();


                            correctTextView.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (questionIndex < act.getRetainedFragment().getQuizQuestions().size() - 1) //there are still questions left to answer
                                    {
                                        act.setupQuestion();
                                    } else {
                                        questionIndex++;
                                        disableCards(answerCards);
                                        act.setupQuestion();
                                    }
                                }
                            }, 1000);
                        }
                    });


                } else {   //set a false answer

                    String falseAnswer = question.getFalseAnswers().get(falseIndex);
                    act.getMainFragment().makeReadable(falseAnswer, answerTxtView);

                    final AppCompatTextView falseTextView = answerTxtViews[generalIndex];

                    //set the listener for false answer
                    final CardView falseCard = answerCards[generalIndex];

                    falseCard.setEnabled(true); //might be disabled from previous game/question
                    falseCard.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                            falseTextView.setTextColor(Color.RED);
                            act.getRetainedFragment().playSound(R.raw.wrong_answer);

                            //disable cards. If still enabled the user can take a second guess until the UI refreshes
                            disableCards(answerCards);

                            falseTextView.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (questionIndex < act.getRetainedFragment().getQuizQuestions().size() - 1) //there are still questions left to answer
                                    {
                                        act.setupQuestion();
                                    } else {
                                        questionIndex++;
                                        disableCards(answerCards);
                                        act.setupQuestion();
                                    }
                                }
                            }, 1500);
                        }
                    });

                    falseIndex++;
                }

                generalIndex++;
            }
        }

    }



    private void disableCards(CardView[] answerCards)
    {
        for(int index=0; index<answerCards.length; index++)
        {
            answerCards[index].setEnabled(false);
        }
    }


}
