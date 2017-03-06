package com.example.admin.puresults.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.admin.puresults.R;

/**
 * Created by admin on 3/4/2017.
 */

public class AlertUtils {

    public static void showAlert(Context context,String message,boolean isCancelable){
        new AlertDialog.Builder(context)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(isCancelable).show();
    }
}
