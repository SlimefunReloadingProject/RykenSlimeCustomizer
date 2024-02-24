package org.lins.mmmjjkx.rykenslimefuncustomizer.update;

import org.lins.mmmjjkx.rykenslimefuncustomizer.ProjectAddonManager;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("unused")
public class ZipUtils {
    public static void unzip(File zipFile, String desDirectory) throws IOException {
        File desDir = new File(ProjectAddonManager.ADDONS_DIRECTORY, desDirectory);
        if (!desDir.exists()) {
            boolean mkdirSuccess = desDir.mkdir();
            if (!mkdirSuccess) {
                throw new IOException("创建解压目标文件夹失败");
            }
        }

        if (!zipFile.exists()) {
            throw new FileNotFoundException("找不到压缩包");
        }

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
                mkdir(new File(unzipFilePath));
            } else {
                String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
                File file = new File(unzipFilePath);
                mkdir(file.getParentFile());
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(new FileOutputStream(unzipFilePath));
                byte[] bytes = new byte[1024];
                int readLen;
                while ((readLen = zipInputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, readLen);
                }
                bufferedOutputStream.close();
            }
            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    private static void mkdir(File file) {
        if (file == null || file.exists()) {
            return;
        }
        mkdir(file.getParentFile());
        file.mkdir();
    }
}
