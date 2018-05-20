package noncom.visvikis.giannis.retrofittest;

public interface InterFragmentCommunication
{


    void closeTheDrawer();
    void openTheDrawer();

    String getApiToken();

    MenuFragment getMenuFragment();
    MainFragment getMainFragment();

    void resetTheToken(String query);
    void setTheQuiz(ApiResponse response, String query);
}
