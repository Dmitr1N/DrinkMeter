package ua.jaraxxus.drinkmeter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.crypto.Cipher;

/**
 * Created by DonP3tru4io on 06.11.2017.
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context context;
    private LoginActivity _class;

    // Constructor
    public FingerprintHandler(Context mContext,LoginActivity _Class) {
        context = mContext;
        _class = _Class;
    }


    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
    }


    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context,R.string.sFPFail,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        File IsUser = new File(context.getFilesDir(),_class.eLLogin.getText().toString());
        if (IsUser.exists())
        {
            FileInputStream inputStream;
            ObjectInputStream ois;

            User user=null;
            try {
                inputStream = context.openFileInput(_class.eLLogin.getText().toString());
                ois = new ObjectInputStream(inputStream);
                user=(User)ois.readObject();
            } catch (FileNotFoundException ex) {
                Toast.makeText(context.getApplicationContext(),context.getResources().getString(R.string.sLoginError),Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
                return;
            } catch (IOException ex) {
                Toast.makeText(context.getApplicationContext(),context.getResources().getString(R.string.sLoginError),Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
                return;
            } catch (ClassNotFoundException ex) {
                Toast.makeText(context.getApplicationContext(),context.getResources().getString(R.string.sLoginError),Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
                return;
            }

            if (user.IsFingerPrint) {
                SharedPreferences sPref = context.getSharedPreferences("sharedSettings", context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean(_class.IsRemember, _class.cbRememberMe.isChecked());
                ed.putString(_class.UserLogin, _class.eLLogin.getText().toString());
                ed.commit();
                Toast.makeText(context,R.string.sFPSuc,Toast.LENGTH_SHORT).show();
                Intent DrinkList = new Intent(context, AlcoholListActivity.class);
                DrinkList.putExtra("login",user.Login);
                context.startActivity(DrinkList);
            }
        }
        else
        {
            Toast.makeText(context,R.string.sLoginError,Toast.LENGTH_SHORT).show();
        }

    }

}
