package noncom.visvikis.giannis.knowbetter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuizQuestion
{

    private String category;
    private String difficulty;
    private String type;
    private String question;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("incorrect_answers")
    private List<String> falseAnswers;

    public String getCategory()
    {
        return category;
    }

    public String getDifficulty()
    {
        return difficulty;
    }

    public String getType()
    {
        return type;
    }

    public String getQuestion()
    {
        return question;
    }

    public String getCorrectAnswer()
    {
        return correctAnswer;
    }

    public List<String> getFalseAnswers()
    {
        return falseAnswers;
    }


    public QuizQuestion(String category, String difficulty, String type, String question, String correctAnswer, List<String> falseAnswers)
    {
        this.category = category;
        this.difficulty = difficulty;
        this.type = type;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.falseAnswers = falseAnswers;
    }


}
