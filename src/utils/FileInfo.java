package utils;

import java.net.Socket;
import java.util.List;

public record FileInfo(String fileName, String fileHash, long fileSize, List<Socket> connections) {

    @Override
    public String toString() {
        return "FileInfo{fileName='" + fileName + "', fileHash='" + fileHash + "', fileSize=" + fileSize + "}";
    }
}
