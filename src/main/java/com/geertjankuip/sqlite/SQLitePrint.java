package com.geertjankuip.sqlite;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLitePrint {

    static void print(ResultSet resultSet, int maxRows) {

        try{
            ResultSetMetaData metaData = resultSet.getMetaData();
            int nColumns = metaData.getColumnCount();

            ArrayList<String> types = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            ArrayList<Integer> positions = new ArrayList<>();

            StringBuilder strb = new StringBuilder();

            System.out.println("");

            for (int i=1 ; i<=nColumns; i++){

                String columnName = metaData.getColumnName(i);
                String columnTypeName = metaData.getColumnTypeName(i);
                names.add(columnName);
                types.add(columnTypeName);
                positions.add(strb.length());

                strb.append(columnName + "(");
                strb.append(columnTypeName +")" + "    ");  // 4 spaces inbetween
            }
            System.out.println(strb.toString());

            int count2=0;
            while (resultSet.next() && maxRows>count2){

                StringBuilder strb2 = new StringBuilder();

                for (int i=1 ; i<=nColumns; i++){
                    if ((types.get(i-1)).equals("INTEGER")) {
                        Integer r1 = resultSet.getInt(i);
                        strb2.append("  " + r1);
                    }else if ((types.get(i-1)).equals("TEXT")) {
                        String r2 = resultSet.getString(i);
                        int headerSize = types.get(i-1).length() + names.get(i-1).length();
                        if (r2.length() > (headerSize-3)) {
                            String truncated = r2.substring(0, headerSize -3);
                            r2 = truncated;
                        }
                        strb2.append("  " + r2);
                    }else if ((types.get(i-1)).equals("BOOLEAN")) {
                        boolean r3 = resultSet.getBoolean(i);
                        strb2.append("  " + r3);
                    }
                    if (i<nColumns) {
                        // align data with column headers
                        int len = strb2.length();
                        int pos = positions.get(i);
                        int diff = pos - len;
                        String str = " ";
                        String repeated = str.repeat(diff);
                        strb2.append(repeated);
                    }
                }

                System.out.println(strb2.toString());
                count2++;
            }
        }catch(SQLException e){

            System.out.println("Something went wrong, cannot print contents of ResultSet ");
        }
    }
}
