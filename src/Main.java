import server.ServerHandler;

import java.util.Scanner;

public class Main {
    public static void main(String [] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Ana sunucuya rastgele gelecek istekler için üst sinir giriniz(varsayılan : 100,önerilen değer : 500)  :");
        int maxMainReq = in.nextInt();
        System.out.println("Alt sunucunun ana sunucudan alacagi istekler için üst sinir giriniz(varsayılan : 50,önerilen değer : 300) :");
        int subMainReq = in.nextInt();
        System.out.println("max :" + maxMainReq + " sub :" + subMainReq);
        ServerHandler handler = new ServerHandler(maxMainReq,subMainReq);
        handler.start();
    }
}
