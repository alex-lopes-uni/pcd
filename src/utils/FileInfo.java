package utils;

import java.net.Socket;
import java.util.List;

public record FileInfo(String filename, String fileHash, long fileSize, List<Socket> connections) {

    @Override
    public String toString() {
        return "FileInfo{filename='" + filename + "', fileHash='" + fileHash + "', fileSize=" + fileSize + "}";
    }
}
