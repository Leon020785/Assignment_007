import java.util.Random;

public class Person implements Comparable<Person> {
    private static final Random rand = new Random();

    private final int id;
    private final String name;
    private final double weight;
    private final int age;

    public Person(String name, double weight, int age) {
        if (name == null || weight <= 0 || age < 0) {
            throw new IllegalArgumentException("Invalid input");
        }
        this.id = rand.nextInt(1000) + 1;  // ID fra 1 til 1000
        this.name = name;
        this.weight = weight;
        this.age = age;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getWeight() { return weight; }
    public int getAge() { return age; }

    @Override
    public int compareTo(Person o) {
        return this.name.compareTo(o.name); // Du kan udvide dette
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + ", " + weight + "kg, " + age + " years";
    }
}
