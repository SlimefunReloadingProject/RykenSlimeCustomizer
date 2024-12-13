package org.lins.mmmjjkx.rykenslimefuncustomizer.update;

import com.google.gson.Gson;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.ProjectAddonManager;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class GithubUpdater {
    public static final File downloadFolder =
            new File(RykenSlimefunCustomizer.INSTANCE.getDataFolder(), "temp-downloads");

    public static boolean checkAndUpdate(
            @NotNull String currentVer,
            @NotNull String author,
            @NotNull String repo,
            @NotNull String prjId,
            @NotNull String folderName) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = "https://api.github.com/repos/" + author + "/" + repo + "/releases/latest";
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            String entity = EntityUtils.toString(response.getEntity());

            GitHubRelease release = new Gson().fromJson(entity, GitHubRelease.class);

            String releaseName = release.getName();

            if (releaseName == null) {
                RykenSlimefunCustomizer.INSTANCE
                        .getLogger()
                        .log(Level.WARNING, "无法检查附属 " + prjId + "的更新: 已到达GitHub API速率限制(60请求/h)");
                return false;
            }

            if (releaseName.startsWith("v") && !currentVer.startsWith("v")) {
                releaseName = releaseName.replaceFirst("v", "");
            }

            if (!Objects.equals(currentVer, releaseName)) {
                if (release.isPrerelease()
                        && !RykenSlimefunCustomizer.INSTANCE.getConfig().getBoolean("update.pre-releases", false)) {
                    return false;
                }

                if (!downloadFolder.exists()) {
                    downloadFolder.mkdirs();
                }

                File zip = new File(downloadFolder, prjId + "-" + releaseName + ".zip");

                String zipUrl;
                List<GitHubRelease.Asset> assets = release.getAssets();
                if (assets == null || assets.isEmpty()) {
                    zipUrl = release.getZipball_url();
                } else {
                    ProjectAddon prj = RykenSlimefunCustomizer.addonManager.get(prjId);
                    if (prj == null) {
                        zipUrl = release.getZipball_url();
                    } else {
                        GitHubRelease.Asset asset = assets.stream()
                                .filter(z -> z.getName().equalsIgnoreCase(prj.getDownloadZipName()))
                                .findFirst()
                                .orElse(null);
                        if (asset == null) {
                            zipUrl = release.getZipball_url();
                        } else {
                            zipUrl = asset.getBrowser_download_url();
                        }
                    }
                }

                URL urlObj = new URL(zipUrl);

                if (!zip.exists()) {
                    if (!zip.createNewFile()) {
                        throw new IOException("创建下载文件失败");
                    }
                }

                long result = Files.copy(urlObj.openStream(), zip.toPath(), StandardCopyOption.REPLACE_EXISTING);

                if (result < 1) {
                    return false;
                }

                if (zip.exists()) {
                    File projectFolder = new File(ProjectAddonManager.ADDONS_DIRECTORY, folderName);

                    if (!projectFolder.exists()) {
                        mkdir(projectFolder);
                    }

                    unzip(zip, projectFolder);

                    File info = new File(projectFolder, "info.yml");
                    YamlConfiguration infoYml = YamlConfiguration.loadConfiguration(info);
                    String id = infoYml.getString("id", "");

                    if (!id.equals(prjId)) {
                        ExceptionHandler.info(
                                "&a成功更新附属 " + prjId + "!" + "\n" + "&b注意：原先的附属ID为 &e" + prjId + " &b现在已变更为 &d" + id);
                    } else {
                        ExceptionHandler.info("&a成功更新附属 " + prjId + "!");
                    }

                    return true;
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            RykenSlimefunCustomizer.INSTANCE.getLogger().log(Level.WARNING, "无法更新附属 " + prjId, e);
            return false;
        }
    }

    public static void unzip(File zipFile, File desDirectory) throws IOException {
        if (!desDirectory.exists()) {
            boolean mkdirSuccess = desDirectory.mkdirs();
            if (!mkdirSuccess) {
                throw new IOException("创建解压目标文件夹失败");
            }
        }

        if (!zipFile.exists()) {
            throw new FileNotFoundException("找不到压缩包");
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    String entryName = zipEntry.getName();

                    int firstSlashIndex = entryName.indexOf('/');
                    if (firstSlashIndex != -1) {
                        entryName = entryName.substring(firstSlashIndex + 1);
                    }
                    File outFile = new File(desDirectory, entryName);
                    mkdir(outFile.getParentFile());

                    try (BufferedOutputStream bufferedOutputStream =
                            new BufferedOutputStream(new FileOutputStream(outFile))) {
                        byte[] bytes = new byte[1024];
                        int readLen;
                        long totalBytesRead = 0;
                        while ((readLen = zipInputStream.read(bytes)) != -1) {
                            bufferedOutputStream.write(bytes, 0, readLen);
                            totalBytesRead += readLen;
                        }

                        if (zipEntry.getSize() != -1 && totalBytesRead != zipEntry.getSize()) {
                            throw new IOException("读取的字节数与条目大小不一致");
                        }
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        }
    }

    private static void mkdir(File file) {
        if (file == null || file.exists()) {
            return;
        }
        mkdir(file.getParentFile());
        file.mkdirs();
    }
}
