package com.geertjankuip.texthandling;

import java.io.File;

public class FileDataContainer {


    Integer id;
    Boolean isFile;
    String name;
    File fileObject;
    Integer parent;
    Boolean isProcessed;

    public FileDataContainer(Integer id, Boolean isFile, String name, File fileObject, Integer parent, Boolean isProcessed) {
        this.id = id;
        this.isFile = isFile;
        this.name = name;
        this.fileObject = fileObject;
        this.parent = parent;
        this.isProcessed = isProcessed;
    }

    public int getID(){

        return(this.id);
    }
    public boolean getIsFile(){

        return(this.isFile);
    }
    public String getName(){

        return(this.name);
    }
    public File getFileObject(){

        return(this.fileObject);
    }
    public int getParent(){

        return(this.parent);
    }
    public boolean getIsProcessed(){

        return(this.isProcessed);
    }



}
