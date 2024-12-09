package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// data class to store a file and it's hash together
public class RepositoryFile {
    private final File file;
    private final String hash;
    private final int size;

    public RepositoryFile(File file) {
        this.file = file;
        this.hash = setHash();
        this.size = getFileBinary().length;
    }

    //file calculating algorithm
    public String setHash() {
        try {
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
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Set Hash: [exception: " + e.getClass().getName() + ", error: " + e.getMessage() + "]");
        }
        return null;
    }

    // returns a block from the array
    public byte[] getFileBlock(int offset, int length) {
        byte[] array = getFileBinary();
        byte[] res = new byte[length];
        if (length - offset >= 0) System.arraycopy(array, offset, res, 0, length - offset);
        return res;
    }

    // creates the full binary array to separate into blocks by function above to send
    public byte[] getFileBinary() {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.err.println("Get File Binary: [exception: " + e.getClass().getName() + ", error: " + e.getMessage() + "]");
            return null;
        }
    }

    public int getSize() {
        return this.size;
    }

    public String getHash() {
        return this.hash;
    }

    // to display file name in GUI
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
