public class TypeSafety {
    public static void main(String[] args) {
        String[] covariantArrayOfString = new String[] {"a", "b", "c"};
        Object[] covariantArrayOfObject = covariantArrayOfString;
        covariantArrayOfObject[0] = 1;
    }
}