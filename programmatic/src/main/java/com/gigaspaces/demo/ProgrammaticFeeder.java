package com.gigaspaces.demo;

import com.gigaspaces.demo.aop.ClientReadProfiler;
import com.gigaspaces.demo.common.MyData;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

/*
 * Same demo as declarative's Feeder (write 10 MyData, then read one), wired programmatically
 * instead of via a Spring PU/XML <aop:config>: no Spring context is created at all here, the
 * raw GigaSpace proxy is built directly with SpaceProxyConfigurer/GigaSpaceConfigurer (the same
 * "mySpace" space that declarative/src/main/resources/META-INF/spring/pu.xml connects to via
 * <os-core:space-proxy>), then wrapped with Spring's AspectJProxyFactory to apply the same
 * profiling advice as declarative's ClientReadProfiler.
 *
 * Run standalone (does not deploy as a PU) after `space-pu` has been deployed, e.g.:
 *   java -cp target/aop-example-programmatic/classes:target/aop-example-programmatic/lib/* \
 *       com.gigaspaces.demo.ProgrammaticFeeder
 */
public class ProgrammaticFeeder {

    public static void main(String[] args) throws Exception {
        SpaceProxyConfigurer configurer = new SpaceProxyConfigurer("mySpace");
        try {
            GigaSpace rawGigaSpace = new GigaSpaceConfigurer(configurer).gigaSpace();

            AspectJProxyFactory proxyFactory = new AspectJProxyFactory(rawGigaSpace);
            proxyFactory.addAspect(new ClientReadProfiler("programmaticFeederProfiler"));
            GigaSpace gigaSpace = (GigaSpace) proxyFactory.getProxy();

            for (int i = 0; i < 10; i++) {
                MyData data = new MyData();
                data.setId(i);
                data.setMessage("msg - " + i);

                gigaSpace.write(data);
            }

            MyData retValue = gigaSpace.read(new MyData());
            System.out.println("Read back: " + retValue);
        } finally {
            configurer.close();
        }
    }
}
