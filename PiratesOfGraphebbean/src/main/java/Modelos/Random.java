package Modelos;

public class Random {
    public static int randomInt(int min, int max){
        return (min + (int)(Math.random() * ((max - min) + 1)));
    }

    public static boolean randomBoolean(){
        if(Math.random() > 0.5){
            return true;
        } else {
            return false;
        }
    }

    public static boolean probabilidad(int prob){
        if (randomInt(0,100) < prob){
            return true;
        } else {
            return false;
        }
    }
}
