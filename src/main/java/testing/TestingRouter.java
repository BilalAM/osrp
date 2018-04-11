package testing;

import network.Router;

public class TestingRouter {

    public static void main(String[] args){
        Router router = new Router("My Router",2000);
        while(true) {
            router.initialize();
        }


    }
}
