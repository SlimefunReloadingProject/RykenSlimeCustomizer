package org.lins.mmmjjkx.rykenslimefuncustomizer.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class ConcurrentDownloader {
    private static final int BUFFER_SIZE = 1024;
    private static final int TIMEOUT_MILLIS = 5 * 60 * 1000;

    public static final File downloadFolder =
            new File(RykenSlimefunCustomizer.INSTANCE.getDataFolder(), "temp-downloads");

    public static boolean downloadFile(String id, String fileUrl, String githubRel) {
        int numThreads = 2;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            File file = new File(downloadFolder, id + "-" + githubRel + ".zip");

            if (!downloadFolder.exists()) {
                downloadFolder.mkdirs();
            }

            if (file.exists()) {
                file.delete();
            }

            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(TIMEOUT_MILLIS));
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(TIMEOUT_MILLIS));

            int statusCode = connection.getResponseCode();

            if (statusCode != HttpURLConnection.HTTP_OK) {
                RykenSlimefunCustomizer.INSTANCE.getLogger().severe("无法检查附属 " + id + " 的更新");
                return false;
            }

            int fileLength = connection.getContentLength();
            int blockSize = fileLength / numThreads;

            Thread[] threads = new Thread[numThreads];
            for (int i = 0; i < numThreads; i++) {
                int startByte = i * blockSize;
                int endByte = (i == numThreads - 1) ? fileLength - 1 : (i + 1) * blockSize - 1;

                threads[i] = new DownloadThread(url, file, startByte, endByte);
                threads[i].start();
            }

            // ?
            boolean hasException = false;
            Exception e = new Exception();

            for (Thread thread : threads) {
                thread.join();

                DownloadThread t = (DownloadThread) thread;
                if (!hasException) {
                    hasException = t.hasException;
                    e = t.exception;
                }
            }

            if (hasException) {
                file.delete();
                RykenSlimefunCustomizer.INSTANCE.getLogger().log(Level.SEVERE, "无法下载附属 " + id + " 的更新", e);
                return false;
            }

            return true;
        } catch (Exception e) {
            RykenSlimefunCustomizer.INSTANCE.getLogger().log(Level.WARNING, "无法下载附属 " + id + "的更新", e);
            return false;
        }
    }

    private static class DownloadThread extends Thread {
        private static final int MAX_RETRIES = 3;
        private static final int RETRY_DELAY_MILLIS = 1000; // 1秒

        private final URL url;
        private final File file;
        private final int startByte;
        private final int endByte;

        private boolean hasException = false;
        private Exception exception;

        public DownloadThread(URL url, File file, int startByte, int endByte) {
            this.url = url;
            this.file = file;
            this.startByte = startByte;
            this.endByte = endByte;
        }

        @Override
        public void run() {
            int retries = 0;
            boolean success = false;

            while (retries < MAX_RETRIES && !success) {
                try {
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
                    InputStream inputStream = connection.getInputStream();
                    hasException = false;
                    FileOutputStream outputStream = new FileOutputStream(file, true);

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    inputStream.close();
                    outputStream.close();

                    success = true;
                } catch (Exception e) {
                    retries++;
                    hasException = true;
                    exception = e;
                    try {
                        Thread.sleep(RETRY_DELAY_MILLIS);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }
}
