package main.java.com.geertjankuip.texthandling;

import main.java.com.geertjankuip.logging.ActivityLogger;

import java.util.ArrayList;


public class TextHandler {

    ArrayList<TextDataContainer> allTextPositionsFilenames = new ArrayList<>();
    ArrayList<FileDataContainer> files;

    ActivityLogger logger;

    public TextHandler(ArrayList<FileDataContainer> files, ActivityLogger logger) {

        this.files = files;
        this.logger = logger;
        processAllFiles();
    }

    public ArrayList<TextDataContainer> getAllData() {

        return(allTextPositionsFilenames);
    }

    private void processAllFiles() {

        for (FileDataContainer item : files) {

            String fullName = item.getFileObject().getPath();
            String lastFive = fullName.substring(fullName.length()-5, fullName.length());

            if (lastFive.equals(".java")) {

                TextDataContainer textDataContainer = processSingleFile((String) item.getFileObject().getPath());
                allTextPositionsFilenames.add(textDataContainer);
            }
        }
    }

    private TextDataContainer processSingleFile(String path){

        TextFileReader textFileReader = new TextFileReader(path, logger);

        TextDataContainer singleFileData = textFileReader.getAsTextDataContainer();

        return singleFileData;
    }
}
