package ua.jaraxxus.drinkmeter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class DrinkInfoActivity extends AppCompatActivity {

    ImageView ivDrinkPicture;
    LinearLayout llInfoLayout;
    TextView InfoLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivDrinkPicture = (ImageView)findViewById(R.id.ivDrinkPicture);
        llInfoLayout = (LinearLayout)findViewById(R.id.llInfoLayout);

        Intent DrinkInfo = getIntent();

        String Picture = DrinkInfo.getStringExtra("picture");
        int Index = DrinkInfo.getIntExtra("index",-1);
        int[] Ingredient = DrinkInfo.getIntArrayExtra("ingredients");
        String[] DrName = getResources().getStringArray(R.array.DrinksName);
        String[] IngName = getResources().getStringArray(R.array.IngredientName);
        String[] OtherName = getResources().getStringArray(R.array.DrinksOtherIngredients);
        InfoLine = (TextView)findViewById(R.id.InfoLine);

        try
        {
            // get input stream
            InputStream ims = getApplicationContext().getAssets().open(Picture+".png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            ivDrinkPicture.setImageDrawable(d);
            ims .close();
        }
        catch(IOException ex)
        {
            return;
        }

        InfoLine.setText("\n"+DrName[Index]+"\n");

        TextView AlcIng = new TextView(llInfoLayout.getContext());
        AlcIng.setText("\n"+getResources().getString(R.string.sAclIng)+"\n");
        AlcIng.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        AlcIng.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
        llInfoLayout.addView(AlcIng);

        for (int i = 0; i<Ingredient.length;i++)
        {
            if (Ingredient[i]!=0) {
                String InfoText =IngName[i] + " : " + Ingredient[i] + " " + getResources().getString(R.string.ml);
                TextView temp = new TextView(llInfoLayout.getContext());
                temp.setText(InfoText);
                temp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                llInfoLayout.addView(temp);
            }
        }

        TextView OthIng = new TextView(llInfoLayout.getContext());
        OthIng.setText("\n"+getResources().getString(R.string.sOthIng));
        OthIng.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        OthIng.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
        llInfoLayout.addView(OthIng);

        TextView InfoLineOther = new TextView(llInfoLayout.getContext());
        InfoLineOther.setText(OtherName[Index]+"\n");
        InfoLineOther.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        llInfoLayout.addView(InfoLineOther);

    }

}
