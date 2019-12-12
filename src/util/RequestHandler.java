package util;

public class RequestHandler {

    //verilen min,max arası rastgele istek döndürür
    public static int provideRequest(int min, int max) {
        return (int)((max - min + 1) * Math.random() + min);
    }

    //sunucunun isteklerini cevaplamak icin gereken süre threadi bekleten metod
    public static void process(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("Current thread interrupted");
        }
    }

    //Kapasitenin ne kadar dolu olduğunu döndürür
    public static float calculateRequestPercentage(int request,int max) {
        return ((float)request* 100)/max ;
    }
}
