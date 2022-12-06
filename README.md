# aop-example

This project demonstrates the use of aop.

See: https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop

The project consists of the following sub-modules.

* **common** - contains the Java class objects shared by all projects
* **feeder** - a client that is deployed as a Gigaspaces Processing Unit that contains a remote GigaSpaces proxy and demonstrates the use of aop. The elapsed time of a GigaSpaces.read() method call is logged.
* **space** - a project defining a space

The feeder project has defined in the read-profiler.xml the **advice** and **pointcut**. This calls the action defined in the ClientReadProfiler.java when the call is made to GigaSpaces.read().

#### Deployment ####

1. Build the jar files. Go to the directory containing the project's main pom.xml. We will call this the `$PROJ_DIR`.

```
mvn package
```

2. Start the grid, for example,

`$GS_HOME/bin/gs.sh host run-agent --manager --gsc=5 --webui`

3. Deploy the jar found in space/target, for example,

`$GS_HOME/bin/gs.sh pu deploy space-pu $PROJ_DIR/space/target/space-1.0-SNAPSHOT.jar`

4. Deploy feeder/target/aop-example-feeder.jar, for example,

`$GS_HOME/bin/gs.sh pu deploy feeder-pu $PROJ_DIR/feeder/target/feeder-1.0-SNAPSHOT.jar`

5. Check gsc logs for the following message:
```
2018-03-05 16:19:43,957 aop-example-feeder [1] INFO [com.gigaspaces.demo.aop.ClientReadProfiler] - clientReadProfiler_gigaSpace_read_MyData took 23 ms. to run.
```