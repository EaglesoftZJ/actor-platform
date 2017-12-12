package im.actor.sdk.controllers.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import im.actor.sdk.ActorSDK;
import im.actor.sdk.ActorStyle;
import im.actor.sdk.R;
import im.actor.sdk.util.Fonts;
import im.actor.sdk.util.KeyboardHelper;
import im.actor.sdk.view.SelectorFactory;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public class SignInForOAFragment extends BaseAuthFragment {

    private EditText signIdEditText;
    private KeyboardHelper keyboardHelper;
    SharedPreferences spIp, spUswer, spLogin;
    private TextView login_set_ip_text;

    public SignInForOAFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_phone_oa, container, false);
        getSupportActionBar().hide();

//        TextView buttonCotinueText = (TextView) v.findViewById(R.id.button_continue_text);
        StateListDrawable states = SelectorFactory.get(ActorSDK.sharedActor().style.getMainColor(), getActivity());
//        buttonCotinueText.setBackgroundDrawable(states);
//        buttonCotinueText.setTypeface(Fonts.medium());
//        buttonCotinueText.setTextColor(ActorSDK.sharedActor().style.getTextPrimaryInvColor());

        keyboardHelper = new KeyboardHelper(getActivity());

        v.findViewById(R.id.divider).setBackgroundColor(style.getDividerColor());


        spUswer = ((AuthActivity) getActivity()).getSharedPreferences("userList", Context.MODE_PRIVATE);
        spIp = ((AuthActivity) getActivity()).getSharedPreferences("ipLogin", Context.MODE_PRIVATE);
        spLogin = ((AuthActivity) getActivity()).getSharedPreferences("userLogin", Context.MODE_PRIVATE);

        initView(v);

//        Get domain logo

//        logoActor = ActorSystem.system().actorOf(Props.create(LogoActor.class, new ActorCreator<LogoActor>() {
//            @Override
//            public LogoActor create() {
//                return new LogoActor();
//            }
//        }), "actor/logo_actor");
//
//        logoActor.send(new LogoActor.AddCallback(new LogoActor.LogoCallBack() {
//            @Override
//            public void onDownloaded(final Drawable logoDrawable) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (logoDrawable != null) {
//                            logo.setImageDrawable(logoDrawable);
//                            logo.measure(0, 0);
//                            expand(logo, logo.getMeasuredHeight());
//                        } else {
//                            expand(logo, 0);
//                        }
//                    }
//                });
//            }
//        }));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO track sign_in auth open
        //messenger().trackAuthPhoneOpen();

        setTitle(R.string.sign_in_title);
//        if (savedAuthId == null || savedAuthId.length() == 0) {
//            focussignId(signIdEditText);
//        } else {
//            focussignId(signPwdEditText);
//        }


//        keyboardHelper.setImeVisibility(signIdEditText, true);
    }

    EditText signPwdEditText;
    String savedAuthId;
    CheckBox reMM;

    private void initView(View v) {
        ActorStyle style = ActorSDK.sharedActor().style;

        RelativeLayout login_bottom_lay = (RelativeLayout) v.findViewById(R.id.login_bottom_lay);
        Drawable bottomBg = getDrawable("loginPage_bg_bottom");
        if (bottomBg != null) {
            login_bottom_lay.setBackgroundDrawable(bottomBg);
        }

        ImageView login_logo = (ImageView) v.findViewById(R.id.login_logo);
        Drawable logoImage = getDrawable("login_logo");
        if (logoImage != null) {
            login_logo.setBackgroundDrawable(logoImage);
        }
        RelativeLayout login_bg_top = (RelativeLayout) v.findViewById(R.id.login_bg_top);
        Drawable topBg = getDrawable("loginPage_bg_top");
        if (topBg != null) {
            login_bg_top.setBackgroundDrawable(topBg);
        }

        signIdEditText = (EditText) v.findViewById(R.id.tv_sign_in);
        signIdEditText.setTextColor(style.getTextPrimaryColor());
        signIdEditText.setHighlightColor(style.getMainColor());

        reMM = (CheckBox) v.findViewById(R.id.reMM);
        reMM.setChecked(spLogin.getBoolean("isMemory", true));
        signPwdEditText = (EditText) v.findViewById(R.id.et_sms_pwd_enter);


        signPwdEditText.setTextColor(style.getTextPrimaryColor());
        signPwdEditText.setHighlightColor(style.getMainColor());
        signPwdEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_GO) {
                    requestCode();

//                    InputMethodManager imm = (InputMethodManager) textView
//                            .getContext().getSystemService(
//                                    Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    keyboardHelper.setImeVisibility(textView, false);

                    return true;
                }
                return false;
            }
        });
        if (reMM.isChecked()) {
            signIdEditText.setText(spLogin.getString("zh", ""));
            signPwdEditText.setText(spLogin.getString("mm", ""));
            savedAuthId = spLogin.getString("zh", "");
            signIdEditText.setSelection(signIdEditText.getText().length());

        } else {
            savedAuthId = "";
        }

        login_set_ip_text = (TextView) v.findViewById(R.id.login_set_ip_text);
        login_set_ip_text.setText(spIp.getString("urlForCompany", ""));
        int availableAuthType = ActorSDK.sharedActor().getAuthType();
