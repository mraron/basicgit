package hu.mraron.basicgit;

import org.apache.commons.codec.digest.DigestUtils;

public class SHA1 {
    public static String hash(String data) {
        return DigestUtils.sha1Hex(data);
    }
}
