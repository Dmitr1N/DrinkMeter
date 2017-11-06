package ua.jaraxxus.drinkmeter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class AlcoholListActivity extends AppCompatActivity {

    ScrollView svDrinkList;
    Button bUserState;
    LinearLayout llDrinkList;
    ArrayList<DrinkListElement>   drinkListElements;
    User user;
    AlertDialog.Builder builder;
    SharedPreferences sPref;
    final String IsRemember = "RememberMe";
    final String UserLogin = "UserLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llDrinkList = (LinearLayout)findViewById(R.id.llDrinkList);
        svDrinkList = (ScrollView)findViewById(R.id.svDrinkList);
        bUserState = (Button)findViewById(R.id.bUserState);

        sPref = getSharedPreferences("sharedSettings",MODE_PRIVATE);

        drinkListElements = new ArrayList<DrinkListElement>();
        Intent LoginIntent  = getIntent();
        String Login = LoginIntent.getStringExtra("login");

        File IsUser = new File(getFilesDir(), Login);

        if (!IsUser.exists())
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.sDataError),Toast.LENGTH_SHORT).show();
            return;

        }

        FileInputStream inputStream;
        ObjectInputStream ois;

        try {
            inputStream = openFileInput(Login);
            ois = new ObjectInputStream(inputStream);
            user=(User)ois.readObject();
            //inputStream.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.sDataError),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.sDataError),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.sDataError),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        String[] DrName = getResources().getStringArray(R.array.DrinksName);
        String[] OtherName = getResources().getStringArray(R.array.DrinksOtherIngredients);

        try {
            AssetManager assetManager = this.getAssets();
            InputStreamReader istream = new InputStreamReader(assetManager.open("DrinkList"));
            BufferedReader br = new BufferedReader(istream);

            int Index=-1;
            String temp;
            while((temp=br.readLine())!=null)
            {
                Index++;
                String Name = temp;
                DrinkListElement drinkListElement;
                int[] ing = new int[20];

                for(int i = 0; i<20 ;i++)
                {
                    ing[i]= Integer.parseInt(br.readLine());
                }
                String Other = br.readLine();
                drinkListElement = new DrinkListElement(getApplicationContext(),llDrinkList.getContext(),new Drink(Index,Name,ing,Other),Index,user);
                drinkListElements.add(drinkListElement);
                llDrinkList.addView(drinkListElement);
            }
            br.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            // IOExeption
        }
        builder=new AlertDialog.Builder(this);

        bUserState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float SumAlcohol=0;
                for (int i = 0; i < drinkListElements.size();i++)
                    SumAlcohol+=drinkListElements.get(i)._drink.getAlcohol(getApplicationContext())*user.Drinks[i];

                float sex;
                if (user.Sex) sex = (float)0.7;
                else sex = (float)0.6;

                float promile = SumAlcohol/(user.Weight*sex);

                String[] userCondition = getResources().getStringArray(R.array.UserCondition);

                int UCon = 0;

                if (promile<0.3) UCon = 0;
                else if (promile>=0.3 && promile < 0.5) UCon =1;
                else if (promile>=0.5 && promile < 1.5) UCon =2;
                else if (promile>=1.5 && promile < 2.5) UCon =3;
                else if (promile>=2.5 && promile < 3.0) UCon =4;
                else UCon = 5;

                builder.setTitle(R.string.sUserState)
                .setCancelable(false)
                .setMessage(userCondition[UCon])
                        .setNeutralButton(R.string.sShare, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                .setNegativeButton(R.string.sOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                //Toast.makeText(getApplicationContext(),""+SumAlcohol,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.iLogOut: {
                AlertDialog.Builder LogOutDialog = new AlertDialog.Builder(this);
                LogOutDialog.setTitle(R.string.sLogOut)
                        .setCancelable(false)
                        .setMessage(R.string.sAreYouSure)
                        .setPositiveButton(R.string.sYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor ed = sPref.edit();
                                ed.putBoolean(IsRemember,false);
                                ed.putString(UserLogin,"");
                                ed.commit();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.sNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = LogOutDialog.create();
                dialog.show();

            }break;
            case R.id.iDeleteUser: {
                AlertDialog.Builder DeleteDialog = new AlertDialog.Builder(this);
                final AlertDialog.Builder ConfirmDialog = new AlertDialog.Builder(this);
                final Context dcontext = ConfirmDialog.getContext();
                DeleteDialog.setTitle(R.string.sDeleteUser)
                        .setCancelable(false)
                        .setMessage(R.string.sAreYouSure)
                        .setPositiveButton(R.string.sYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                {
                                    final EditText ePassWord = new EditText(dcontext);
                                    ePassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    ePassWord.setHint(R.string.sPassword);
                                    ConfirmDialog.setTitle(R.string.sDeleteUser)
                                            .setView(ePassWord)
                                            .setCancelable(false)
                                            .setMessage(R.string.sConfirmPasword)
                                            .setPositiveButton(R.string.sConfirm, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    if (ePassWord.getText().toString().compareTo(user.Password)==0) {
                                                        SharedPreferences.Editor ed = sPref.edit();
                                                        ed.putBoolean(IsRemember, false);
                                                        ed.putString(UserLogin, "");
                                                        ed.commit();
                                                        deleteFile(user.Login);
                                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.sUserDeleted),Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        //Toast.makeText(getApplicationContext(),"File deleted", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), R.string.sWrongPasword, Toast.LENGTH_SHORT).show();
                                                        //dialogInterface.dismiss();
                                                    }
                                                }
                                            })
                                            .setNegativeButton(R.string.sCancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                    AlertDialog cdialog = ConfirmDialog.create();
                                    cdialog.show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.sNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = DeleteDialog.create();
                dialog.show();

            }break;
            case R.id.iUserInfo: {
                AlertDialog.Builder InfoDialog = new AlertDialog.Builder(this);
                String sInfo;
                if (user.Sex) sInfo = getResources().getString(R.string.sMale);
                else sInfo = getResources().getString(R.string.sFeMale);
                InfoDialog.setTitle(R.string.sShowUserInfo)
                        .setCancelable(false)
                        .setMessage(getResources().getString(R.string.sLogin) + " : "+ user.Login +"\n"+getResources().getString(R.string.sWeight)+ " : "+user.Weight
                                +"\n"+getResources().getString(R.string.sSex)+ " : "+sInfo)
                        .setNegativeButton(R.string.sOk, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = InfoDialog.create();
                dialog.show();

            }break;
            case R.id.iChangeWeight: {
                AlertDialog.Builder ChWeightDialog = new AlertDialog.Builder(this);
                final EditText eWeight = new EditText(ChWeightDialog.getContext());
                eWeight.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eWeight.setHint(R.string.sWeight);
                eWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ChWeightDialog.setTitle(R.string.sChangeWeight)
                        .setView(eWeight)
                        .setCancelable(false)
                        .setPositiveButton(R.string.sConfirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int t = user.Weight;
                                try {
                                    user.Weight = Integer.parseInt(eWeight.getText().toString());
                                }catch(NumberFormatException e){
                                    user.Weight=t;
                                }
                                if (user.Weight<=0)
                                {
                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sMinusWeight),Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                user.RefreshUser(getApplicationContext());

                            }
                        })
                        .setNegativeButton(R.string.sCancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = ChWeightDialog.create();
                dialog.show();

            }break;
            case R.id.iAbout: {
                Intent iAbout = new Intent(AlcoholListActivity.this,AboutActivity.class);
                startActivity(iAbout);

            }break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finishAffinity();
    }

}
