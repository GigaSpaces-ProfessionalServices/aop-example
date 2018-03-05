# aop-example

This project demonstrates the use of aop.

See: https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop

The project consists of the following sub-modules.

* common - contains the Java class objects shared by all projects
* feeder - a client that contains a remote GigaSpaces proxy and demonstrates the use of aop. The elapsed time of a GigaSpace.read() method call is logged.
* aop - a project defining a space

The feeder project has a defined in the read-profiler.xml the advice and pointcut. This calls the action defined in the ClientReadProfiler.java.

For deployment on XAP grid.

```
mvn package
```

1. Deploy the jar found in aop/target
2. Deploy feeder/target/aop-example-feeder.jar
3. Check gsc logs for the following message:
2018-03-05 16:19:43,957 aop-example-feeder [1] INFO [com.gigaspaces.demo.aop.ClientReadProfiler] - clientReadProfiler_gigaSpace_read_MyData took 23 ms. to run.
