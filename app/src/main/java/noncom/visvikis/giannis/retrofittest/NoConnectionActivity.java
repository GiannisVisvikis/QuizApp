package noncom.visvikis.giannis.retrofittest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

public class NoConnectionActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_wifi_activity);

        AppCompatImageView restart = findViewById(R.id.no_wifi_image_place);
        restart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent restartIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(restartIntent);
                NoConnectionActivity.this.finish();
            }
        });

    }
}
