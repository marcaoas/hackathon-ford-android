package br.com.jera.hackathonford.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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


    @InjectView(R.id.nameEditText)
    EditText nameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_activity);
        ButterKnife.inject(this);
//        Log.d("HACKATON", User.getCount() + "");



        if(User.getCount() > 0){
            Intent mainActivityIntent = new Intent(this,MainActivity.class);
            finish();
            startActivity(mainActivityIntent);
        }
        nameEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);
    }


    @OnClick(R.id.startButton)
    public void save(){

        String name =  nameEditText.getText().toString();
        User newUser = new User(name);
        Intent mainActivityIntent = new Intent(this,MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
        newUser.save();

    }


}