//        savedAuthId = messenger().getPreferences().getString("sign_in_auth_id");
//        signIdEditText.setText(savedAuthId);
        boolean needSuggested = savedAuthId == null || savedAuthId.isEmpty();
        if (((availableAuthType & AuthActivity.AUTH_TYPE_PHONE) == AuthActivity.AUTH_TYPE_PHONE) && ((availableAuthType & AuthActivity.AUTH_TYPE_EMAIL) == AuthActivity.AUTH_TYPE_EMAIL)) {
            //both hints set phone + email by default
            if (needSuggested) {
                setSuggestedEmail(signIdEditText);
            }
        } else if ((availableAuthType & AuthActivity.AUTH_TYPE_PHONE) == AuthActivity.AUTH_TYPE_PHONE) {
            signIdEditText.setHint(getString(R.string.sign_in_edit_text_hint_phone_only));
            signIdEditText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        } else if ((availableAuthType & AuthActivity.AUTH_TYPE_EMAIL) == AuthActivity.AUTH_TYPE_EMAIL) {
            signIdEditText.setHint(getString(R.string.sign_in_edit_text_hint_email_only));
            if (needSuggested) {
                setSuggestedEmail(signIdEditText);
            }
        }


        Button singUp = (Button) v.findViewById(R.id.button_sign_up);
        Drawable butBg = getDrawable("loginPage_but");
        if (butBg != null) {
            singUp.setBackgroundDrawable(butBg);
        }
//        singUp.setTextColor(style.getTextSecondaryColor());
        onClick(singUp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCode();
//                InputMethodManager imm = (InputMethodManager) v
//                        .getContext().getSystemService(
//                                Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                keyboardHelper.setImeVisibility(v, false);
            }
        });

//        onClick(v, R.id.button_continue, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                requestCode();
////                InputMethodManager imm = (InputMethodManager) view
////                        .getContext().getSystemService(
////                                Context.INPUT_METHOD_SERVICE);
////                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            }
//        });


        //这是我的根布局
        RelativeLayout mRootView = (RelativeLayout) v.findViewById(R.id.login_bottom_lay);
        //获取屏幕高度
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int screenHeight = metrics.heightPixels;
        final int buttonHeigjt = singUp.getHeight();
        final int reyHeight = mRootView.getLayoutParams().height;

        //因为系统没有直接监听软键盘API，所以就用以下方法
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() { //当界面大小变化时，系统就会调用该方法
                        Rect r = new Rect(); //该对象代表一个矩形（rectangle）
                        mRootView.getWindowVisibleDisplayFrame(r); //将当前界面的尺寸传给Rect矩形
                        int deltaHeight = screenHeight - r.bottom;  //弹起键盘时的变化高度，在该场景下其实就是键盘高度。
                        if (deltaHeight > 150) {  //该值是随便写的，认为界面高度变化超过150px就视为键盘弹起。
                            Drawable butBg = getDrawable("loginPage_bg_bottom_small");
                            if (butBg != null) {
                                singUp.setBackgroundDrawable(butBg);
                            } else {
                                mRootView.setBackgroundResource(R.drawable.logo_bg_but);
                            }

                        } else {
                            Drawable butBg = getDrawable("loginPage_but");
                            if (butBg != null) {
                                singUp.setBackgroundDrawable(butBg);
                            } else {
                                mRootView.setBackgroundResource(R.drawable.logo_bg_2);
                            }

                        }

                    }
                });

    }

    private void requestCode() {
        String message = getString(R.string.auth_error_wrong_auth_id);
        if (signIdEditText.getText().toString().trim().length() == 0) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(message)
                    .setPositiveButton(R.string.dialog_ok, null)
                    .show();
            return;
        }

        String rawId = signIdEditText.getText().toString().trim();
        if (rawId.length() == 0) {
            Toast.makeText(getActivity(), R.string.sign_in_nickname_hint, Toast.LENGTH_SHORT).show();
        }

        String pwd = signPwdEditText.getText().toString().trim();
        if (pwd.length() == 0)
            Toast.makeText(getActivity(), R.string.auth_password_init, Toast.LENGTH_SHORT).show();

        if (rawId.contains("@")) {
            startEmailAuth(rawId);
        } else {
            try {
//                isNeedSignUp(rawId);
                spLogin.edit().putBoolean("isMemory", reMM.isChecked());
                startNickNameAuth(rawId, pwd);
//                startPhoneAuth(Long.parseLong(rawId.replace("+", "")));
            } catch (Exception e) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton(R.string.dialog_ok, null)
                        .show();
                return;
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.sign_in, menu);
        MenuItem itemSignUp = menu.findItem(R.id.sign_up);
        itemSignUp.setVisible(false);

        MenuItem item = menu.findItem(R.id.change_endpoint);
        if (item != null) {
            item.setVisible(ActorSDK.sharedActor().isUseAlternateEndpointsEnabled());
        }
    }


    private void focussignId(EditText editText) {
        focus(editText);
    }


    private Drawable getDrawable(String imageName) {
        SharedPreferences sp = getActivity().getSharedPreferences("flyChatSp", Context.MODE_PRIVATE);
        String Page_bg = sp.getString(imageName, null);

        if (Page_bg != null) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(Page_bg);
            if (imageBitmap != null) {
                return new BitmapDrawable(imageBitmap);
            }
        }
        return null;
    }

}
