package com.tapjoy;

import com.alawar.mutant.jni.MutantMessages;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: classes.dex */
public class TapjoyUtil {
    private static final String TAPJOY_UTIL = "TapjoyUtil";

    public static String SHA256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        TapjoyLog.m190i(TAPJOY_UTIL, "SHA256: " + text);
        byte[] bArr = new byte[40];
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 15;
            int two_halfs = 0;
            while (true) {
                if (halfbyte >= 0 && halfbyte <= 9) {
                    buf.append((char) (halfbyte + 48));
                } else {
                    buf.append((char) ((halfbyte - 10) + 97));
                }
                halfbyte = data[i] & 15;
                int two_halfs2 = two_halfs + 1;
                if (two_halfs >= 1) {
                    break;
                }
                two_halfs = two_halfs2;
            }
        }
        return buf.toString();
    }

    public static Document buildDocument(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(is);
            return document;
        } catch (Exception e) {
            TapjoyLog.m189e(TAPJOY_UTIL, "buildDocument exception: " + e.toString());
            return null;
        }
    }

    public static String getNodeTrimValue(NodeList nodeList) {
        Element element = (Element) nodeList.item(0);
        String nodeValue = MutantMessages.sEmpty;
        if (element == null) {
            return null;
        }
        NodeList itemNodeList = element.getChildNodes();
        int length = itemNodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node node = itemNodeList.item(i);
            if (node != null) {
                nodeValue = nodeValue + node.getNodeValue();
            }
        }
        if (nodeValue == null || nodeValue.equals(MutantMessages.sEmpty)) {
            return null;
        }
        return nodeValue.trim();
    }

    public static void deleteFileOrDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] arr$ = fileOrDirectory.listFiles();
            for (File child : arr$) {
                deleteFileOrDirectory(child);
            }
        }
        TapjoyLog.m190i(TAPJOY_UTIL, "****************************************");
        TapjoyLog.m190i(TAPJOY_UTIL, "deleteFileOrDirectory: " + fileOrDirectory.getAbsolutePath());
        TapjoyLog.m190i(TAPJOY_UTIL, "****************************************");
        fileOrDirectory.delete();
    }
}
