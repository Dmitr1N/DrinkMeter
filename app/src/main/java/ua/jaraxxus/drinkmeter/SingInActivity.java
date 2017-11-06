package ua.jaraxxus.drinkmeter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyStore;

import javax.crypto.Cipher;

public class SingInActivity extends AppCompatActivity {

    EditText eLogin;
    EditText ePassword;
    EditText eRepPassword;
    EditText eWeight;
    Button bEndSingIn;
    RadioButton rbMale;
    CheckBox cbFingerPrint;

    final int NO_PERMISSION =1;
    final int NO_REGISTERED = 2;
    final int NO_LOCK = 3;
    final int NON_FOUND = -1;
    final int NORMAL =0;

    private static final String FINGER_PRINT = Manifest.permission.USE_FINGERPRINT;

    private static final int REQUEST_FINGER_PRINT = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eLogin = (EditText)findViewById(R.id.eLogin);
        ePassword = (EditText)findViewById(R.id.ePassword);
        eRepPassword = (EditText)findViewById(R.id.eRepPassword);
        eWeight = (EditText)findViewById(R.id.eWeight);
        bEndSingIn = (Button)findViewById(R.id.bEndSingIn);
        rbMale = (RadioButton)findViewById(R.id.rbMale);
        cbFingerPrint = (CheckBox)findViewById(R.id.cbFingerPrint);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            checkFingerPrintSensorCode(fingerprintManager, keyguardManager);
        }

        bEndSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eLogin.getText().toString().isEmpty() || ePassword.getText().toString().isEmpty()||
                        eRepPassword.getText().toString().isEmpty()||eWeight.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sEmptyField),Toast.LENGTH_SHORT).show();
                    return;
                }

                if(ePassword.getText().toString().compareTo(eRepPassword.getText().toString())!=0)
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sErrorRepPassword),Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Integer.parseInt(eWeight.getText().toString())<=0)
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sMinusWeight),Toast.LENGTH_SHORT).show();
                    return;
                }


                File IsUser = new File(getFilesDir(), eLogin.getText().toString());
                if(IsUser.exists())
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sSameUser),Toast.LENGTH_SHORT).show();
                    return;
                }

                FileOutputStream outputStream;
                ObjectOutputStream oos;
                try {
                    outputStream = openFileOutput(eLogin.getText().toString(), MODE_PRIVATE);
                    oos = new ObjectOutputStream(outputStream);
                    oos.writeObject(new User(eLogin.getText().toString(),ePassword.getText().toString(),
                            Integer.parseInt(eWeight.getText().toString()),cbFingerPrint.isChecked(),rbMale.isChecked(),new int[42]));
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sSingInError),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sSingInError),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.sUserAdded),Toast.LENGTH_SHORT).show();
                finish();


            }
        });
    }


    @TargetApi(Build.VERSION_CODES.M)
    int checkFingerPrintSensorCode(FingerprintManager fingerprintManager, KeyguardManager keyguardManager)
    {
        int code=NON_FOUND;
        if (fingerprintManager.isHardwareDetected())
        {
            // Checks whether fingerprint permission is set on manifest
            cbFingerPrint.setVisibility(CheckBox.VISIBLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                code = NO_PERMISSION;
                requestPermission(FINGER_PRINT, REQUEST_FINGER_PRINT);
                Toast.makeText(this,R.string.sNoPermissionFP,Toast.LENGTH_SHORT).show();
                return code;
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                   code =NO_REGISTERED;
                    Toast.makeText(this,R.string.sNoFPRegistered,Toast.LENGTH_SHORT).show();
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                       code = NO_LOCK;
                        Toast.makeText(this,R.string.sLockScreenDisabled,Toast.LENGTH_SHORT).show();
                    } else {
                        code = NORMAL;
                        cbFingerPrint.setEnabled(true);
                    }
                }
            }
        }
        return code;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isFingerPrintAvailable(FingerprintManager fingerprintManager)
    {
        if (!fingerprintManager.isHardwareDetected()) return false;
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission(String permission, int requestCode) {
        // запрашиваем разрешение
        ActivityCompat.requestPermissions(this,
                new String[]{permission}, requestCode);
    }

}
