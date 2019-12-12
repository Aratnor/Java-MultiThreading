package server;

import model.Request;
import util.RequestHandler;

//Ana sunucuya istek gönderir
public class RequestGenerator extends Thread {
    Request request ;
    int maxReq ;
    public RequestGenerator(Request request,int maxReq) {
        this.request = request;
        this.maxReq = maxReq;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            RequestHandler.process(500);
            //Ana sunucuya 1-100 arası rastgele istek gönderir
            request.setRequest(RequestHandler.provideRequest(1,maxReq));
        }
    }

}
