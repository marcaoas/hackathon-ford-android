package br.com.jera.hackathonford.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.jera.hackathonford.R;
import br.com.jera.hackathonford.model.User;
import butterknife.ButterKnife;
import butterknife.InjectView;

import butterknife.OnClick;

/**
 * Created by Diego on 05/02/2015.
 */
public class SignupActivity extends BaseActivity {

    final String PREFS_NAME = "MyPrefsFile";

    @InjectView(R.id.nameEditText)
    EditText nameEditText;
    @InjectView(R.id.startButton)
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_activity);
        ButterKnife.inject(this);
        Log.d("HACKATON", User.getCount() + "");

        if(User.getCount() > 0){
            Intent mainActivityIntent = new Intent(this,MainActivity.class);
            startActivity(mainActivityIntent);
        }
    }


    @OnClick(R.id.startButton)
    public void save(){

        String name =  nameEditText.getText().toString();
        User newUser = new User(name);
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        newUser.save();
    }


}
