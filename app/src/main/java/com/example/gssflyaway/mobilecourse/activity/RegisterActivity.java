package com.example.gssflyaway.mobilecourse.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.gssflyaway.mobilecourse.login_and_register.*;

import com.example.gssflyaway.mobilecourse.R;

public class RegisterActivity extends AppCompatActivity {

    //已注册用户，测试用
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "zcc", "gss","wm","myp","zy"
    };
    //用户登录或注册任务
    private UserRegisterTask mAuthTask = null;
    //验证码数组
    private int [] checkNum =null;

    //界面元素
    private EditText mAccountView;
    private EditText mPhoneView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mCaptchaView;
    private View mProgressView;
    private View mRegisterFormView;
    private CheckView mCheckView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupToolbar();

        mAccountView = (EditText) findViewById(R.id.register_layout_account);
        mPhoneView=(EditText) findViewById(R.id.register_layout_phonenumber);
        mEmailView=(AutoCompleteTextView) findViewById(R.id.register_layout_email);
        mPasswordView = (EditText)findViewById(R.id.register_layout_password);
        mCaptchaView=(EditText)findViewById(R.id.input_captcha);

        //生产验证码
        mCheckView=(CheckView) findViewById(R.id.captcha);
        checkNum = CheckUtil.getCheckNum();
        mCheckView.setCheckNum(checkNum);
        mCheckView.invaliChenkNum();
        mCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNum = CheckUtil.getCheckNum();
                mCheckView.setCheckNum(checkNum);
                mCheckView.invaliChenkNum();
            }
        });


        //点击注册按钮
        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //注册账户
                register();

                //若注册失败需要更新验证码
                checkNum = CheckUtil.getCheckNum();
                mCheckView.setCheckNum(checkNum);
                mCheckView.invaliChenkNum();

            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if(ab!=null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void register() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAccountView.setError(null);
        mPhoneView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mCaptchaView.setError(null);

        // Store values at the time of the login attempt.
        String account = mAccountView.getText().toString();
        String phone =mPhoneView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String capt = mCaptchaView.getText().toString();


        // 检查账户
        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.account_input_null));
            mAccountView.requestFocus();
            return;
        }

        //检查手机号
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.phone_input_null));
            mPhoneView.requestFocus();
            return;
        }else if(phone.length()!=11)
        {
            mPhoneView.setError(getString(R.string.phone_input_error));
            mPhoneView.requestFocus();
            return;
        }

        //检查邮箱
        if(TextUtils.isEmpty(email)){
            mEmailView.setError(getString(R.string.email_input_null));
            mEmailView.requestFocus();
            return;

        }else if(!email.matches("^[a-zA-Z0-9_]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$"))
        {
            mEmailView.setError(getString(R.string.email_input_error));
            mEmailView.requestFocus();
            return;
        }

        //检查密码
        if(password.length()<8)
        {
            mPasswordView.setError(getString(R.string.psw_input_invaid));
            mPasswordView.requestFocus();
            return;
        }

        //检查验证码是否正确
        if(!CheckUtil.checkNum(capt,checkNum))
        {
            mCaptchaView.setError(getString(R.string.captcha_error));
            mCaptchaView.requestFocus();
            return;

        }

        //输入检查完毕，进行注册操作
        showProgress(true);
        mAuthTask = new UserRegisterTask(account, password);
        mAuthTask.execute((Void) null);

    }

    private boolean IsAccountExist(String account)
    {
        return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     登陆验证类
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mAccount;
        private final String mPassword;

        UserRegisterTask(String account, String password) {
            mAccount = account;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {

                if (credential.equals(mAccount)) {
                    // Account exists, return true if the password matches.
                    return false;
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mAccountView.setError(getString(R.string.account_input_exist));
                mAccountView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

