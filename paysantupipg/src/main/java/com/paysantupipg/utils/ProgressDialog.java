package com.paysantupipg.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialog;
import com.paysantupipg.R;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

public class ProgressDialog extends AppCompatDialog {
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ProgressDialog(Context context) {
        super(context);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.context = context;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}