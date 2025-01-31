package main.java.com.geertjankuip.sqlite;

public class ClassContainer {

    int id;
    int token_id;
    int token_id_end;
    int token_dict_ref;
    int place;
    int line;
    int lineend;
    int file;
    String representation;
    boolean isinnerclass;

    public ClassContainer(int id, int token_id, int token_id_end, int token_dict_ref, int place, int line, int lineend, int file, String representation,boolean isinnerclass) {

        this.id = id;
        this.token_id = token_id;
        this.token_id_end = token_id_end;
        this.token_dict_ref = token_dict_ref;
        this.place = place;
        this.line = line;
        this.lineend = lineend;
        this.file = file;
        this.representation = representation;
        this.isinnerclass = isinnerclass;
    }
}
