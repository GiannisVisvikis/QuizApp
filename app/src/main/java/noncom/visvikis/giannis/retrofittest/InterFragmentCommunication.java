package noncom.visvikis.giannis.retrofittest;

public interface InterFragmentCommunication
{


    void closeTheDrawer();
    void openTheDrawer();

    String getApiToken();

    MenuFragment getMenuFragment();
    MainFragment getMainFragment();
    RetainedFragment getRetainedFragment();

    void resetTheToken(String query);
    void setTheQuiz(ApiResponse response, String query);
    void launchNewQuiz();

    void incrementCorrectAnswers();
    void setupQuestion();

    void showSnackBar(int totalCorrect);

}
