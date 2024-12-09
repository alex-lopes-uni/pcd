package utils.data_classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Data class to store a file together with the necessary functions to hash it and turn it into binary code
public class RepositoryFile {
    private final File file;
    private final String hash;
    private final int size;

    public RepositoryFile(File file) {
        this.file = file;
        this.hash = setHash();
        this.size = getFileBinary().length;
    }

    // Method to calculate the SHA-256 hash of the file
    public String setHash() {
        try {
            // Create a SHA-256 MessageDigest instance
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Open file for reading
            try (FileInputStream stream = new FileInputStream(file)) {
                byte[] array = new byte[1024];
                int bytesRead;
                // Read the file in chunks and update the digest
                while ((bytesRead = stream.read(array)) != -1) {
                    md.update(array, 0, bytesRead);
                }
            }
            // Get the final hash value
            byte[] hash = md.digest();

            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                String byteHex = Integer.toHexString(0xff & b);
                if (byteHex.length() == 1) {
                    result.append('0');
                }
                result.append(byteHex);
            }
            // Return the hex string as the hash
            return result.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Set Hash: [exception: " + e.getClass().getName() + ", error: " + e.getMessage() + "]");
            return null;
        }
    }

    // Returns a block from the array
    public byte[] getFileBlock(int offset, int length) {
        // Get the full file's binary data
        byte[] array = getFileBinary();
        // Create a byte array to store the block
        byte[] res = new byte[length];
        // Copy the block from the full binary data
        if (length - offset >= 0) System.arraycopy(array, offset, res, 0, length - offset);
        return res;
    }

    // Creates the full binary array to separate into blocks by function above to send
    public byte[] getFileBinary() {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.err.println(this.getClass() + ": [" + "getFileBinary()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            return null;
        }
    }

    // Method to get the size of the file in byte
    public int getSize() {
        return this.size;
    }

    // Method to get the hash of the file
    public String getHash() {
        return this.hash;
    }

    // Method to get the file's name (for GUI display)
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
