package utils;

import app.Node;

import java.util.List;

// stores info after search necessary to download
public record FileInfo(String fileName, String fileHash, long fileSize, List<Node.NodeConnectionThread> nodeConnectionThreads) {

    @Override
    public String toString() {
        return "FileInfo: [fileName='" + fileName + "', fileHash='" + fileHash + "', fileSize=" + fileSize + "]";
    }
}
