package com.diklat.pln.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        radioGroup = (RadioGroup)findViewById(R.id.login_connection_rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });
        setView();

    }

    private void setView(){
        final Button login = (Button)findViewById(R.id.login);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(username.getText().toString().trim().length()==0){
                    username.setError("Username tidak boleh kosong");
                }
                else{
                    username.setError(null);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(password.getText().toString().length()==0){
                    password.setError("Password tidak boleh kosong");
                }
                else{
                    password.setError(null);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Logging In . . .",Toast.LENGTH_SHORT).show();
                if(kurangChecking()){
                    int radioCheked = radioGroup.getCheckedRadioButtonId();
                    if(radioCheked==R.id.rb_1)
                        Konstanta.URL=Konstanta.IPLOCAL;
                    else if(radioCheked==R.id.rb_2)
                        Konstanta.URL=Konstanta.IPPUBLIC;
                    final Session session = new Session(getBaseContext());
                    session.loginProcess(username.getText().toString(), password.getText().toString(), new Callback() {
                        @Override
                        public void onSuccessResponse(String result) {
                            ParseJson pj = new ParseJson(result);
                            String msg = pj.parseLogin() != null ? pj.parseLogin() : "";
                            if(msg.equals("invalid") || msg.equals("")){
                                Toast.makeText(getBaseContext(),"username atau password salah",Toast.LENGTH_SHORT).show();

                            }
                            else  {
                                String token = pj.parseToken();
                                openIntent(TabbedActivity.class);
                                session.removePreferences();
                                Log.d("Token",token);
                                session.editPreferences(token,username.getText().toString(),password.getText().toString());

                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            ChangeIpUtils changeIpUtils = new ChangeIpUtils(getBaseContext());
//                            changeIpUtils.loginChange();
                            Toast.makeText(getBaseContext(),"Terjadi kesalahan pada login, coba cek koneksi anda",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(getBaseContext(),"Username atau password kurang",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private boolean kurangChecking(){
        if(username.getText().toString().trim().length()==0 || password.getText().toString().length()==0){
            return false;
        }
        else return true;
    }
    private void openIntent(Class page){
        Intent openPage = new Intent(getBaseContext(),page);
        openPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(openPage);
    }
}
