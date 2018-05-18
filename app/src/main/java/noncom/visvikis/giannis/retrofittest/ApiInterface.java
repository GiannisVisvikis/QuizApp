
package noncom.visvikis.giannis.retrofittest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface
{

    public static final String API_TOKEN_REQUEST_URL = "https://www.opentdb.com/api_token.php?command=request";

    public static final String BASE_URL = "https://opentdb.com/api.php/";

    @GET
    public Call<ApiResponse> getResponse(@Url String restOfUrl);

}
