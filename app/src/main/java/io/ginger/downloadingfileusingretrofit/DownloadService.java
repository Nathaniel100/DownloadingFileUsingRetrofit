package io.ginger.downloadingfileusingretrofit;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * Created by wufan on 2017/7/24.
 */

public interface DownloadService {
  @Streaming
  @GET("/test_100m.zip")
  Flowable<ResponseBody> downloadFile();
}
