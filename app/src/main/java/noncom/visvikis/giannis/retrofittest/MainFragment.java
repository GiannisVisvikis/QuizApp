package noncom.visvikis.giannis.retrofittest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment
{

    private RecyclerView theRecyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.main_fragment, container, false);

        theRecyclerView = root.findViewById(R.id.the_recycler_view);

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        InterFragmentCommunication act = (InterFragmentCommunication) getActivity();

        theRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(act.getTheAdapter() != null){
            theRecyclerView.setAdapter(act.getTheAdapter());
        }

    }


}
