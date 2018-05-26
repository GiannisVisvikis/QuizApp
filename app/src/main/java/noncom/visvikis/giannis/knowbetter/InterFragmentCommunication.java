package noncom.visvikis.giannis.knowbetter;

public interface InterFragmentCommunication
{


    void closeTheDrawer();
    void openTheDrawer();

    String getApiToken();

    MainFragment getMainFragment();
    RetainedFragment getRetainedFragment();

    void resetTheToken(String query);
    void setTheQuiz(ApiResponse response, String query);

    void incrementCorrectAnswers();
    void setupQuestion();

    void showSnackBar(int totalCorrect);

}
