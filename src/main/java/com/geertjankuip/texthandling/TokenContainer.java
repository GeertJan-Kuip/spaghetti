package com.geertjankuip.texthandling;


public class TokenContainer {

    int line;
    int pos;
    int color;
    String representation;

    public TokenContainer(int line, int pos, int color, String representation){

        this.line = line;
        this.pos=pos;
        this.color=color;
        this.representation = representation;


    }


}