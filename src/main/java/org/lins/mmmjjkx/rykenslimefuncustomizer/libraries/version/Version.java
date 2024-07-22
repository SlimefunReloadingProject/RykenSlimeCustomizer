package org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.Bukkit;

public enum Version {
    v1_7_R1,
    v1_7_R2,
    v1_7_R3,
    v1_7_R4,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_13_R3,
    v1_14_R1,
    v1_14_R2,
    v1_15_R1,
    v1_15_R2,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3(4),
    v1_20_R4(5, 6),
    v1_21_R1(0),
    v1_21_R2,
    v1_21_R3,
    v1_22_R1,
    v1_22_R2,
    v1_22_R3,
    v1_23_R1,
    v1_23_R2,
    v1_23_R3;

    private static int subVersion = 0;
    private static Version current = null;

    static {
        getCurrent();
    }

    @Getter
    private Integer value;

    private int[] minorVersions = null;

    @Getter
    private final String shortVersion;

    Version(int... versions) {
        this();
        minorVersions = versions;
    }

    Version() {
        try {
            this.value = Integer.valueOf(this.name().replaceAll("[^\\d.]", ""));
        } catch (Exception ignored) {
        }
        shortVersion = this.name().substring(0, this.name().length() - 3);
    }

    public static Version getCurrent() {
        if (current != null) return current;
        // Paper returns examples as of 1.20
        //        Bukkit.getServer().getClass().getPackage().getName();   org.bukkit.craftbukkit
        //        Bukkit.getBukkitVersion();                              1.21-R0.1-SNAPSHOT
        //        Bukkit.getMinecraftVersion();                           1.21
        //        Bukkit.getVersion();                                    1.21-4-090775e (MC: 1.21)
        //        Bukkit.getVersionMessage();                             This server is running Paper version
        // 1.21-4-master@090775e (2024-06-18T13:42:35Z) (Implementing API version 1.21-R0.1-SNAPSHOT)

        String[] v = Bukkit.getServer().getClass().getPackage().getName().split("\\.");

        try {
            String vr = Bukkit.getBukkitVersion().split("-", 2)[0];
            String[] split = vr.split("\\.");
            if (split.length <= 2) subVersion = 0;
            else {
                subVersion = Integer.parseInt(split[2]);
            }
        } catch (Throwable ignored) {
        }

        String vv = v[v.length - 1];
        for (Version one : values()) {
            if (one.name().equalsIgnoreCase(vv)) {
                current = one;
                break;
            }
        }

        if (current != null) return current;

        String ve = Bukkit.getBukkitVersion().split("-", 2)[0];
        main:
        for (Version one : values()) {
            if (one.name().equalsIgnoreCase(ve)) {
                current = one;
                break;
            }
            List<String> cleanVersion = one.getMinorVersions();
            for (String cv : cleanVersion) {
                if (ve.equalsIgnoreCase(cv)) {
                    current = one;
                    break main;
                }
            }
        }

        if (current != null) return current;

        main:
        for (int i = 1; i < 10; i++) {
            try {
                Class.forName("org.bukkit.craftbukkit.v" + ve.replace(".", "_") + "_R" + i + ".entity.CraftPlayer");
                for (Version one : values()) {
                    if (one.name().equalsIgnoreCase("v" + ve.replace(".", "_") + "_R" + i)) {
                        current = one;
                        break main;
                    }
                }

                break;
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (current != null) return current;

        for (Version one : values()) {
            if (ve.startsWith(one.getSimplifiedVersion())
                    || ve.startsWith(one.getSimplifiedVersion()
                            .substring(0, one.getSimplifiedVersion().length() - 1))) {
                current = one;
                break;
            }
        }

        return current;
    }

    public static boolean isCurrentEqualOrHigher(Version v) {
        return getCurrent().getValue() >= v.getValue();
    }

    public static boolean isCurrentHigher(Version v) {
        return getCurrent().getValue() > v.getValue();
    }

    public static boolean isCurrentLower(Version v) {
        return getCurrent().getValue() < v.getValue();
    }

    public static boolean isCurrentEqualOrLower(Version v) {
        return getCurrent().getValue() <= v.getValue();
    }

    public static boolean isCurrentEqual(Version v) {
        return getCurrent().getValue() == v.getValue();
    }

    public static boolean isCurrentSubEqualOrHigher(int subVersion) {
        return Version.subVersion >= subVersion;
    }

    public static boolean isCurrentSubHigher(int subVersion) {
        return Version.subVersion > subVersion;
    }

    public static boolean isCurrentSubLower(int subVersion) {
        return Version.subVersion < subVersion;
    }

    public static boolean isCurrentSubEqualOrLower(int subVersion) {
        return Version.subVersion <= subVersion;
    }

    public static boolean isCurrentSubEqual(int subVersion) {
        return Version.subVersion == subVersion;
    }

    public static Integer convertVersion(String v) {
        v = v.replaceAll("[^\\d.]", "");
        Integer version = 0;
        if (v.contains(".")) {
            String lVersion = "";
            for (String one : v.split("\\.")) {
                String s = one;
                if (s.length() == 1) s = "0" + s;
                lVersion += s;
            }

            try {
                version = Integer.parseInt(lVersion);
            } catch (Exception e) {
            }
        } else {
            try {
                version = Integer.parseInt(v);
            } catch (Exception e) {
            }
        }
        return version;
    }

    public static String deconvertVersion(Integer v) {

        StringBuilder version = new StringBuilder();

        String vs = String.valueOf(v);

        while (vs.length() > 0) {
            int subv = 0;
            try {
                if (vs.length() > 2) {
                    subv = Integer.parseInt(vs.substring(vs.length() - 2));
                    version.insert(0, "." + subv);
                } else {
                    subv = Integer.parseInt(vs);
                    version.insert(0, subv);
                }
            } catch (Throwable e) {

            }
            if (vs.length() > 2) vs = vs.substring(0, vs.length() - 2);
            else break;
        }

        return version.toString();
    }

    public String getShortFormated() {
        return shortVersion.replace("v", "").replace("_", ".") + ".x";
    }

    public String getFormated() {
        return shortVersion.replace("v", "").replace("_", ".") + "." + subVersion;
    }

    public boolean isLower(Version version) {
        return getValue() < version.getValue();
    }

    public boolean isHigher(Version version) {
        return getValue() > version.getValue();
    }

    public boolean isEqualOrLower(Version version) {
        return getValue() <= version.getValue();
    }

    public boolean isEqualOrHigher(Version version) {
        return getValue() >= version.getValue();
    }

    private String getSimplifiedVersion() {
        return this.name().substring(1).replace("_", ".").split("R", 2)[0];
    }

    public List<String> getMinorVersions() {

        if (minorVersions == null) return new ArrayList<>();

        return Arrays.stream(minorVersions)
                .mapToObj(version -> getSimplifiedVersion() + version)
                .collect(Collectors.toList());
    }
}
