package littleWhiteBear.SafeCheck.AntiCheck;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class CheckSignatrue {

    public static boolean check_sign_pass(Activity thisActivity, boolean isCheckPMProxy, boolean isCheckClass, boolean isFullToast, String sign_sha) {
        boolean isHasMtHook = false,
                isHasTaiChi = false,
                isHasBugXp = false,
                isHasMinusOne = false,
                isHasModexIndex = false,
                isHasArmOld = false,
                isHasArmEpic = false,
                isHasSigBypass = false,                
                isHasCheck = true;
        if (isCheckClass) {

            isHasMtHook = isHasClass("bin.mt.signature.KillerApplication");
            isHasTaiChi = isHasClass("me.weishu.exposed.ExposedApplication");
            isHasBugXp = isHasClass("com.supremacy.kctool.KCTool");
            isHasMinusOne = isHasClass("com.swift.sandhook.SandHook");
            isHasModexIndex = isHasClass("top.minusoneapp.HookApplication");
            isHasArmOld = isHasClass("arm.StubApp");
            isHasArmEpic = isHasClass("ؙؙؚؓؗؒؕؒ؜ؚؖؖؗؖؖؔؒؑؓؕؐؒؔ؜ؓؐؓ");
            isHasSigBypass = isHasClass("cn.lianquke.Hook.SigBypass");
            isHasCheck = isHasClass("littleWhiteBear.SafeCheck.MainActivity");

        }
        final boolean check_class = !isHasMtHook && !isHasTaiChi && !isHasBugXp && !isHasMinusOne && !isHasModexIndex && !isHasArmOld && !isHasArmEpic && !isHasSigBypass && isHasCheck;
        final String check_class_str = check_class ? "5bey6YCa6L+H" : "5LiN6YCa6L+H"; //已通过 - 不通过
        final String singInfoSHA1 = getSingInfoSHA1(thisActivity.getApplicationContext(), thisActivity.getPackageName());
        final boolean checkPMProxyResult = !isCheckPMProxy || checkPMProxy(thisActivity.getPackageManager());
        final boolean check_result = sign_sha.equals(singInfoSHA1) && checkPMProxyResult && check_class;
        final String titleStr = check_result ? "5bCP5LyZ5a2Q5L2g55qE5oOz5rOV5pyJ54K55Y2x6Zmp5ZGA8J+klA==" : "5bCP5LyZ5a2Q5L2g55qE5oOz5rOV5pyJ54K55Y2x6Zmp5ZGA8J+klA=="; // 签名检验通过 - 签名检验失败
        final String checkPMProxyStr = checkPMProxyResult ? "5bey6YCa6L+H" : "5LiN6YCa6L+H"; // 已通过 - 不通过
        final String toastStr = isFullToast ? String.format("%s%s%s%s%s%s%s%s%s%s%s",
                base64_decode_str(titleStr),
                "\n\n",
                base64_decode_str("6K6h566X562+5ZCNOg=="),//"计算签名:"
                "\n\n",
                singInfoSHA1,
                "\n\n",
                base64_decode_str("5Luj55CG5qOA5rWLOg=="), //"代理检测:"
                base64_decode_str(checkPMProxyStr),
                "\n\n",
                base64_decode_str("57G75ZCN5qOA5rWLOg=="), //"类名检测:"
                base64_decode_str(check_class_str)) : base64_decode_str(titleStr);
        if (isFullToast) {
            copyString(thisActivity.getApplicationContext(), toastStr);
        }
        if (check_result) {
            return true;
        } else {
            Toast.makeText(thisActivity.getApplicationContext(), toastStr, Toast.LENGTH_SHORT).show();
            thisActivity.moveTaskToBack(true);
            return false;
        }
    }

    //base64解码
    public static String base64_decode_str(String encode_str) {
        String s;
        try {
            s = new String(Base64.decode(encode_str.getBytes(), Base64.DEFAULT));
        } catch (Exception exception) {
            s = "";
        }
        return s;
    }

    //复制到剪切板
    public static void copyString(Context context, String copyStr) {
        try {
            if (copyStr == null || copyStr.length() < 1) {
                copyStr = "";
            }
            final ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            final ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            if (cm != null) {
                cm.setPrimaryClip(mClipData);
            }
        } catch (Exception ignored) {
        }
    }

    //判断是否存在某个类
    private static boolean isHasClass(String className) {
        boolean isExist = true;
        try {
            Class.forName(className);
        } catch (Exception ex) {
            isExist = false;
        }
        return isExist;
    }

    //检测 PM 代理
    private static boolean checkPMProxy(PackageManager packageManager) {
        return "android.content.pm.IPackageManager$Stub$Proxy".equals(nowPMName_str(packageManager));
    }

    private static String nowPMName_str(PackageManager packageManager) {
        String now_PM_Name = "";
        try {
            Field mPMField = packageManager.getClass().getDeclaredField("mPM");
            mPMField.setAccessible(true);
            Object mPM = mPMField.get(packageManager);
            // 取得类名
            assert mPM != null;
            now_PM_Name = mPM.getClass().getName();
        } catch (Exception ignored) {
        }
        // 类名改变说明被代理了
        return now_PM_Name;
    }

    //返回一个签名的对应类型的字符串
    public static String getSingInfoSHA1(Context context, String packageName) {
        String tmp = null;
        final Signature[] signs = getSignatures(context, packageName);
        for (Signature sig : Objects.requireNonNull(signs)) {
            tmp = getSignatureString(sig);
            break;
        }
        if (tmp == null) {
            tmp = "";
        }
        return tmp;
    }

    //返回对应包的签名信息
    @SuppressLint("PackageManagerGetSignatures")
    public static Signature[] getSignatures(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取相应的类型的字符串（把签名的byte[]信息转换成16进制）
    public static String getSignatureString(Signature sig) {
        final byte[] hexBytes = sig.toByteArray();
        String fingerprint = "error!";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] digestBytes = digest.digest(hexBytes);
            StringBuilder sb = new StringBuilder();
            for (byte digestByte : digestBytes) {
                sb.append((Integer.toHexString((digestByte & 0xFF) | 0x100)).substring(1, 3));
            }
            fingerprint = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return fingerprint;
    }
}