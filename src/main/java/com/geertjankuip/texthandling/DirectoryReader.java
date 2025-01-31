package com.geertjankuip.texthandling;

import com.geertjankuip.logging.ActivityLogger;
import com.geertjankuip.logging.MyLogger;
//import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryReader {

    private ArrayList<FileDataContainer> files = new ArrayList<>();

    private ActivityLogger logger;

    public DirectoryReader(ActivityLogger logger) {

        this.logger = logger;
    }

    public boolean getValidFolderAndStartProcessingIt(){
        File selectedDirectory = selectDirectory();
        if (selectedDirectory != null) {
            readDirectory(selectedDirectory);
            return true;
        } else {
            return false;
        }

    }

    public ArrayList<FileDataContainer> getFileArray() {

        return files;
    }

    private File selectDirectory() {

        File selectedDirectory;

        JFileChooser fileChooser = new JFileChooser(new File("C:/Kuips files/temp/javaprojectscanner - temp2/"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = fileChooser.getSelectedFile();
            logger.logAction("Succesfully selected a folder.");
        } else {
            logger.logAction("No folder selected.");
            selectedDirectory = null;
        }
        return selectedDirectory;
    }

    private void readDirectory(File directoryPath) {

        List<Path> walkResultList = null;

        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath.getPath()))) {

            walkResultList = paths.collect(Collectors.toList());
            logger.logAction("Successfully collected all folder- and filenames");
        } catch (IOException e) {
            logger.logAction("Something went wrong when iterating the files and folders");
            e.printStackTrace();
        }

        int counter = 1;
        HashMap<File, Integer> myHashMap = new HashMap<File, Integer>();

        if (walkResultList!=null) {
            for (Path i : walkResultList) {

                File fileObject = i.toFile();
                boolean isFile = fileObject.isFile();
                String name = fileObject.getName();

                FileDataContainer fdc = new FileDataContainer(counter, isFile, name, fileObject, 0, true);
                files.add(fdc);
                myHashMap.put(fileObject, counter);
                counter++;
            }
        }
        for (FileDataContainer i : files) {

            File parentAsFile = i.fileObject.getParentFile();
            Integer newParentId = myHashMap.get(parentAsFile);
            if (newParentId == null) {
                i.parent = -1;
            } else {
                i.parent = newParentId;
            }
        }
        logNumberOfFoldersAndFiles();
    }

    private void logNumberOfFoldersAndFiles() {

        int countFolders = 0;
        int countFiles = 0;
        for (FileDataContainer fdc : files) {

            if (fdc.isFile == true) {
                countFiles++;
            } else {
                countFolders++;
            }
        }
        logger.logAction("Number of folders: " + countFolders);
        logger.logAction("Number of files: " + countFiles);
    }

}
