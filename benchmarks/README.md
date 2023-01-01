

```
mvn package
java -jar target/microbenchmarks.jar IpParseBenchmark
```


```
public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IpParseBenchmark.class.getSimpleName())
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.MILLISECONDS)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
```