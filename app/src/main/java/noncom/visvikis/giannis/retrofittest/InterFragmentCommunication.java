package noncom.visvikis.giannis.retrofittest;

import android.support.v7.widget.RecyclerView;

public interface InterFragmentCommunication
{

    RetainedFragment getRetainedFragment();
    RecyclerView.Adapter getTheAdapter();

    void closeTheDrawer();
}
