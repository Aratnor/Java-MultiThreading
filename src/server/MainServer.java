package server;


import model.Request;
import util.RequestHandler;

//Ana Sunucu
public class MainServer extends Thread {
    //Ana sunucunun tuttuğu istekler
    Request request;

    //ekrana kapasite yazdırmak icin kullanılan sayac
    int count = 0;

    //Sunucunun maximum istek kapasitesi
    private final int MAX_REQUEST = 10000;

    public MainServer(Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        super.run();
        while (true){
            processRequests();
            count++;
            printCapacityOfServer();
        }
    }

    //belli aralıklarla ekrana sunucunun kapasitesinin yazdırır
    private void printCapacityOfServer() {
        if(count == 10) {
            count = 0;
            System.out.println(
                    "Capacity of main server : %" +
                            RequestHandler.calculateRequestPercentage(request.getRequests(),MAX_REQUEST));
        }
    }

    //sunucu 200ms aralıklarla isteklere cevap verir
    public void processRequests() {
        int processRequestNum = RequestHandler.provideRequest(1,50);
        request.deleteRequest(processRequestNum);
        RequestHandler.process(200);
    }
}

