package noncom.visvikis.giannis.retrofittest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse
{

    @SerializedName("response_code")
    private String responseCode;


    private List<QuizQuestion> results;


    public String getResponseCode()
    {
        return responseCode;
    }

    public List<QuizQuestion> getQuestions()
    {
        return results;
    }


    public ApiResponse(String responseCode, List<QuizQuestion> results)
    {
        this.responseCode = responseCode;
        this.results = results;
    }


}
