package smartfoodcluster.feedme.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import smartfoodcluster.feedme.R;
import smartfoodcluster.feedme.handlers.AbstractGetNameTask;
import smartfoodcluster.feedme.handlers.GetNameInForeground;
import smartfoodcluster.feedme.qrcode.QRCodeScanner;
import smartfoodcluster.feedme.restaurant.RestaurantHome;
//import smartfoodcluster.feedme.restaurant.RestaurantHomeNew;
import smartfoodcluster.feedme.user.UserHome;
import smartfoodcluster.feedme.util.Constants;

//import com.appspot.myapplicationid.restaurantEndpoint.model.Restaurant;
//import appcloud.controller.EndpointsAsyncTask;

/**
 * A login screen that offers login via Google account
 */
public class LoginActivity extends AppCompatActivity {

    Context mContext = LoginActivity.this;
    AccountManager mAccountManager;
    String token;
    int serverCode;
    public static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constants.adminMode) {
            Intent i = new Intent(getApplicationContext(), QRCodeScanner.class);
            startActivity(i);
            setContentView(R.layout.activity_qrcode_scanner);
        }

        setContentView(R.layout.activity_login);
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        final boolean userAuthenticated = settings.getBoolean(Constants.userAuthenticated, false);
        if (userAuthenticated) {
            boolean userRole = settings.getBoolean(Constants.userRole, Constants.UserRole.USER);
            navigateToNextPage(userRole);
            return;
        }
        // end points call
        //new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Srinivas"));

        SignInButton mEmailSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup userRoleGroup = (RadioGroup) findViewById(R.id.userRole);
                if (userRoleGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(LoginActivity.this, "Please select a role for yourself before authenticating. This can be changed later!!", Toast.LENGTH_LONG).show();
                    return;
                }
                boolean userRole = Constants.UserRole.USER;
                if (findViewById(userRoleGroup.getCheckedRadioButtonId()).getId() == R.id.owner) {
                    userRole = Constants.UserRole.OWNER;
                } else {
                    userRole = Constants.UserRole.USER;
                }
                SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                settings.edit().putBoolean(Constants.userRole, userRole).commit();
                settings.edit().putBoolean(Constants.userAuthenticated, true).commit();


                Log.e("Auth / user role ", ">>" + userRole);
                boolean authSuccess = true;
                if (Constants.authEnabled) authSuccess = syncGoogleAccount();
                if (authSuccess) {
                    navigateToNextPage(userRole);
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                    }
                    Toast.makeText(LoginActivity.this, "No google account sync!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToNextPage(boolean userRole) {
        // Register user in cloud database.
        // Inside your Activity class onCreate method
        SharedPreferences settings = getSharedPreferences(
                getString(R.string.app_name), 0);
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(this,
                "server:client_id:1-web-app.apps.googleusercontent.com");

        if (userRole == Constants.UserRole.USER) {
            //Intent i = new Intent(getApplicationContext(), UserPayment.class);
            Intent i = new Intent(getApplicationContext(), UserHome.class);
            i.putExtra(Constants.showSuccess, true);
            startActivity(i);
            setContentView(R.layout.activity_user_home);
            //setContentView(R.layout.activity_user_payment);
        } else {
            Intent i = new Intent(getApplicationContext(), RestaurantHome.class);
            i.putExtra(Constants.showSuccess, true);
            startActivity(i);
            setContentView(R.layout.activity_restaurant_home);
        }
    }

    private String[] getAccountNames() {
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            names[i] = accounts[i].name;
        }
        return names;
    }

    private AbstractGetNameTask getTask(LoginActivity activity, String email, String scope) {
        return new GetNameInForeground(activity, email, scope);
    }

    public boolean syncGoogleAccount() {
        if (isNetworkAvailable()) {
            String[] accountarrs = getAccountNames();
            if (accountarrs.length > 0) {
                getTask(LoginActivity.this, accountarrs[0], SCOPE).execute();
                Log.e("Authenticated", "Done");
                return true;
            } else {
                Toast.makeText(LoginActivity.this, "No google account sync!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "No network available!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    boolean isNetworkAvailable() {
        ConnectivityManager cmgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cmgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e("Network testing", "##Hurray!! Available");
            Log.e("Network testing", "##Hurray!! Available");
            return true;
        } else {
            Log.e("Network testing", "##Not Available");
            return false;
        }
    }
}

