package org.fossasia.phimpme.gallery.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;

import java.util.Objects;

import android.text.TextWatcher;

import com.google.gson.Gson;

import org.fossasia.phimpme.utilities.SnackBarHandler;

/**
 * Created by Jibo on 06/05/2016.
 */

public class SecurityHelper {
    private boolean activeSecurity;
    private boolean passwordOnDelete;
    private boolean passwordOnHidden;
    private boolean passwordOnfolder;
    private String passwordValue;
    private String[] securedfolders;
    private TextInputLayout passwordTextInputLayout;

    private Context context;

    public SecurityHelper(Context context) {
        this.context = context;
        updateSecuritySetting();
    }

    public boolean isActiveSecurity() {
        return activeSecurity;
    }

    public boolean isPasswordOnHidden() {
        return passwordOnHidden;
    }

    public boolean isPasswordOnDelete() {
        return passwordOnDelete;
    }

    public boolean isPasswordOnfolder() {
        return passwordOnfolder;
    }

    public boolean checkPassword(String pass) {
        return (isActiveSecurity() && pass.equals(passwordValue));
    }

    public void updateSecuritySetting() {
        PreferenceUtil SP = PreferenceUtil.getInstance(context);
        String secur = SP.getString(context.getString(R.string.preference_use_password_secured_local_folders), "");
        Gson gson = new Gson();
        String[] secfo = gson.fromJson(secur, String[].class);
        if (secfo != null) {
            this.securedfolders = secfo;
        }
        this.activeSecurity = SP.getBoolean(context.getString(R.string.preference_use_password), false);
        this.passwordOnDelete = SP.getBoolean(context.getString(R.string.preference_use_password_on_delete), false);
        this.passwordOnHidden = SP.getBoolean(context.getString(R.string.preference_use_password_on_hidden), true);
        this.passwordValue = SP.getString(context.getString(R.string.preference_password_value), "");
        // this.securedfolders = SP.get
        this.passwordOnfolder = SP.getBoolean(context.getString(R.string.preference_use_password_on_folder), false);
    }

