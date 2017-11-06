package ua.jaraxxus.drinkmeter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginSingInActivity extends AppCompatActivity {


    Button bSingIn, bLogin;

    SharedPreferences sPref;
    final String IsRemember = "RememberMe";
    final String UserLogin = "UserLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sing_in);
        bLogin = (Button)findViewById(R.id.bLogin);
        bSingIn = (Button)findViewById(R.id.bSingIn);


        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent LoginIntent = new Intent(LoginSingInActivity.this,LoginActivity.class);
                startActivity(LoginIntent);

            }
        });

        bSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent SingInIntent = new Intent(LoginSingInActivity.this,SingInActivity.class);
                startActivity(SingInIntent);

            }
        });

        sPref = getSharedPreferences("sharedSettings",MODE_PRIVATE);
        boolean remembered = sPref.getBoolean(IsRemember,false);
        String userlogin = sPref.getString(UserLogin,"");

        if (remembered)

        {
            Intent DrinkList = new Intent(LoginSingInActivity.this,AlcoholListActivity.class);
            DrinkList.putExtra("login",userlogin);
            startActivity(DrinkList);
        }

    }
}
