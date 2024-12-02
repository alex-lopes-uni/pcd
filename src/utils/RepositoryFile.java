package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RepositoryFile {
    private final File file;
    private final String hash;

    public RepositoryFile(File file) throws NoSuchAlgorithmException, IOException {
        this.file = file;
        this.hash = setHash();
    }

    public File getFile() {
        return this.file;
    }

    public String getHash() {
        return this.hash;
    }

    public String setHash() throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] array = new byte[1024];
            int bytesRead;
            while ((bytesRead = stream.read(array)) != -1) {
                md.update(array, 0, bytesRead);
            }
        }
        byte[] hash = md.digest();

        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            String byteHex = Integer.toHexString(0xff & b);
            if (byteHex.length() == 1) {
                result.append('0');
            }
            result.append(byteHex);
        }
        return result.toString();
    }

    public byte[] getFileBlock(byte[] array, int offset, int lenght) {
        byte[] res = new byte[lenght];
        if (lenght - offset >= 0) System.arraycopy(array, offset, res, 0, lenght - offset);
        return res;
    }

    public byte[] getFileBinary() {
        int size = this.hash.length();
        byte[] result = new byte[size / 2];
        for (int i = 0; i < size; i += 2) {
            result[i / 2] = (byte) ((Character.digit(this.hash.charAt(i), 16) << 4)
                    + Character.digit(this.hash.charAt(i + 1), 16));
        }
        return result;
    }

    public String getFileName() {
        return this.file.getName();
    }

    @Override
    public String toString() {
        return this.file.getName()
                + ": [hash="
                + this.hash
                + "]";
    }

}