    public EditText getInsertPasswordDialog(final ThemedActivity activity, final AlertDialog.Builder passwordDialog) {

        final View PasswordDialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_password, null);
        final TextView passwordDialogTitle = (TextView) PasswordDialogLayout.findViewById(R.id.password_dialog_title);
        final CardView passwordDialogCard = (CardView) PasswordDialogLayout.findViewById(R.id.password_dialog_card);
        final EditText editxtPassword = (EditText) PasswordDialogLayout.findViewById(R.id.password_edittxt);
        final TextView forgot_password = (TextView) PasswordDialogLayout.findViewById(R.id.forgot_password_button);
        passwordTextInputLayout = (TextInputLayout) PasswordDialogLayout.findViewById(R.id.password_text_input_layout);
        passwordTextInputLayout.setError(context.getString(R.string.wrong_password));
        CheckBox checkBox = (CheckBox) PasswordDialogLayout.findViewById(R.id.show_password_checkbox);
        checkBox.setText(context.getResources().getString(R.string.show_password));
        checkBox.setButtonTintList(ColorStateList.valueOf(activity.getAccentColor()));
        checkBox.setTextColor(activity.getTextColor());
        editxtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    editxtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    int position = editxtPassword.getText().length();
                    Editable editObj = editxtPassword.getText();
                    Selection.setSelection(editObj, position);
                } else {
                    editxtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    int position = editxtPassword.getText().length();
                    Editable editObj = editxtPassword.getText();
                    Selection.setSelection(editObj, position);
                }
            }
        });
        passwordTextInputLayout.setVisibility(View.GONE);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword(activity, passwordDialog);
            }
        });

        passwordDialogTitle.setBackgroundColor(activity.getPrimaryColor());
        passwordDialogCard.setBackgroundColor(activity.getCardBackgroundColor());
        ThemeHelper.setCursorDrawableColor(editxtPassword, activity.getTextColor());
        editxtPassword.getBackground().mutate().setColorFilter(activity.getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editxtPassword.setTextColor(activity.getTextColor());
        passwordDialog.setView(PasswordDialogLayout);
        return editxtPassword;
    }

    public void forgotPassword(final ThemedActivity activity, final AlertDialog.Builder passwordDialog) {
        final PreferenceUtil SP = PreferenceUtil.getInstance(context);
        final View PasswordDialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        final TextView passwordDialogTitle = (TextView) PasswordDialogLayout.findViewById(R.id.forgot_password_dialog_title);
        final TextView securityQuestion = (TextView) PasswordDialogLayout.findViewById(R.id.security_question);
        final CardView passwordDialogCard = (CardView) PasswordDialogLayout.findViewById(R.id.forgot_password_dialog_card);
        final EditText securityAnswer1 = (EditText) PasswordDialogLayout.findViewById(R.id.password_edittxt);
        securityAnswer1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        String question = "";
        question = SP.getString("Security Question", question);
        passwordDialogTitle.setText(R.string.forgot_password);
        passwordDialogTitle.setBackgroundColor(activity.getPrimaryColor());
        passwordDialogCard.setBackgroundColor(activity.getCardBackgroundColor());
        securityQuestion.setText(question);
        securityAnswer1.getBackground().mutate().setColorFilter(activity.getTextColor(), PorterDuff.Mode.SRC_ATOP);
        securityAnswer1.setTextColor(activity.getTextColor());
        securityAnswer1.setHintTextColor(activity.getSubTextColor());
        activity.setCursorDrawableColor(securityAnswer1, activity.getTextColor());

        passwordDialog.setView(PasswordDialogLayout);

        passwordDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (securityAnswer1.getText().length() == 0) {
                    Toast.makeText(activity.getApplicationContext(), "Enter Answer To the Question", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    String answer1 = "";
                    answer1 = SP.getString("Security Answer", answer1);


                    if (Objects.equals(answer1, securityAnswer1.getText().toString())) {
                        changePassword(activity, passwordDialog);
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "Incorrect Answer", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });

        passwordDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //empty method for cancel button
                //nothing is done
            }

        });

        AlertDialog dialog = passwordDialog.create();
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void changePassword(final ThemedActivity activity, final AlertDialog.Builder passwordDialog) {
        final PreferenceUtil SP = PreferenceUtil.getInstance(context);
        final View PasswordDialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_set_password, null);
        final TextView passwordDialogTitle = (TextView) PasswordDialogLayout.findViewById(R.id.password_dialog_title);
        CheckBox checkBox = (CheckBox) PasswordDialogLayout.findViewById(R.id.set_password_checkbox);
        checkBox.setText(activity.getResources().getString(R.string.show_password));
        checkBox.setTextColor(activity.getTextColor());
        final CardView passwordDialogCard = (CardView) PasswordDialogLayout.findViewById(R.id.password_dialog_card);
        final EditText editTextPassword = (EditText) PasswordDialogLayout.findViewById(R.id.password_edittxt);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText editTextConfirmPassword = (EditText) PasswordDialogLayout.findViewById(R.id.confirm_password_edittxt);
        final EditText securityAnswer1 = (EditText) PasswordDialogLayout.findViewById(R.id.security_answer_edittext);
        final EditText securityQuestion = (EditText) PasswordDialogLayout.findViewById(R.id.security_question_edittext);
        editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //empty method body
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextConfirmPassword.setSelection(editTextConfirmPassword.getText().toString().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 11) {
                    editTextConfirmPassword.setText(editable.toString().substring(0, 10));
                    editTextConfirmPassword.setSelection(10);
                    Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.max_password_length), Toast.LENGTH_SHORT).show();
                }
            }
        });
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //empty method body
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextPassword.setSelection(editTextPassword.getText().toString().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 11) {
                    editTextPassword.setText(editable.toString().substring(0, 10));
                    editTextPassword.setSelection(10);
                    Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.max_password_length), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        passwordDialogTitle.setText(R.string.change_password);
        passwordDialogTitle.setBackgroundColor(activity.getPrimaryColor());
        passwordDialogCard.setBackgroundColor(activity.getCardBackgroundColor());
        editTextPassword.getBackground().mutate().setColorFilter(activity.getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editTextPassword.setTextColor(activity.getTextColor());
        editTextPassword.setHintTextColor(activity.getSubTextColor());
        activity.setCursorDrawableColor(editTextPassword, activity.getTextColor());
        editTextConfirmPassword.getBackground().mutate().setColorFilter(activity.getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editTextConfirmPassword.setTextColor(activity.getTextColor());
        editTextConfirmPassword.setHintTextColor(activity.getSubTextColor());
        activity.setCursorDrawableColor(editTextConfirmPassword, activity.getTextColor());
        securityAnswer1.getBackground().mutate().setColorFilter(activity.getTextColor(), PorterDuff.Mode.SRC_ATOP);
        securityAnswer1.setTextColor(activity.getTextColor());
        securityAnswer1.setHintTextColor(activity.getSubTextColor());
        activity.setCursorDrawableColor(securityAnswer1, activity.getTextColor());
        securityQuestion.getBackground().mutate().setColorFilter(activity.getTextColor(), PorterDuff.Mode.SRC_ATOP);
        securityQuestion.setTextColor(activity.getTextColor());
        securityQuestion.setHintTextColor(activity.getSubTextColor());
        activity.setCursorDrawableColor(securityQuestion, activity.getTextColor());
        passwordDialog.setView(PasswordDialogLayout);

        AlertDialog dialogchange = passwordDialog.create();
        dialogchange.setCancelable(false);
        dialogchange.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing is done.
            }
        });
        dialogchange.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editTextPassword.length() > 3) {
                    if (editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
                        if (!editTextPassword.getText().toString().equals(SP.getString(activity.getString(R.string.preference_password_value), ""))) {
                            if(securityQuestion.getText().length() == 0) {
                                Toast.makeText(activity.getApplicationContext(), "Security question cannot be empty.", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            if (securityAnswer1.getText().length() == 0) {
                                Toast.makeText(activity.getApplicationContext(), "Security answer cannot be empty.", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                SP.putString(activity.getString(R.string.preference_password_value), editTextPassword.getText().toString());
                                SP.putString(activity.getString(R.string.security_question), securityQuestion.getText().toString());
                                SP.putString(activity.getString(R.string.security_answer), securityAnswer1.getText().toString());
                                SnackBarHandler.show(activity.findViewById(android.R.id.content), R.string.remember_password_message);
                                updateSecuritySetting();
                                Toast.makeText(activity.getApplicationContext(), "Password Reset", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else
                            SnackBarHandler.show(activity.findViewById(android.R.id.content), R.string.error_password_match);
                    } else
                        SnackBarHandler.show(activity.findViewById(android.R.id.content), R.string.password_dont_match);
                } else
                    SnackBarHandler.show(activity.findViewById(android.R.id.content), R.string.error_password_length);
            }
        });
        dialogchange.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogchange.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, activity.getAccentColor(), dialogchange);
    }

    public String[] getSecuredfolders() {
        return securedfolders;
    }

    public TextInputLayout getTextInputLayout() {
        return passwordTextInputLayout;
    }
}
