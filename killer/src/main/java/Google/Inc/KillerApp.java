package Google.Inc;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class KillerApp extends Application {
    public static final String URL = "https://bbs.binmt.cc/home.php?mod=space&uid=105927&do=profile&from=space";

    static {
        String packageName = "Google.Inc";
        String signatureData = "MIIDpjCCAo6gAwIBAgIESShVoDANBgkqhkiG9w0BAQ0FADBsMQswCQYDVQQGEwJVUzELMAkGA1UE\n" +
        "CBMCTVQxFjAUBgNVBAcTDU5ldyBZb3JrIENpdHkxCTAHBgNVBAoTADESMBAGA1UECxMJQ29kaW5n\n" +
        "R2F5MRkwFwYDVQQDDBDwnZeg8J2XtvCdl7nwnZe4MCAXDTk5MDUxOTIxMjAzMloYDzIwOTkwNDI0\n" +
        "MjEyMDMyWjBsMQswCQYDVQQGEwJVUzELMAkGA1UECBMCTVQxFjAUBgNVBAcTDU5ldyBZb3JrIENp\n" +
        "dHkxCTAHBgNVBAoTADESMBAGA1UECxMJQ29kaW5nR2F5MRkwFwYDVQQDDBDwnZeg8J2XtvCdl7nw\n" +
        "nZe4MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA07WkeRWjlHPBcWi9W12NyAHrGArt\n" +
        "1rdKqS/6j2+CEfHyzJ6Ig6Qgy8Kc/8R756jlIdW/EJs1HX2aksvciJNDUURPfpPxjv98QUal0viL\n" +
        "zWsGFTrDjy50f2/en4Ss+MH3hTKWbTmW3dVvZWDtzg6dfQXTbavOPUjOsO3lyEyFp7B7qdytbFFM\n" +
        "EGi3fH3dEFV9nHKluELwem3jOQ7z/HTiuhr2Hen/gbIcjw40XP7G8hLwB5UZc2CmoPdOj8JxzTb0\n" +
        "2ws4yD1K1WR2c0fk+Xvb27iVP4cwd/n963z78CUwJk+AMAGpp0EbjKViSW5MVbSkil/QqSD/HXUZ\n" +
        "WRXmC91TcwIDAQABo04wTDArBgNVHRAEJDAigA8xOTk5MDUxOTIxMjAzMlqBDzIwOTkwNDI0MjEy\n" +
        "MDMyWjAdBgNVHQ4EFgQUUKGUMSUuinDPU9AFqCZxDcC03hEwDQYJKoZIhvcNAQENBQADggEBAJPU\n" +
        "fWsjeWlBCd6s4eMDP8ofBZ9e/pPEs3sjO42r1JwfmCU9kwVrC2u54V4YPpj6jP7HM04o3AU8XsH3\n" +
        "AeT5WiNoCjockcJfgFb4W2mI8eLqR2RoOfTOtkC06aE7vzcskhZAndrpp4sxYIM+cECuoYFxgl5N\n" +
        "RJhhQ+p0dUYhYls658C9jGtM7oFlsFRShaWuoZDSFhiRdS0sexq6ojSd9Iv4mjEyyfL3Hd23i5HH\n" +
        "DizZ4X2biuJGrH6iGIW7oNOADbCqk7eUUUJgvgySqZ1E+kMxRQQLr+Q0yGFknE0Ho9f7/V7Ilvsf\n" +
        "9Gcc02wofOToDBPhhH7o2yvCcIPS5zap534=\n";
        killPM(packageName, signatureData);
        killOpen(packageName);
    }

    private static void killPM(String packageName, String signatureData) {
        Signature fakeSignature = new Signature(Base64.decode(signatureData, Base64.DEFAULT));
        Parcelable.Creator<PackageInfo> originalCreator = PackageInfo.CREATOR;
        Parcelable.Creator<PackageInfo> creator = new Parcelable.Creator<PackageInfo>() {
            @Override
            public PackageInfo createFromParcel(Parcel source) {
                PackageInfo packageInfo = originalCreator.createFromParcel(source);
                if (packageInfo.packageName.equals(packageName)) {
                    if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                        packageInfo.signatures[0] = fakeSignature;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (packageInfo.signingInfo != null) {
                            Signature[] signaturesArray = packageInfo.signingInfo.getApkContentsSigners();
                            if (signaturesArray != null && signaturesArray.length > 0) {
                                signaturesArray[0] = fakeSignature;
                            }
                        }
                    }
                }
                return packageInfo;
            }

            @Override
            public PackageInfo[] newArray(int size) {
                return originalCreator.newArray(size);
            }
        };
        try {
            findField(PackageInfo.class, "CREATOR").set(null, creator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("Landroid/os/Parcel;", "Landroid/content/pm", "Landroid/app");
        }
        try {
            Object cache = findField(PackageManager.class, "sPackageInfoCache").get(null);
            cache.getClass().getMethod("clear").invoke(cache);
        } catch (Throwable ignored) {
        }
        try {
            Map<?, ?> mCreators = (Map<?, ?>) findField(Parcel.class, "mCreators").get(null);
            mCreators.clear();
        } catch (Throwable ignored) {
        }
        try {
            Map<?, ?> sPairedCreators = (Map<?, ?>) findField(Parcel.class, "sPairedCreators").get(null);
            sPairedCreators.clear();
        } catch (Throwable ignored) {
        }
    }

    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            while (true) {
                clazz = clazz.getSuperclass();
                if (clazz == null || clazz.equals(Object.class)) {
                    break;
                }
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                } catch (NoSuchFieldException ignored) {
                }
            }
            throw e;
        }
    }

    private static void killOpen(String packageName) {
        try {
            System.loadLibrary("AppKiller");
        } catch (Throwable e) {
            System.err.println("Load AppKiller library failed");
            return;
        }
        String apkPath = getApkPath(packageName);
        if (apkPath == null) {
            System.err.println("Get apk path failed");
            return;
        }
        File apkFile = new File(apkPath);
        File repFile = new File(getDataFile(packageName), "IORedirect.apk");
        try (ZipFile zipFile = new ZipFile(apkFile)) {
            String name = "assets/FCKiller/IORedirect.apk";
            ZipEntry entry = zipFile.getEntry(name);
            if (entry == null) {
                System.err.println("Entry not found: " + name);
                return;
            }
            if (!repFile.exists() || repFile.length() != entry.getSize()) {
                try (InputStream is = zipFile.getInputStream(entry); OutputStream os = new FileOutputStream(repFile)) {
                    byte[] buf = new byte[102400];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        os.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IORedirectApkPath(apkFile.getAbsolutePath(), repFile.getAbsolutePath());
    }

    @SuppressLint("SdCardPath")
    private static File getDataFile(String packageName) {
        String username = Environment.getExternalStorageDirectory().getName();
        if (username.matches("\\d+")) {
            File file = new File("/data/user/" + username + "/" + packageName);
            if (file.canWrite()) {
                return file;
            }
        }
        return new File("/data/data/" + packageName);
    }

    private static String getApkPath(String packageName) {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/self/maps"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split("\\s+");
                String path = arr[arr.length - 1];
                if (isApkPath(packageName, path)) {
                    return path;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isApkPath(String packageName, String path) {
        if (!path.startsWith("/") || !path.endsWith(".apk")) {
            return false;
        }
        String[] splitStr = path.substring(1).split("/", 6);
        int splitCount = splitStr.length;
        if (splitCount == 4 || splitCount == 5) {
            if (splitStr[0].equals("data") && splitStr[1].equals("app") && splitStr[splitCount - 1].equals("base.apk")) {
                return splitStr[splitCount - 2].startsWith(packageName);
            }
            if (splitStr[0].equals("mnt") && splitStr[1].equals("asec") && splitStr[splitCount - 1].equals("pkg.apk")) {
                return splitStr[splitCount - 2].startsWith(packageName);
            }
        } else if (splitCount == 3) {
            if (splitStr[0].equals("data") && splitStr[1].equals("app")) {
                return splitStr[2].startsWith(packageName);
            }
        } else if (splitCount == 6) {
            if (splitStr[0].equals("mnt") && splitStr[1].equals("expand") && splitStr[3].equals("app") && splitStr[5].equals("base.apk")) {
                return splitStr[4].endsWith(packageName);
            }
        }
        return false;
    }

    private static native void IORedirectApkPath(String apkPath, String repPath);
}
