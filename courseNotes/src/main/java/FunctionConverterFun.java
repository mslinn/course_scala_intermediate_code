import collections.FunctionConverterFromJava$;
import scala.Tuple2;
import scala.collection.immutable.IndexedSeq;
import java.util.Arrays;
import java.util.List;

public class FunctionConverterFun {
    public static void main(String[] args) {
        String string = "Hello!";
        String reversed = FunctionConverterFromJava$.MODULE$.reverse(string);
        System.out.println("reversed = " + reversed);

        IndexedSeq<Tuple2<Object, Object>> zippedChars = FunctionConverterFromJava$.MODULE$.zipChars(string);
        System.out.println("zippedChars = " + zippedChars);

        List<Object> list1 = Arrays.asList(1, 2);
        Tuple2<List<Object>, List<Object>> list2 = FunctionConverterFromJava$.MODULE$.intoEvenOddForJava().apply(list1);
        System.out.println("list2 = " + list2);

        java.util.function.Function<List<Object>, Tuple2<List<Object>, List<Object>>> f = FunctionConverterFromJava$.MODULE$.intoEvenOddForJava();
        Tuple2<List<Object>, List<Object>> list3 = f.apply(list1);
        System.out.println("list3 = " + list3);
    }
}
