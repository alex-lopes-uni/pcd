package utils.data_classes;

import app.Node;

import java.util.List;

// stores info after search necessary to download
public record FileInfo(String fileName, String fileHash, int fileSize, List<Node.NodeConnectionThread> nodeConnectionThreads) {

    @Override
    public String toString() {
        return "FileInfo: [fileName='" + fileName + "', fileHash='" + fileHash + "', fileSize=" + fileSize + "]";
    }
}
