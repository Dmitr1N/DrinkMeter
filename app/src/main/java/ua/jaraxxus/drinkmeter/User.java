package ua.jaraxxus.drinkmeter;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by DonP3tru4io on 04.11.2017.
 */

public class User implements Serializable{

    String Login;
    String Password;
    int Weight;
    boolean IsFingerPrint;
    boolean Sex;
    int[] Drinks;

    User (String Login,String Password,int Weight,boolean IsFingerPrint,boolean Sex,int[] Drinks)
    {
        this.Login = Login;
        this.Password = Password;
        this.Weight = Weight;
        this.IsFingerPrint = IsFingerPrint;
        this.Sex = Sex;
        this.Drinks = Drinks;
    }

    public void RefreshUser(Context context)
    {
        FileOutputStream outputStream;
        ObjectOutputStream oos;
        try {
            outputStream = context.openFileOutput(Login, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(outputStream);
            oos.writeObject(this);
            outputStream.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(context, context.getResources().getString(R.string.sDataError), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        } catch (IOException e) {
            Toast.makeText(context,context.getResources().getString(R.string.sDataError), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
    }

}
