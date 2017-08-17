package io.ginger.downloadingfileusingretrofit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by wufan on 2017/7/25.
 */

public class DownloadIntentService extends IntentService {
  private static final String TAG = DownloadIntentService.class.getSimpleName();

  private static final File DOWNLOAD_FILE = new File(
      Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "testfile");

  private NotificationCompat.Builder notificationBuilder;
  private NotificationManager notificationManager;
  private int totalFileSize;

  public DownloadIntentService() {
    super("Download intent service");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    Log.d(TAG, "onHandleIntent");

    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Download")
        .setContentText("Downloading File")
        .setAutoCancel(true);
    notificationManager.notify(0, notificationBuilder.build());

    initDownload();
  }

  private void initDownload() {
    Log.d(TAG, "initDownload");
    DownloadService service = DownloadServiceProvider.instance().provideDownloadService();
    service.downloadFile()
        .map(responseBody -> writeFile(responseBody, DOWNLOAD_FILE))
        .subscribeOn(Schedulers.io())
        .subscribe(code -> Log.d(TAG, "Download success"),
            e -> Log.e(TAG, e.getLocalizedMessage()));
  }

  private int writeFile(ResponseBody responseBody, File file) throws IOException {
    long fileSize = responseBody.contentLength();
    BufferedSink sink = null;
    long total = 0;
    int timeCount = 1;
    try {
      sink = Okio.buffer(Okio.sink(file));
      long count;
      long startTime = System.currentTimeMillis();
      while ((count = responseBody.source().read(sink.buffer(), 1024 * 4)) > 0) {
        total += count;
        totalFileSize = (int) (1.0 * fileSize / 1024);
        double current = Math.round(1.0 * total / 1024);

        int progress = (int) ((total * 100) / fileSize);
        long currentTime = System.currentTimeMillis() - startTime;
        Download download = new Download();
        download.setTotalFileSize(totalFileSize);
        if (currentTime > 1000 * timeCount) {
          download.setCurrentFileSize((int) current);
          download.setProgress(progress);
          sendNotification(download);
          timeCount++;
        }
      }
      sink.flush();
      onDownloadComplete();
      return 0;
    } finally {
      if (sink != null) {
        sink.close();
      }
    }
  }

  private void sendNotification(Download download) {
    sendIntent(download);
    notificationBuilder.setProgress(100, download.getProgress(), false);
    notificationBuilder.setContentText(
        "Downloading file " + download.getCurrentFileSize() + "/" + totalFileSize + " KB");
    notificationManager.notify(0, notificationBuilder.build());
  }

  private void sendIntent(Download download) {
    Intent intent = new Intent(MainActivity.MESSAGE_PROGRESS);
    intent.putExtra("download", download);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }

  private void onDownloadComplete() {
    Download download = new Download();
    download.setProgress(100);
    sendIntent(download);

    notificationManager.cancel(0);
    notificationBuilder.setProgress(0, 0, false);
    notificationBuilder.setContentText("File Downloaded");
    notificationManager.notify(0, notificationBuilder.build());
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    notificationManager.cancel(0);
  }
}
