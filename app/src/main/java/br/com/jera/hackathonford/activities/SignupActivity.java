package br.com.jera.hackathonford.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.jera.hackathonford.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

import br.com.jera.hackathonford.activities.BaseActivity;

/**
 * Created by Diego on 05/02/2015.
 */
public class SignupActivity extends BaseActivity {

    @InjectView(R.id.nameEditText)
    EditText nameEditText;
    @InjectView(R.id.startButton)
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_activity);
        ButterKnife.inject(this);
    }


}
