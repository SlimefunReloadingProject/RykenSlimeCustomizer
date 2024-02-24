package org.lins.mmmjjkx.rykenslimefuncustomizer.update;

import lombok.Getter;

import java.util.List;

@Getter
public class GitHubRelease {
    private String url;
    private String html_url;
    private String assets_url;
    private String upload_url;
    private String tarball_url;
    private String zipball_url;
    private int id;
    private String node_id;
    private String tag_name;
    private String target_commitish;
    private String name;
    private String body;
    private boolean draft;
    private boolean prerelease;
    private String created_at;
    private String published_at;
    private Author author;
    private List<Asset> assets;

    @Getter
    public static class Author {
        private String login;
        private int id;
        private String node_id;
        private String avatar_url;
        private String gravatar_id;
        private String url;
        private String html_url;
        private String followers_url;
        private String following_url;
        private String gists_url;
        private String starred_url;
        private String subscriptions_url;
        private String organizations_url;
        private String repos_url;
        private String events_url;
        private String received_events_url;
        private String type;
        private boolean site_admin;
    }

    @Getter
    public static class Asset {
        private String url;
        private String browser_download_url;
        private int id;
        private String node_id;
        private String name;
        private String label;
        private String state;
        private String content_type;
        private int size;
        private int download_count;
        private String created_at;
        private String updated_at;
        private Author uploader;
    }
}
