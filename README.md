# aop-example

This project demonstrates the use of aop.

See: https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop

The project consists of the following sub-modules.

* **common** - contains the Java class objects shared by all projects
* **space** - a project defining a space
* **declarative** - a client that is deployed as a GigaSpaces Processing Unit that contains a remote GigaSpaces proxy and demonstrates the use of aop, wired **declaratively** via a Spring `<aop:config>` XML file. The elapsed time of every GigaSpace data-operation call (`read*`, `write*`, `take*`, `change*`, `count`, `clear`, ...) is logged.
* **programmatic** - the same demo, but run as a plain standalone Java process instead of a deployed PU. The raw GigaSpace proxy is built directly in code (`SpaceProxyConfigurer`/`GigaSpaceConfigurer`, no Spring context at all) and wrapped with Spring's `AspectJProxyFactory` instead of an XML `<aop:config>`.

The two `ClientReadProfiler`/`NameHelperUtils` classes (one under `declarative/`, one under `programmatic/`) are intentionally separate copies with the same profiling logic — one is XML-driven (plain class, no annotations), the other is annotation-driven (`@Aspect`/`@Around`) since `AspectJProxyFactory` requires it. Comparing the two modules side by side is the easiest way to see both wiring styles.

|                    | declarative                                  | programmatic                                    |
|--------------------|-----------------------------------------------|--------------------------------------------------|
| Wiring             | Spring `<aop:config>` XML (`read-profiler.xml`) | `AspectJProxyFactory` in `ProgrammaticFeeder.java` |
| Spring context     | Yes (PU-managed)                              | None                                              |
| Runs as            | A deployed GigaSpaces Processing Unit          | A plain `java` process                            |
| Advice class       | `declarative/.../aop/ClientReadProfiler.java` | `programmatic/.../aop/ClientReadProfiler.java`   |

#### Deployment / Run ####

1. Build the jar files. Go to the directory containing the project's main pom.xml. We will call this the `$PROJ_DIR`.

```
mvn package
```

2. Start the grid, for example,

`$GS_HOME/bin/gs.sh host run-agent --manager --gsc=5 --webui`

3. Deploy the jar found in space/target, for example,

`$GS_HOME/bin/gs.sh pu deploy space-pu $PROJ_DIR/space/target/space.jar`

4. To see the **declarative** example, deploy declarative/target/aop-example-declarative.jar, for example,

`$GS_HOME/bin/gs.sh pu deploy declarative-pu $PROJ_DIR/declarative/target/aop-example-declarative.jar`

Check gsc logs for the following message:
```
2018-03-05 16:19:43,957 aop-example-declarative [1] INFO [com.gigaspaces.demo.aop.ClientReadProfiler] - clientReadProfiler_gigaSpace_read_MyData took 23 ms. to run.
```

5. To see the **programmatic** example instead (after step 3, no PU deploy needed), run it directly:

```
java -cp $PROJ_DIR/programmatic/target/aop-example-programmatic/classes:$PROJ_DIR/programmatic/target/aop-example-programmatic/lib/* \
    com.gigaspaces.demo.ProgrammaticFeeder
```

Its profiling output is printed straight to the console it's run from (not a GSC log), for example:
```
2026-08-11 16:00:36,676  INFO [com.gigaspaces.demo.aop.ClientReadProfiler] - programmaticFeederProfiler_mySpace_read_MyData took 25 ms. to run.
```
