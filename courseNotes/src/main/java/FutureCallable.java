import java.util.concurrent.*;
import scala.jdk.javaapi.FutureConverters;

public class FutureCallable {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        CompletableFuture<String> future = new CompletableFuture<>();
        scala.concurrent.Future<String> scalaFuture = FutureConverters.asScala(future);

        System.out.println("Do something else while callable executes");

        future.complete("All done!");

        System.out.println("Retrieve the result of the future");
        String result = future.get(); // Future.get() blocks until the result is available
        System.out.println(result);

        executorService.shutdown();
    }
}
