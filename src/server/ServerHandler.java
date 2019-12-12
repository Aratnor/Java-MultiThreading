package server;

import model.Request;
import util.RequestHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
Tüm Sunucular burda kontrol edilir
 */
public class ServerHandler extends Thread {
    //Alt sunucuların ana sunucudan istekte bulunacağı üst sınır
    int subMainReq;

    //Ana sunucu isteklerin depolandığı yer
    Request mainServerRequestRepo = new Request(10000);

    //Ana sunucu icin istek üreten thread
    RequestGenerator requestGenerator;

    //Ana sunucu
    MainServer mainServer;

    //Sürekli calısmaya devam edecek sub thread
    SubServer coreSubServer1;

    //Sürekli calısmaya devam edecek sub thread
    SubServer coreSubServer2;

    //Gerektiginde oluşturulup yok edilecek sub threadler
    List<SubServer> tempSubServers;


    public ServerHandler(int maxMainReq,int subMainReq) {
        this.subMainReq = subMainReq;
        // main repoyu kullanacak threadler icin mainServerRequestRepo ile construct edilen objeler
        requestGenerator = new RequestGenerator(mainServerRequestRepo,maxMainReq);
        mainServer = new MainServer(mainServerRequestRepo);
        coreSubServer1 = new SubServer(mainServerRequestRepo,subMainReq,true);
        coreSubServer2 = new SubServer(mainServerRequestRepo,subMainReq,true);
        //gecici sub server listesi construct edilir
        tempSubServers = new ArrayList<>();
    }


    @Override
    public void run() {
        super.run();
        requestGenerator.start();
        mainServer.start();
        coreSubServer1.start();
        coreSubServer2.start();
        System.out.println("Sub server : " + coreSubServer1.getId() + " and " + coreSubServer2.getId() + " always running");
        while (true) {
            //Her 700 or 100(kararınıza bağlı) ms da bir sub threadlerin kapasitesi kontrol edilir
            RequestHandler.process(100);
            checkSubServers();
        }
    }

    //Sürekli calısmaya devam edecek sub thread kapasite kontrol methodu
    private void checkCoreSubServerCapacity() {
        //kapasitesi 70 den büyük olup olmadığı kontrol edilir
        if(coreSubServer1.getCapacity() >= 70) {
            checkCoreThreadCapacity(coreSubServer1);
        }
        if(coreSubServer2.getCapacity() >= 70) {
            checkCoreThreadCapacity(coreSubServer2);
        }
    }

    //Kapasitesi 70den büyük olan threadlerin kapasitesi yarıya düsürülüp yeni thread olusturulur.
    private void checkCoreThreadCapacity(SubServer subServer) {
        int currentCapacity = subServer.request.getRequests();
        int newCapacity = currentCapacity % 2 == 0 ? currentCapacity / 2 : (currentCapacity - 1) / 2;
        subServer.request.clear();
        subServer.request.setRequest(newCapacity);
        startNewSubServer(newCapacity);
    }

    //Tüm sub threadlerin kontrol edildiği metod
    private void checkSubServers(){
        checkCoreSubServerCapacity();
        checkCapacityOFSubServers();
    }

    //gecici sub threadlerin kapasite kontrolu
    private synchronized void checkCapacityOFSubServers() {
        synchronized (this){
            ArrayList<SubServer> copy = new ArrayList<>(tempSubServers);
            Iterator<SubServer> iterator = copy.iterator();
            ArrayList<SubServer> willBeDeleted = new ArrayList<>();
            while(iterator.hasNext() && !copy.isEmpty()) {
                SubServer server = iterator.next();
                float currentCapacity = server.getCapacity();
                if(!server.isAlive()){
                    willBeDeleted.add(server);
                    continue;
                }
                if(currentCapacity >= 70.0) {
                    int totalRequest = server.request.getRequests();
                    int newCapacity = totalRequest % 2 == 0 ? totalRequest / 2 : (totalRequest - 1) / 2;
                    server.request.clear();
                    server.request.setRequest(newCapacity);
                    startNewSubServer(newCapacity);
                } else if(currentCapacity == 0.0) {
                    //kapasitesi 0 a ulasan threadin calısması durdurulur ve  silinir
                    server.interrupt();
                    willBeDeleted.add(server);
                }
            }
            tempSubServers.removeAll(willBeDeleted);
        }
    }

    //Kapasitesi 70 ulasan threadlerin yeni thread baslatmasi icin kullanılan method
    private void startNewSubServer(int requestNumber) {
        SubServer subServer = new SubServer(mainServerRequestRepo,subMainReq,false);
        subServer.setRequest(requestNumber);
        subServer.start();
        tempSubServers.add(subServer);
    }


}

