package server;

import model.Request;
import util.RequestHandler;

public class SubServer extends Thread {
    //Ana sunucu Request objesine referans(point) eder
    Request serverRequest;

    boolean isCore = false;

    //ana sunucudan bulunacak isteğin üst sınırı
    int maxReq;

    //Sub Serverin kendi istek deposu
    Request request = new Request(5000);

    //Ekrana yazdırma sayacı
    int count = 0;

    //Depo üst sınırı
    private final int MAX_REQUEST = 5000;

    public SubServer(Request request,int maxReq,boolean isCore) {
        this.maxReq = maxReq;
        this.serverRequest = request;
        this.isCore = isCore;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted() || isCore) {
            if(!isCore && getCapacity() == 0.0){
                interrupt();
                System.out.println("------ \tDeleting server : " + currentThread().getId());
                continue;

            }
            count++;
            RequestHandler.process(100);
            if(count % 3 == 0){
                processRequest();
            }
            if(count % 5 == 0){
                getRequestFromMain();
            }
            if(count == 30) {
                count = 0;
                System.out.println(
                        "Capacity of SubServer " + currentThread().getId() + " : %" +
                                getCapacity()
                );
            }
        }

    }

    //Threading kapasitesini hesaplar
    public float getCapacity() {
        return RequestHandler.calculateRequestPercentage(request.getRequests(),MAX_REQUEST);
    }

    //tüm istekleri silip verilen istek miktarını üstüne yazar
    public void resetAndSetRequest(int newRequest) {
        request.clear();
        request.setRequest(newRequest);
    }

    // yeni isteği depoya ekler
    public void setRequest(int newRequest) {
        request.setRequest(newRequest);
    }

    //300ms aralıkla rastgele 1-50 isteğe cevap verir
    private void processRequest() {
        int processRequestNum = RequestHandler.provideRequest(1,50);
        request.deleteRequest(processRequestNum);
    }

    //500ms(200ms+300ms) aralıkla ana sunucudan 1-50 rastgele istek alır
    private void getRequestFromMain() {
        int requestFromServer = RequestHandler.provideRequest(1,maxReq);
        int isHaveRequest = serverRequest.deleteRequest(requestFromServer);
        request.setRequest(isHaveRequest);
    }
}

