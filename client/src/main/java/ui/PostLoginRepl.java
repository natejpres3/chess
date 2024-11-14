//package ui;
//
//import server.ServerFacade;
//
//import java.util.Collection;
//import java.util.Scanner;
//
//public class PostLoginRepl {
//    private ServerFacade serverFacade;
//    private final PreLoginRepl preLoginRepl;
////    private final UserClient client;
//
//    public PostLoginRepl(String serverUrl) {
////        client = new UserClient(serverUrl);
//        preLoginRepl = new PreLoginRepl(serverUrl);
//    }
//
//    public void run() {
//        System.out.println("Welcome! You're logged in and almost there.");
//        System.out.print(client.loggedInHelp());
//
//        Scanner scanner = new Scanner(System.in);
//        var result = "";
//        while(!result.equals("quit")) {
//            String line = scanner.nextLine();
//
//            try {
//                result = client.eval(line);
//                System.out.print(result);
//            } catch(Exception e) {
//
//            }
//        }
//        if(!client.getIsLoggedIn()) {
//            preLoginRepl.run();
//        }
//    }
//}
