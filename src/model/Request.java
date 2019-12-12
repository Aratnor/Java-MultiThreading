package model;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
Multithreading icin kullanılacak,
Thread safe istek sınıfı
İlham alınan github repo:https://github.com/batux/java.concurrency.examples/tree/master/java.concurrency.examples/src/com/concurrency/readwritelock/example
 */
public class Request {
    //istek miktarı
    private volatile int request;

    //ana sunucu istek kapasitesi
    private  int MAX_REQUEST = 10000;

    /*
    Birden fazla thread bu sınıfa erişmek istediğinde read,write talimatları icin
    thread safe yapı oluşturan lock objesi.
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public Request(int MAX_REQUEST) {
        request = 0;
        this.MAX_REQUEST = MAX_REQUEST;
    }

    public void setRequest(int newRequest) {
        /*
        Thread safe yapısını oluşturmak icin bir Thread bu methodu
        veya write(requesting değerini değiştirme) talimatını calıstırdığında
        başka thread erişemesin diye kilitliyoruz(lock)
         */
        this.readWriteLock.writeLock().lock();

        try {
            int newVal = request + newRequest;
            if(newVal <= MAX_REQUEST)
                request = newVal;
        } finally {
            /*
            İşimizi bitirdiğimizde diğer threadlerin kullanımına açmak için kilidi kaldırıyoruz(unlock)
             */
            this.readWriteLock.writeLock().unlock();
        }
    }
    //istek değerini sıfırlama
    public void clear() {
        this.readWriteLock.writeLock().lock();
        try {
            request = 0;
        } finally {
            this.readWriteLock.writeLock().unlock();
        }

    }

    //Cevap verilen istekleri toplam istekten cıkarıyoruz(siliyoruz)
    public int deleteRequest(int deleteRequest ) {
        this.readWriteLock.writeLock().lock();

        try {
            int remainRequest = request - deleteRequest;
            if(remainRequest >= 0){
                request = remainRequest;
                return deleteRequest;
            } else {
                int temp = request;
                request = 0;
                return temp;
            }
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    //Request(istek) değerini döndürüyoruz
    public int getRequests() {
        this.readWriteLock.readLock().lock();

        try {
            return request;
        } finally {
            this.readWriteLock.readLock().unlock();
        }
    }
}
