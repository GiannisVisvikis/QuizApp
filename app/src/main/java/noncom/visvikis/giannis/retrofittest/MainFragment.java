package noncom.visvikis.giannis.retrofittest;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment
{


    private InterFragmentCommunication act;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        //TODO decide which layout to use depending on true/false or multiple choice provided in the arguments
        View root = inflater.inflate(R.layout.main_fragment_multiple, container, false);

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        act = (InterFragmentCommunication) getActivity();

    }


    @Override
    public void onDetach()
    {
        super.onDetach();

        act = null;
    }
}
