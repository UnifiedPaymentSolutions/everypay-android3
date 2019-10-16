package com.everypay.sdk.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everypay.sdk.R;

public class DialogUtils {
    private static final int FLAG_RESOURCE_NULL = -1;

    public interface AppDialogCallBack {
        /**
         * @param pAlertDialog
         * @param pDialogType
         */
        void onClickDialog(DialogInterface pAlertDialog, int pDialogType);
    }

    /**
     * Get AlertDialog
     *
     * @param context
     * @param titleResId          Set -1 if do not want to show
     * @param messageResId        Set -1 if do not want to show
     * @param positiveButtonResId Set -1 if do not want to show
     * @param negativeButtonResId Set -1 if do not want to show
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialog(Context context, int titleResId, int messageResId, int positiveButtonResId, int negativeButtonResId, int neutralButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialog(context, titleResId, context.getString(messageResId), positiveButtonResId, negativeButtonResId, neutralButtonResId, callBack);
    }

    public static AlertDialog getAlertDialog(Context context, int titleResId, String message, int positiveButtonResId, int negativeButtonResId, int neutralButtonResId, final AppDialogCallBack callBack) {
        final int buttonTextColor = ContextCompat.getColor(context, R.color.ep_accent);
        final int buttonNegativeColor = ContextCompat.getColor(context, android.R.color.black);
        final int buttonPositiveColor = ContextCompat.getColor(context, R.color.ep_primary);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context) {
            @NonNull
            @Override
            public AlertDialog show() {
                final AlertDialog alertDialog = super.show();
                //Only after .show() was called
                TextView messageView = alertDialog.findViewById(android.R.id.message);
                if (messageView != null)
                    messageView.setGravity(Gravity.CENTER);
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(buttonPositiveColor);
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(buttonNegativeColor);
                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(buttonTextColor);
                return alertDialog;
            }
        };
        if (titleResId != FLAG_RESOURCE_NULL) {
            String title = context.getResources().getString(titleResId);
            SpannableString str = new SpannableString(title);
            str.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.black)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            alertDialogBuilder.setTitle(str);
        }
        if (!TextUtils.isEmpty(message)) {
            alertDialogBuilder.setMessage(message);
        }
        if (positiveButtonResId != FLAG_RESOURCE_NULL) {
            alertDialogBuilder.setPositiveButton(positiveButtonResId, (dialogInterface, which) -> {
                if (callBack != null) {
                    callBack.onClickDialog(dialogInterface, DialogInterface.BUTTON_POSITIVE);
                }
            });
        }
        if (neutralButtonResId != FLAG_RESOURCE_NULL) {
            alertDialogBuilder.setNeutralButton(neutralButtonResId, (dialogInterface, i) -> {
                if (callBack != null) {
                    callBack.onClickDialog(dialogInterface, DialogInterface.BUTTON_NEUTRAL);
                }
            });
        }
        if (negativeButtonResId != FLAG_RESOURCE_NULL) {
            alertDialogBuilder.setNegativeButton(negativeButtonResId, (dialogInterface, which) -> {
                if (callBack != null) {
                    callBack.onClickDialog(dialogInterface, DialogInterface.BUTTON_NEGATIVE);
                }
            });
        }

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            //Only after .show() was called
            TextView messageView = alertDialog.findViewById(android.R.id.message);
            if (messageView != null) {
                messageView.setPadding(messageView.getPaddingStart(), 20, messageView.getPaddingEnd(), 0);
                messageView.setGravity(Gravity.CENTER_VERTICAL);
            }
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(buttonPositiveColor);
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(buttonNegativeColor);
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(buttonTextColor);
        });
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);
        return alertDialog;
    }

    public static AlertDialog getAlertDialogCustom(Context context, int customLayout, String title, String message, int positiveButtonResId, int negativeButtonResId, int neutralButtonResId, final AppDialogCallBack callBack) {
        final int buttonTextColor = ContextCompat.getColor(context, R.color.ep_accent);
        final int buttonNegativeColor = ContextCompat.getColor(context, android.R.color.black);
        final int buttonPositiveColor = ContextCompat.getColor(context, R.color.ep_primary);
        View view = LayoutInflater.from(context).inflate(customLayout, null);
        float dpi = context.getResources().getDisplayMetrics().density;
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context) {
            @NonNull
            @Override
            public AlertDialog show() {
                final AlertDialog alertDialog = super.show();
                //Only after .show() was called
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(buttonPositiveColor);
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(buttonNegativeColor);
                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(buttonTextColor);
                return alertDialog;
            }
        };
        if (!TextUtils.isEmpty(title)) {
            SpannableString str = new SpannableString(title);
            str.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.black)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            alertDialogBuilder.setTitle(str);
        }
        if (!TextUtils.isEmpty(message)) {
            alertDialogBuilder.setMessage(message);
        }
        if (positiveButtonResId != FLAG_RESOURCE_NULL) {
            alertDialogBuilder.setPositiveButton(positiveButtonResId, (dialogInterface, which) -> {
                if (callBack != null) {
                    callBack.onClickDialog(dialogInterface, DialogInterface.BUTTON_POSITIVE);
                }
            });
        }
        if (neutralButtonResId != FLAG_RESOURCE_NULL) {
            alertDialogBuilder.setNeutralButton(neutralButtonResId, (dialogInterface, i) -> {
                if (callBack != null) {
                    callBack.onClickDialog(dialogInterface, DialogInterface.BUTTON_NEUTRAL);
                }
            });
        }
        if (negativeButtonResId != FLAG_RESOURCE_NULL) {
            alertDialogBuilder.setNegativeButton(negativeButtonResId, (dialogInterface, which) -> {
                if (callBack != null) {
                    callBack.onClickDialog(dialogInterface, DialogInterface.BUTTON_NEGATIVE);
                }
            });
        }

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            //Only after .show() was called
            TextView messageView = alertDialog.findViewById(android.R.id.title);
            if (messageView != null) {
                messageView.setPadding(messageView.getPaddingStart(), 20, messageView.getPaddingEnd(), 0);
                messageView.setGravity(Gravity.CENTER_VERTICAL);
            }
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(buttonPositiveColor);
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(buttonNegativeColor);
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(buttonTextColor);
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(v -> {
                if (callBack != null) {
                    callBack.onClickDialog(dialogInterface, DialogInterface.BUTTON_NEUTRAL);
                }
            });
        });
        alertDialog.setView(view, (int) (15 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);
        return alertDialog;
    }

    public static AlertDialog getAlertDialogCustom(Context context, int customLayout) {
        return getAlertDialogCustom(context, customLayout, "", "", FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, null);
    }

    /**
     * Get AlertDialog with 2 buttons button no title
     *
     * @param context
     * @param positiveButtonResId
     * @param negativeButtonResId
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialogCustom(Context context, int customLayout, int positiveButtonResId, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialogCustom(context, customLayout, "", "", positiveButtonResId, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    /**
     * Get AlertDialog with 2 buttons button has title
     *
     * @param context
     * @param positiveButtonResId
     * @param negativeButtonResId
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialogCustom(Context context, int customLayout, int titleResId, int positiveButtonResId, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialogCustom(context, customLayout, context.getString(titleResId), "", positiveButtonResId, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    /**
     * Get AlertDialog with 2 buttons button has title
     *
     * @param context
     * @param positiveButtonResId
     * @param negativeButtonResId
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialogCustom(Context context, int customLayout, String title, int positiveButtonResId, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialogCustom(context, customLayout, title, "", positiveButtonResId, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    public static AlertDialog getAlertDialogOneButton(Context context, int titleResId, String messageResId, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialog(context, titleResId, messageResId, FLAG_RESOURCE_NULL, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    /**
     * Get AlertDialog with 2 buttons button has title
     *
     * @param context
     * @param messageResId
     * @param positiveButtonResId
     * @param negativeButtonResId
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialog(Context context, int titleResId, int messageResId, int positiveButtonResId, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialog(context, titleResId, messageResId, positiveButtonResId, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    /**
     * Get AlertDialog with 2 buttons button no title
     *
     * @param context
     * @param messageResId
     * @param positiveButtonResId
     * @param negativeButtonResId
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialog(Context context, int messageResId, int positiveButtonResId, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialog(context, FLAG_RESOURCE_NULL, messageResId, positiveButtonResId, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    /**
     * Get AlertDialog with one negative button
     *
     * @param context
     * @param messageResId
     * @param negativeButtonResId
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialog(Context context, int messageResId, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialog(context, FLAG_RESOURCE_NULL, messageResId, FLAG_RESOURCE_NULL, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    /**
     * Get AlertDialog with one negative button
     *
     * @param context
     * @param messageContent
     * @param negativeButtonResId
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialog(Context context, String messageContent, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialog(context, FLAG_RESOURCE_NULL, messageContent, FLAG_RESOURCE_NULL, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    /**
     * Get AlertDialog with one negative button and title
     *
     * @param context
     * @param messageTitleId
     * @param messageContent
     * @param negativeButtonResId
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialog(Context context, int messageTitleId, String messageContent, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialog(context, messageTitleId, messageContent, FLAG_RESOURCE_NULL, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    /**
     * Get AlertDialog with one negative button and title
     *
     * @param context
     * @param messageTitleId
     * @param messageContent
     * @param negativeButtonResId
     * @param callBack
     * @return
     */
    public static AlertDialog getAlertDialog(Context context, int messageTitleId, String messageContent, int positiveButtonResId, int negativeButtonResId, final AppDialogCallBack callBack) {
        return getAlertDialog(context, messageTitleId, messageContent, positiveButtonResId, negativeButtonResId, FLAG_RESOURCE_NULL, callBack);
    }

