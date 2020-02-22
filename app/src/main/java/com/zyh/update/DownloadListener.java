package com.zyh.update;

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
}
