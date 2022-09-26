public class UserGenerator {

    public static User getDefault(){
        return new User("burger-test1@yandex.ru","test12","burger-test");
    }

    public static User getEmptyCredentials(){
        return new User("","","");
    }

    public static User getWithoutPassword(){
        return new User("burger-test1@yandex.ru","","burger-test");
    }

    public static User getGetWithoutEmail(){
        return new User("","test12","burger-test");
    }

    public static User getWithoutName(){
        return new User("burger-test1@yandex.ru","test12","");
    }

}
