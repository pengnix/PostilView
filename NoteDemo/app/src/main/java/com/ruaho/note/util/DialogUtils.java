package com.ruaho.note.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

public class DialogUtils {
    public static void createDialog(final Activity activity){
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle("警告")
//                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity,"成功",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(activity,"失败",Toast.LENGTH_LONG).show();
                    }
                }).show();
//        dialog.setCanceledOnTouchOutside(false);
    }
}
