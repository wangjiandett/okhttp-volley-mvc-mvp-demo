/**
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiange.okhttp.okhttp.R;


/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2015年8月29日
 */
public class LoadDialogUtils {
    
    /**
     * 显示加载进度条
     *
     * @param context
     * @return
     */
    public static Dialog showLoadingDialog(Context context, String message) {
        Dialog loadingDialog = new Dialog(context, R.style.ProgressHUD);
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog_layout, null);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局

        TextView showText = (TextView) view.findViewById(R.id.loading_text);
        if (message != null) {
            showText.setText(message);
        }
        else {
            showText.setText(context.getString(R.string.common_loading));
        }
        loadingDialog.show();
        return loadingDialog;
    }
    
    /**
     * 隐藏进度条
     *
     * @param dialog
     */
    public static void hideLodingDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 显示通用对话框
     *
     * @param context
     * @return
     */
    public static Dialog showDialog(Context context, int layoutid) {
        Dialog dialog = new Dialog(context, R.style.ProgressHUD);
        View v = View.inflate(context, layoutid, null);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setContentView(v);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return dialog;
    }
}
