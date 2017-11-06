package ua.jaraxxus.drinkmeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

/**
 * Created by DonP3tru4io on 04.11.2017.
 */

public class DrinkListElement extends LinearLayout {


    TextView tvName;
    ImageButton ibInfo;
    CheckBox cbUsed;
    ImageView ivPicture;
    EditText eCount;
    Drink _drink;
    int _index;
    User _user;
    Context _Parent;

    DrinkListElement(Context AppContext,Context Parent, Drink drink, final int index, User user)
    {
        super(Parent);
        setOrientation(LinearLayout.HORIZONTAL);

        _Parent = AppContext;
        _drink = drink;
        _index = index;
        _user = user;

        String[] DrName = _Parent.getResources().getStringArray(R.array.DrinksName);
        LinearLayout.LayoutParams rule0 = new  LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        rule0.gravity=Gravity.CENTER;
        rule0.weight=1f;

        cbUsed = new CheckBox(Parent);
        addView(cbUsed,rule0);


        LinearLayout.LayoutParams rule1 = new  LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        rule1.gravity=Gravity.CENTER;
        rule1.weight=2f;
        //rule1.weight= (int)getResources().getDimension(R.dimen.ZeroDP);

        ivPicture = new ImageView(Parent);
        try
        {
            // get input stream
            InputStream ims = AppContext.getAssets().open(_drink.Name+".png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            ivPicture.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivPicture.setAdjustViewBounds(false);
            ivPicture.setImageDrawable(d);
            ims .close();
        }
        catch(IOException ex)
        {
            return;
        }
        addView(ivPicture,rule1);


        LinearLayout.LayoutParams rule2 = new  LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        rule2.gravity=Gravity.CENTER;
        rule2.weight=25f;
        rule2.width=(int)getResources().getDimension(R.dimen.ZeroDP);
        tvName = new TextView(Parent);
        tvName.setText(DrName[_index]);
        tvName.setClickable(true);
        tvName.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tvName.setGravity(Gravity.CLIP_HORIZONTAL|Gravity.CLIP_VERTICAL|Gravity.CENTER);
        addView(tvName,rule2);

        LinearLayout.LayoutParams rule3 = new  LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        rule3.gravity=Gravity.CENTER;
        rule3.weight=2f;

        ibInfo = new ImageButton(Parent);
        ibInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_outline_black_24dp));
        addView(ibInfo,rule3);

        LinearLayout.LayoutParams rule4 = new  LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        rule4.gravity=Gravity.CENTER;
        rule4.weight=3f;

        eCount = new EditText(Parent);
        if (_user.Drinks[index]==0)
        {
            eCount.setEnabled(false);
            eCount.setText("0");
            cbUsed.setChecked(false);
        }
        else
        {
            eCount.setEnabled(true);
            eCount.setText(""+_user.Drinks[index]);
            cbUsed.setChecked(true);
        }

        eCount.setEms(2);
        eCount.setTextAlignment(EditText.TEXT_ALIGNMENT_CENTER);
        eCount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        addView(eCount,rule4);

        cbUsed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b)
                {
                    eCount.setText("0");
                    _user.Drinks[index]=0;
                    _user.RefreshUser(_Parent);
                    eCount.setEnabled(false);
                }
                else
                {
                    eCount.setText("1");
                    _user.Drinks[index]=1;
                    _user.RefreshUser(_Parent);
                    eCount.setEnabled(true);
                }
            }
        });

        ibInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DrinkInfo = new Intent(getContext(),DrinkInfoActivity.class);
                DrinkInfo.putExtra("picture",_drink.Name);
                DrinkInfo.putExtra("index",_drink.Index);
                DrinkInfo.putExtra("ingredients",_drink.IngredientPortion);
                getContext().startActivity(DrinkInfo);
            }
        });

        tvName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cbUsed.setChecked(!cbUsed.isChecked());
            }
        });

        eCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    _user.Drinks[index] = Integer.parseInt(eCount.getText().toString());
                }catch(NumberFormatException e){
                    _user.Drinks[index]=0;
                    }
                _user.RefreshUser(_Parent);
            }
        });

    }

}
