import java.util.ArrayList;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Elvira Paun", 79, 45));
        people.add(new Person("Isabella", 35, 8));
        people.add(new Person("Kejser", 90, 39));


        for(Person p : people){
            System.out.println(p);
        }
    }
}
