package com.ruaho.note.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;


public class DialogUtils {
    public static void createDialog(final Activity activity,int titleID,int okId,int cancelId,final NoteDialogInterface callBack){
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(titleID)
//                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(okId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(callBack != null){
                            callBack.Ok();
                        }
                        //Toast.makeText(activity,"成功",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(cancelId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(callBack != null){
                            callBack.cancel();
                        }
                        //Toast.makeText(activity,"失败",Toast.LENGTH_LONG).show();
                    }
                }).show();
//        dialog.setCanceledOnTouchOutside(false);
    }

    public static void createOKDialog(final Activity activity,int titleID,int okId,final NoteDialogInterface callBack){
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(titleID)
//                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(okId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(callBack != null){
                            callBack.Ok();
                        }
                        //Toast.makeText(activity,"成功",Toast.LENGTH_LONG).show();
                    }
                }).show();
//        dialog.setCanceledOnTouchOutside(false);
    }

    public interface NoteDialogInterface{
        public void Ok();
        public void cancel();
    }
}
