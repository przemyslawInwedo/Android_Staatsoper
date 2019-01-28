package app.nunc.com.staatsoperlivestreaming.Model;

public class Casting {

    private String name;
    private String role;

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "role: " + role +'\'' +
                name + '\'';
    }
}
