package littleWhiteBear.SafeCheck.AntiCheck;

import android.app.Activity;
import android.content.Context;
import java.io.File;

public class ApkPathChecker {
    public static void checkApkPath(Context context) {
        String dataPath = context.getApplicationContext().getFilesDir().getAbsolutePath();
        File dataDir = new File(dataPath).getParentFile();
        if (dataDir.exists() && dataDir.isDirectory()) {
            checkFiles(context, dataDir);
        }
    }

    private static void checkFiles(Context context, File directory) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals("libmthook.so")
                            || file.getName().equals("origin.apk")
                            || file.getName().equals("littleWhiteBear.SecretKey.apk")
                            || file.getName().equals("libfuck.so")
                            || file.getName().equals("libarm.so")
                            || file.getName().equals("libarmsc.so")
                            || file.getName().equals("info")
                            || file.getName().equals("fancy.dat")
                            || file.getName().equals("libhidemaps.so")
                            || file.getName().equals("libFunJni.so")
                            || file.getName().equals("libFunDex.so")
                            || file.getName().equals("libFunELF.so")
                            || file.getName().equals("FunDexConfig")
                            || file.getName().equals("FunELFConfig")
                            || file.getName().equals("FunJniConfig")
                            || file.getName().equals("libblackdex.so")
                            || file.getName().equals("libblackdex_d.so")
                            || file.getName().equals("libjshook.so")
                            || file.getName().equals("libjshook")
                            || file.getName().equals("libjshook.config.so")
                            || file.getName().equals("libjshook.core.so")
                            || file.getName().equals("javatweak.dex")
                            || file.getName().equals("libsotweak.so")
                            || file.getName().equals("libIOHook.so")
                            || file.getName().equals("libmocls.so")
                            || file.getName().equals("libtarget.so")
                            || file.getName().equals("libsandhook.so")
                            || file.getName().equals("ccfcc9ff.jar")
                            || file.getName().equals("libnphook.so")) {
                        exitApplication(context);
                    }
                } else if (file.isDirectory()) {
                    if (file.getName().equals("lspatch")
                            || file.getName().equals("app_tweak")
                            || file.getName().equals("SFPatch")
                            || file.getName().equals("lianquke")
                            || file.getName().equals("virtual")
                            || file.getName().equals("sigdata")
                            || file.getName().equals("fv")
                            || file.getName().equals("jshook")
                            || file.getName().equals("FunELFCache")
                            || file.getName().equals("FunDexCache")
                            || file.getName().equals("FunJniCache")
                            || file.getName().equals("logs")
                            || file.getName().equals("App_dex")) {
                        exitApplication(context);   
                    }
                    checkFiles(context, file);
                }
            }
        }
    }

    private static void exitApplication(Context context) {
        if (context instanceof Activity) {
            ((Activity) context).finishAffinity();
            System.exit(0);
        }
    }
}
