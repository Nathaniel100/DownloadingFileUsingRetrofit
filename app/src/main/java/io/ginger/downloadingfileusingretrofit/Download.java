package io.ginger.downloadingfileusingretrofit;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wufan on 2017/7/25.
 */

public class Download implements Parcelable {
  private int progress;
  private int currentFileSize;
  private int totalFileSize;

  public Download() {
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  public int getCurrentFileSize() {
    return currentFileSize;
  }

  public void setCurrentFileSize(int currentFileSize) {
    this.currentFileSize = currentFileSize;
  }

  public int getTotalFileSize() {
    return totalFileSize;
  }

  public void setTotalFileSize(int totalFileSize) {
    this.totalFileSize = totalFileSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Download download = (Download) o;

    if (progress != download.progress) return false;
    if (currentFileSize != download.currentFileSize) return false;
    return totalFileSize == download.totalFileSize;
  }

  @Override
  public int hashCode() {
    int result = progress;
    result = 31 * result + currentFileSize;
    result = 31 * result + totalFileSize;
    return result;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.progress);
    dest.writeInt(this.currentFileSize);
    dest.writeInt(this.totalFileSize);
  }

  protected Download(Parcel in) {
    this.progress = in.readInt();
    this.currentFileSize = in.readInt();
    this.totalFileSize = in.readInt();
  }

  public static final Parcelable.Creator<Download> CREATOR = new Parcelable.Creator<Download>() {
    @Override
    public Download createFromParcel(Parcel source) {
      return new Download(source);
    }

    @Override
    public Download[] newArray(int size) {
      return new Download[size];
    }
  };
}
