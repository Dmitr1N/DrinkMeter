package ua.jaraxxus.drinkmeter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {

    EditText eLLogin;
    EditText eLPassword;
    Button bEndLogin;
    CheckBox cbRememberMe;
    SharedPreferences sPref;
    final String IsRemember = "RememberMe";
    final String UserLogin = "UserLogin";

    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "FingerPrint";
    private Cipher cipher;


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
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eLLogin = (EditText)findViewById(R.id.eLLogin);
        eLPassword = (EditText)findViewById(R.id.eLPassword);
        cbRememberMe = (CheckBox)findViewById(R.id.cbRememderMe);
        bEndLogin = (Button) findViewById(R.id.bEndLogin);

        sPref = getSharedPreferences("sharedSettings",MODE_PRIVATE);

        StartAuthenticate();

        bEndLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File IsUser = new File(getFilesDir(), eLLogin.getText().toString());

                if (!IsUser.exists() || eLPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sLoginError),Toast.LENGTH_SHORT).show();
                    return;

                }

                FileInputStream inputStream;
                ObjectInputStream ois;

                User user=null;
                try {
                    inputStream = openFileInput(eLLogin.getText().toString());
                    ois = new ObjectInputStream(inputStream);
                    user=(User)ois.readObject();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sLoginError),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sLoginError),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                } catch (ClassNotFoundException e) {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sLoginError),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }

                if (user.Password.compareTo(eLPassword.getText().toString())!=0)
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.sLoginError),Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent DrinkList = new Intent(LoginActivity.this,AlcoholListActivity.class);
                DrinkList.putExtra("login",user.Login);

                eLLogin.setText("");
                eLPassword.setText("");

                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean(IsRemember,cbRememberMe.isChecked());
                ed.putString(UserLogin,user.Login);
                ed.commit();

                startActivity(DrinkList);

            }
        });
    }
//////////////
    @TargetApi(Build.VERSION_CODES.M)
    int checkFingerPrintSensorCode(FingerprintManager fingerprintManager, KeyguardManager keyguardManager)
    {
        int code=NON_FOUND;
        if (fingerprintManager.isHardwareDetected())
        {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(this, FINGER_PRINT) != PackageManager.PERMISSION_GRANTED) {
                code = NO_PERMISSION;
                requestPermission(FINGER_PRINT, REQUEST_FINGER_PRINT);
                return code;
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    code =NO_REGISTERED;
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                        code = NO_LOCK;
                    } else {
                        code = NORMAL;
                    }
                }
            }
        }
        return code;
    }
//////////////////////////////////////////
    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }


        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }


        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    boolean Authenticate(FingerprintManager fingerprintManager)
    {
        generateKey();
        if (cipherInit()) {
            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintHandler helper = new FingerprintHandler(this,this);
            helper.startAuth(fingerprintManager, cryptoObject);
        }
        return true;
    }

    void StartAuthenticate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            if (checkFingerPrintSensorCode(fingerprintManager, keyguardManager) == NORMAL) {
                Authenticate(fingerprintManager);
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission(String permission, int requestCode) {
        // запрашиваем разрешение
        ActivityCompat.requestPermissions(this,
                new String[]{permission}, requestCode);
    }

}

