package com.gigaspaces.demo.aop;


import com.gigaspaces.query.IdQuery;
import com.gigaspaces.query.IdsQuery;
import com.gigaspaces.client.ReadModifiers;
import com.j_spaces.core.client.SQLQuery;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openspaces.core.GigaSpace;
import org.springframework.util.StopWatch;

import java.util.logging.Logger;

/*
 * A separate copy of declarative's ClientReadProfiler (see
 * declarative/src/main/java/com/gigaspaces/demo/aop/ClientReadProfiler.java), ported to
 * annotation-driven AspectJ style for this module's programmatic wiring example: here
 * there's no Spring context/XML PU to hang a <aop:config> off of, so the raw GigaSpace
 * proxy (built via SpaceProxyConfigurer/GigaSpaceConfigurer, see ProgrammaticFeeder) is
 * instead wrapped with Spring's AspectJProxyFactory and addAspect(new ClientReadProfiler(name)).
 * The pointcut expression and profiling logic are otherwise identical to declarative's example.
 */
@Aspect
public class ClientReadProfiler {

    protected Logger log = Logger.getLogger(this.getClass().getName());

    protected String name;

    public ClientReadProfiler() {
    }

    public ClientReadProfiler(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * Data points:
     * gigaProxy object
     * method name
     * argument list
     * template or SQLQuery
     * readModifiers
     *
     * The same advice/profiler can be used across all GigaSpace data operations
     * (read*, write*, take*, change*, count, clear, ...) because in every case the
     * first argument is the template/query/entry/class being operated on. Accessors
     * (get, is, set, new prefixed methods) are excluded below: they're config lookups,
     * not API calls, and getName() is invoked by this advice itself, so including it
     * would recurse infinitely.
     */
    @Around("execution(* org.openspaces.core.GigaSpace.*(..))"
            + " && !execution(* org.openspaces.core.GigaSpace.get*(..))"
            + " && !execution(* org.openspaces.core.GigaSpace.is*(..))"
            + " && !execution(* org.openspaces.core.GigaSpace.set*(..))"
            + " && !execution(* org.openspaces.core.GigaSpace.new*(..))")
    public Object profile(ProceedingJoinPoint call) throws Throwable {

        GigaSpace gigaProxy = (GigaSpace) call.getThis();

        String gigaProxyName = gigaProxy.getName();

        String methodName = call.getSignature().getName();

        Object[] argArray = call.getArgs();

        Object param1 = argArray[0];

        String queryType = "";

        if( param1 instanceof SQLQuery) {
            SQLQuery<?> sqlQuery = (SQLQuery<?>) param1;
            String className = NameHelperUtils.getSimpleName(sqlQuery.getTypeName());
            queryType = param1.getClass().getSimpleName() + "<" + className + ">";
        } else if( param1 instanceof IdQuery) {
            // used in readById
            IdQuery<?> idQuery = (IdQuery<?>) param1;
            String className = NameHelperUtils.getSimpleName(idQuery.getTypeName());
            queryType = param1.getClass().getSimpleName() + "<" + className + ">";
        } else if( param1 instanceof IdsQuery) {
            // used in readByIds
            IdsQuery<?> idsQuery = (IdsQuery<?>) param1;
            String className = NameHelperUtils.getSimpleName(idsQuery.getTypeName());
            queryType = param1.getClass().getSimpleName() + "<" + className + ">";
        }
        else if( param1 instanceof Class ) {
            Class<?> clazz = (Class<?>) param1;
            queryType = clazz.getSimpleName();
        }
        else {
            // we are dealing with a template, get the class name
            queryType = param1.getClass().getSimpleName();
        }


        ReadModifiers readModifiers = null;

        /*
         * if ReadModifiers was used,
         *      there will have been 2-4 arguments
         *      it will always be the last argument
         */
        if( argArray.length >= 2 ) {
            Object lastParam = argArray[argArray.length - 1];
            if( lastParam instanceof ReadModifiers ) {
                readModifiers = (ReadModifiers) lastParam;
            }
        }


        StopWatch clock = new StopWatch("Profiling for " + this.name);
        try {
            clock.start(call.toShortString());
            return call.proceed();
        } finally {
            clock.stop();
            System.out.println(clock.prettyPrint());
            log.info(NameHelperUtils.createName(this.name, gigaProxyName, methodName, queryType, readModifiers) + " took " + clock.getTotalTimeMillis() + " ms. to run.");
        }
    }
}
