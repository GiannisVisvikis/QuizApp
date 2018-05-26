package noncom.visvikis.giannis.knowbetter;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class ResetDialog extends DialogFragment
{


    private InterFragmentCommunication act;
    private String query;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        act = (InterFragmentCommunication) getActivity();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setCancelable(false);
        if(savedInstanceState == null)
            query = getArguments().getString("QUERY");
        else
            query = savedInstanceState.getString("QUERY");
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.reset_dialog, container, false);

        AppCompatButton repeatButton = root.findViewById(R.id.repeat_button);
        repeatButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                act.resetTheToken(query); //reset the token through a loader and on loader finish make the retrofit call again
                ResetDialog.this.dismiss();
            }
        });


        AppCompatButton resetNewQuizButton = root.findViewById(R.id.reset_new_button);
        resetNewQuizButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ResetDialog.this.dismiss();
                act.openTheDrawer(); //if not a drawer present, will be handled there
            }
        });


        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return root;
    }



    @Override
    public void onStart() {
        super.onStart();

        int width, height;

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            width = (int) (getResources().getDisplayMetrics().widthPixels * 0.70);
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
        }
        else{
            width = (int) (getResources().getDisplayMetrics().widthPixels * 0.50);
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.70);
        }

        getDialog().getWindow().setLayout(width, height);

    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString("QUERY", query);
    }


}
