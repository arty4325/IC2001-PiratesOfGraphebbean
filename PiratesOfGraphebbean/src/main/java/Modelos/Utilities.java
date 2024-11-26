package Modelos;

public class Utilities {
    public static String formatearEnTimer(int num){
        if(num<10){
            return "0" + num;
        } else{
            return Integer.toString(num);
        }
    }
}