    /**
     * thanh.vn Show alert dialog with ok and cancel button
     *
     * @param pContext
     * @param pMsgContentResId
     * @param pDialogCallBack
     * @return
     */
    public static AlertDialog getBackAlertDialog(Context pContext, int titleResId, int pMsgContentResId, final AppDialogCallBack pDialogCallBack) {
        return getAlertDialog(pContext, titleResId, pMsgContentResId, R.string.ep_action_back, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, pDialogCallBack);
    }

    public static AlertDialog getBackAlertDialog(Context pContext, int titleResId, String pMsgContent, final AppDialogCallBack pDialogCallBack) {
        return getAlertDialog(pContext, titleResId, pMsgContent, R.string.ep_action_back, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, pDialogCallBack);
    }

    /**
     * thanh.vn Show alert dialog with ok and cancel button
     *
     * @param pContext
     * @param pMsgContentResId
     * @param pDialogCallBack
     * @return
     */
    public static AlertDialog getBackAlertDialog(Context pContext, int pMsgContentResId, final AppDialogCallBack pDialogCallBack) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContentResId, R.string.ep_action_back, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, pDialogCallBack);
    }

    /**
     * thanh.vn Show alert dialog with ok and cancel button
     *
     * @param pContext
     * @param pMsgContent
     * @param pDialogCallBack
     * @return
     */
    public static AlertDialog getBackAlertDialog(Context pContext, String pMsgContent, final AppDialogCallBack pDialogCallBack) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContent, R.string.ep_action_back, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, pDialogCallBack);
    }

    /**
     * thanh.vn Show alert dialog with ok and cancel button
     *
     * @param pContext
     * @param pMsgContentResId
     * @param pDialogCallBack
     * @return
     */
    public static AlertDialog getOkCancelAlertDialog(Context pContext, int pMsgContentResId, final AppDialogCallBack pDialogCallBack) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContentResId, R.string.ep_alert_ok_button, R.string.ep_action_back, FLAG_RESOURCE_NULL, pDialogCallBack);
    }

    public static AlertDialog getOkCancelAlertDialog(Context pContext, String pMsgContentResContent, final AppDialogCallBack pDialogCallBack) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContentResContent, R.string.ep_alert_ok_button, R.string.ep_action_back, FLAG_RESOURCE_NULL, pDialogCallBack);
    }

    public static AlertDialog getYesNoAlertDialog(Context pContext, String pMsgContentResContent, final AppDialogCallBack pDialogCallBack) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContentResContent, R.string.ep_alert_yes_button, R.string.ep_alert_no_button, FLAG_RESOURCE_NULL, pDialogCallBack);
    }

    public static AlertDialog getOkCancelAlertDialog(Context pContext, int pMsgContentResId) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContentResId, R.string.ep_alert_ok_button, R.string.ep_action_back, FLAG_RESOURCE_NULL, new SimpleDialogDismissListener());
    }

    /**
     * thanh.vn Show alert dialog with ok button
     *
     * @param pContext
     * @param pMsgContentResId
     * @param pDialogCallBack
     * @return
     */
    public static AlertDialog getOkAlertDialog(Context pContext, int pMsgContentResId, final AppDialogCallBack pDialogCallBack) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContentResId, R.string.ep_alert_ok_button, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, pDialogCallBack);
    }

    public static AlertDialog getOkAlertDialog(Context pContext, String pMsgContent, final AppDialogCallBack pDialogCallBack) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContent, R.string.ep_alert_ok_button, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, pDialogCallBack);
    }

    public static AlertDialog getOkAlertDialog(Context pContext, int pMsgContentResId) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContentResId, R.string.ep_alert_ok_button, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, new SimpleDialogDismissListener());
    }

    public static AlertDialog getOkAlertDialogWithTitle(Context pContext, int pMsgTitleResId, int pMsgContentResId) {
        return getAlertDialog(pContext, pMsgTitleResId, pMsgContentResId, R.string.ep_alert_ok_button, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, new SimpleDialogDismissListener());
    }

    public static AlertDialog getOkAlertDialogWithTitle(Context pContext, int pMsgTitleResId, String pMsgContent) {
        return getAlertDialog(pContext, pMsgTitleResId, pMsgContent, R.string.ep_alert_ok_button, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, new SimpleDialogDismissListener());
    }

    public static AlertDialog getOkAlertDialog(Context pContext, String pMsgContentRes) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, pMsgContentRes, R.string.ep_alert_ok_button, FLAG_RESOURCE_NULL, FLAG_RESOURCE_NULL, new SimpleDialogDismissListener());
    }

    public static AlertDialog getThreeButtonsAlertDialog(Context pContext, String msgContent, AppDialogCallBack callBack) {
        return getAlertDialog(pContext, FLAG_RESOURCE_NULL, msgContent, R.string.ep_alert_yes_button, R.string.ep_alert_no_button, R.string.ep_alert_yes_all, callBack);
    }

    public static AlertDialog getAlertDialogNotFound(Context context, String message, String documentName) {
        return getOkAlertDialog(context, Html.fromHtml(String.format(message, documentName)).toString());
    }

    public static class SimpleDialogDismissListener implements AppDialogCallBack {

        /**
         * @param pAlertDialog
         * @param pDialogType
         */
        @Override
        public void onClickDialog(DialogInterface pAlertDialog, int pDialogType) {
            pAlertDialog.dismiss();
        }
    }

    public static void showDialogNoNetworkNoTitle(final Context pContext, int resMessage) {
        DialogUtils.getAlertDialog(pContext, FLAG_RESOURCE_NULL, resMessage, R.string.ep_accept, R.string.ep_setting_internet, new AppDialogCallBack() {

            @Override
            public void onClickDialog(DialogInterface pAlertDialog, int pDialogType) {
                pAlertDialog.dismiss();
                if (pDialogType == DialogInterface.BUTTON_NEGATIVE) {
                    pContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        }).show();
    }

    public static void showDialogNoNetworkNoTitle(final Context pContext, String message) {
        DialogUtils.getAlertDialog(pContext, FLAG_RESOURCE_NULL, message, R.string.ep_accept, R.string.ep_setting_internet, new AppDialogCallBack() {

            @Override
            public void onClickDialog(DialogInterface pAlertDialog, int pDialogType) {
                pAlertDialog.dismiss();
                if (pDialogType == DialogInterface.BUTTON_NEGATIVE) {
                    pContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        }).show();
    }

    public static void showDialogNoNetworkWithTitle(final Context pContext, int titleResId) {
        DialogUtils.getAlertDialog(pContext, titleResId, R.string.ep_err_network_failure, R.string.ep_accept, R.string.ep_setting_internet, new AppDialogCallBack() {

            @Override
            public void onClickDialog(DialogInterface pAlertDialog, int pDialogType) {
                pAlertDialog.dismiss();
                if (pDialogType == DialogInterface.BUTTON_NEGATIVE) {
                    pContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        }).show();
    }

    public static void showDialogNoNetworkNoTitle(final Context pContext) {
        DialogUtils.getAlertDialog(pContext, R.string.ep_err_network_failure, R.string.ep_accept, R.string.ep_setting_internet, new AppDialogCallBack() {

            @Override
            public void onClickDialog(DialogInterface pAlertDialog, int pDialogType) {
                pAlertDialog.dismiss();
                if (pDialogType == DialogInterface.BUTTON_NEGATIVE) {
                    pContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        }).show();
    }

}