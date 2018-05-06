package noncom.visvikis.giannis.retrofittest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;


/**
 *  Will hold on to expensive adapter during orientation changes
 */

public class RetainedFragment extends Fragment
{

    private RecyclerView.Adapter theQuizadapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }


    public void setTheQuizadapter(RecyclerView.Adapter theAdapter){
        this.theQuizadapter = theAdapter;
    }


    public RecyclerView.Adapter getTheAdapter(){
        return this.theQuizadapter;
    }

}
