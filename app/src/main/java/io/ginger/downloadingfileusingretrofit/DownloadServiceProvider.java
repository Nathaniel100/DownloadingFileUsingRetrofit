package io.ginger.downloadingfileusingretrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by wufan on 2017/7/24.
 */

public class DownloadServiceProvider {
  private static final String BASE_URL = "http://tpdb.speed2.hinet.net/";
  private static DownloadServiceProvider instance;

  private DownloadService service;

  private DownloadServiceProvider() {
  }

  public static DownloadServiceProvider instance() {
    if (instance == null) {
      instance = new DownloadServiceProvider();
    }
    return instance;
  }

  public DownloadService provideDownloadService() {
    if (service == null) {
      service = new Retrofit.Builder().baseUrl(BASE_URL)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(getOkHttpClient())
          .build()
          .create(DownloadService.class);
    }
    return service;
  }

  private OkHttpClient getOkHttpClient() {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

    return new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();
  }
}
