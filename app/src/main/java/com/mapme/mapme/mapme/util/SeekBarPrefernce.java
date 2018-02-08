package com.mapme.mapme.mapme.util;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mapme.mapme.mapme.R;

/**
 * Created by Yitschak on 05/02/2018.
 */


public class SeekBarPrefernce extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

    TextView radius;
    SeekBar seekBar;
    float radiusValue;

    public SeekBarPrefernce(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.seekbar);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);


    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        radius = view.findViewById(R.id.tv_radius_preference);
        seekBar = view.findViewById(R.id.seekbar_preferece);

        seekBar.setOnSeekBarChangeListener(this);

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            persistFloat(radiusValue);
        }


    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        radiusValue = 10 + (progress * 10f);

        radius.setText(String.valueOf((int) radiusValue));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

