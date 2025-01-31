package main.java.com.geertjankuip.texthandling;

import java.util.ArrayList;


public class TextDataContainer {

    public ArrayList<String> lines;
    public ArrayList<ArrayList<Integer>> positions;
    public String path;

    public TextDataContainer(ArrayList<String> lines, ArrayList<ArrayList<Integer>> positions, String path){
        this.lines = lines;
        this.positions = positions;
        this.path = path;
    }

    public ArrayList<String> getLines(){

        return (lines);
    }
    public ArrayList<ArrayList<Integer>> getPositions(){

        return (positions);
    }
    public String getPath(){

        return (path);
    }
}
