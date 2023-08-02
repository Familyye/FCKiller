package littleWhiteBear.SafeCheck;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String signatureExpected = "36f357767fcaf0787c0add0b96e235e5";
        String signatureFromAPI = md5(signatureFromAPI());
        String signatureFromAPK = md5(signatureFromAPK());
        String signatureFromSVC = md5(signatureFromSVC());


        boolean isSignatureValid = signatureExpected.equals(signatureFromAPI)
        && signatureExpected.equals(signatureFromAPK)
        && signatureExpected.equals(signatureFromSVC);

        if (!isSignatureValid) {
            MainActivity.this.moveTaskToBack(true);
            Toast.makeText(MainActivity.this, "小伙子你的想法有点危险呀😄", Toast.LENGTH_SHORT).show();
            return false;
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
            e.printStackTrace();
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
            e.printStackTrace();
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
