package littleWhiteBear.SafeCheck;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import littleWhiteBear.SafeCheck.AntiCheck.CheckSignatrue;
import littleWhiteBear.SafeCheck.AntiCheck.ApkPathChecker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class MainActivity extends Activity {

    static {
        System.loadLibrary("SafeCheck");
    }
    private ImageView fishImageView;
    private TextView mkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView mkTextView = findViewById(R.id.mkTextView);
        mkTextView.setText("SafeCheck");        
        fishImageView = findViewById(R.id.fishImageView);  
        // 获取屏幕的高度
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // 计算小鱼的游动范围（从屏幕顶部到底部）
        float startTranslationY = 0;
        float endTranslationY = screenHeight - fishImageView.getHeight();

        // 创建垂直方向上的属性动画
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(fishImageView, "translationY", startTranslationY, endTranslationY);
        animatorY.setDuration(5000); // 动画持续时间（毫秒）
        animatorY.setInterpolator(new LinearInterpolator()); // 设置线性插值器，使得动画匀速播放
        animatorY.setRepeatCount(ObjectAnimator.INFINITE); // 设置动画重复次数（无限次）
        animatorY.setRepeatMode(ObjectAnimator.REVERSE); // 设置动画重复模式为反向播放
        animatorY.start(); // 启动动画              

        String signatureExpected = "36f357767fcaf0787c0add0b96e235e5";
        String signatureFromAPI = md5(signatureFromAPI());
        String signatureFromAPK = md5(signatureFromAPK());
        String signatureFromSVC = md5(signatureFromSVC());

        ApkPathChecker.checkApkPath(this); // Apk文件检测

        boolean isSignatureValid = signatureExpected.equals(signatureFromAPI)
                && signatureExpected.equals(signatureFromAPK)
                && signatureExpected.equals(signatureFromSVC);

        if (!isSignatureValid) {
            Toast.makeText(MainActivity.this, "小伙子你的想法有点危险呀😄", Toast.LENGTH_SHORT).show();
            throw new RuntimeException("Fuck You...");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApkPathChecker.checkApkPath(MainActivity.this);
        CheckSignatrue.check_sign_pass(MainActivity.this, true, true, false, "dcacc40ecd6f0dbf14ff67eea62b1a9d1739334d");

        String signatureExpected = "36f357767fcaf0787c0add0b96e235e5";
        String signatureFromAPI = md5(signatureFromAPI());
        String signatureFromAPK = md5(signatureFromAPK());
        String signatureFromSVC = md5(signatureFromSVC());

        boolean isSignatureValid = signatureExpected.equals(signatureFromAPI)
                && signatureExpected.equals(signatureFromAPK)
                && signatureExpected.equals(signatureFromSVC);

        if (!isSignatureValid) {
            Toast.makeText(MainActivity.this, "小伙子你的想法有点危险呀😄", Toast.LENGTH_SHORT).show();
            throw new RuntimeException("Fuck You...");
        }
    }

    private byte[] signatureFromAPI() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            return info.signatures[0].toByteArray();
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] signatureFromAPK() {
        try (ZipFile zipFile = new ZipFile(getPackageResourcePath())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.matches("(META-INF/.*)\\.(RSA|DSA|EC)")) {
                    try (InputStream is = zipFile.getInputStream(entry)) {
                        CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                        X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(is);
                        return x509Cert.getEncoded();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private byte[] signatureFromSVC() {
        try (ParcelFileDescriptor fd = ParcelFileDescriptor.adoptFd(openAt(getPackageResourcePath()));
             ZipInputStream zis = new ZipInputStream(new FileInputStream(fd.getFileDescriptor()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.matches("(META-INF/.*)\\.(RSA|DSA|EC)")) {
                    CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                    X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(zis);
                    return x509Cert.getEncoded();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String md5(byte[] bytes) {
        if (bytes == null) {
            return "null";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = digest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digestBytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean compareSignatures(String expected, String actual) {
        return expected.equals(actual);
    }

    private static native int openAt(String path);
}
