package ua.jaraxxus.drinkmeter;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by DonP3tru4io on 04.11.2017.
 */

public class Drink implements Serializable {

    String Name;
    int IngredientPortion[];
    String Others;
    int Index;

    Drink (int index,String name,int[] ingPort,String other)
    {
        Index = index;
        Name = name;
        IngredientPortion = ingPort;
        Others = other;
    }

    public float getAlcohol(Context context)
    {
        String[] AlcPercent = context.getResources().getStringArray(R.array.AlcoholPercent);
        float Alcohol=0;
        for (int i = 0;i<20;i++)
        {
            Alcohol+=IngredientPortion[i]* Float.parseFloat(AlcPercent[i])*0.7893;
        }
        return Alcohol;

    }
}