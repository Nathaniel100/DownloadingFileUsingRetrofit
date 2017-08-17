package io.ginger.downloadingfileusingretrofit;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
  public static final String MESSAGE_PROGRESS = "MESSAGE_PROGRESS";

  private static final int RC_PERMISSION = 1;
  private static final String TAG = MainActivity.class.getSimpleName();

  @BindView(R.id.container)
  View container;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.progressBar)
  ProgressBar progressBar;

  @BindView(R.id.progressText)
  TextView progressText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);

    registerReceiver();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver();
  }

  @OnClick(R.id.fab)
  public void downloadFile() {
    if (checkPermission()) {
      Log.d(TAG, "Start download");
      startDownload();
    } else {
      Log.d(TAG, "No permission");
      requestPermission();
    }
  }

  private void registerReceiver() {
    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(MESSAGE_PROGRESS);
    localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
  }

  private void unregisterReceiver() {
    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
    localBroadcastManager.unregisterReceiver(broadcastReceiver);
  }

  private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(MESSAGE_PROGRESS)) {
        Download download = intent.getParcelableExtra("download");
        progressBar.setProgress(download.getProgress());
        if (download.getProgress() == 100) {
          progressText.setText("File Download Complete");
        } else {
          progressText.setText(
              String.format(Locale.CHINA, "Downloaded (%d/%d) KB", download.getCurrentFileSize(),
                  download.getTotalFileSize()));
        }
      }
    }
  };

  public void startDownload() {
    Intent intent = new Intent(this, DownloadIntentService.class);
    startService(intent);
  }

  private void showToast(String message) {
    Snackbar.make(container, message, Snackbar.LENGTH_LONG).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private boolean checkPermission() {
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
    return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED;
  }

  private void requestPermission() {
    ActivityCompat.requestPermissions(this,
        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, RC_PERMISSION);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case RC_PERMISSION:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          startDownload();
        } else {
          showToast("Permission Denied, Please allow to proceed!");
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }
}
