package javagames.filesandres;

import java.util.Properties;
import java.util.Set;

public class PrintSystemProp
{
    public static void main( String[] arguments) {
        Properties sys;
        sys = System.getProperties();
        Set<Object> names = sys.keySet();

        System.out.println();
        System.out.println();
        System.out.println("------*******--------");
        System.out.println("Property name                       value");
        System.out.println();
        System.out.println();
        System.out.println();
        for (Object name : names) {
            System.out.println(name + "                         " + System.getProperty(name.toString()));
        }
    }
}
