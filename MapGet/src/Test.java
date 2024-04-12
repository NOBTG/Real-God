import java.net.URISyntaxException;

public final class Test {
    public static void main(String[] args) throws URISyntaxException, ClassNotFoundException {
        for (StackTraceElement element : new Exception().getStackTrace()) {
            System.out.println(element.toString());
        }
        System.out.println(Class.forName(new Exception().getStackTrace()[0].getClassName()));
    }
}
