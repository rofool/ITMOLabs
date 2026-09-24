package main.java.db;

public class Bootstrap {
    public static void main(String[] args) throws Exception {
        DataBaseManager.connect();
        long id = DataBaseManager.register("testuser", "testpass"); // пароль хэшнётся MD2
        System.out.println("created user id = " + id);
    }
}
