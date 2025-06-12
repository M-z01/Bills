package com.extract.bills.bill;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;

public class PasswordReader {
    public PasswordReader(){}

    public static String getPassword(){
        String filePath = "password.txt";
        
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            if(!lines.isEmpty()) {
                String password = lines.get(0);
                return password;        
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    } 

}
