import collections.FunctionConverterFromJava$;
import scala.Tuple2;
import scala.collection.immutable.IndexedSeq;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FunctionConverterFun {
    public static void main(String[] args) {
        String string = "Hello!";
        String reversed = FunctionConverterFromJava$.MODULE$.reverse(string);
        System.out.println("reversed = " + reversed);

        IndexedSeq<Tuple2<Object, Object>> zippedChars = FunctionConverterFromJava$.MODULE$.zipChars(string);
        System.out.println("zippedChars = " + zippedChars);

        List<Object> list1 = Arrays.asList(1, 2);

        Tuple2<List<Object>, List<Object>> list2 = FunctionConverterFromJava$.MODULE$.intoEvenOddForJava1().apply(list1);
        System.out.println("list2 = " + list2);

        List<Integer> list3 = Arrays.asList(1, 2);

        Tuple2<List<Integer>, List<Integer>> list4 = FunctionConverterFromJava$.MODULE$.intoEvenOddForJava2().apply(list3);
        System.out.println("list2 = " + list4);

        Function<List<Integer>, Tuple2<List<Integer>, List<Integer>>> f = FunctionConverterFromJava$.MODULE$.intoEvenOddForJava2();
        Tuple2<List<Integer>, List<Integer>> list5 = f.apply(list3);
        System.out.println("list3 = " + list5);
    }
}
