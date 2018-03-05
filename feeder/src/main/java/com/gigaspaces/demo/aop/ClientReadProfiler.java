package com.gigaspaces.demo.aop;


import com.gigaspaces.query.IdQuery;
import com.gigaspaces.query.IdsQuery;
import com.gigaspaces.client.ReadModifiers;
import com.j_spaces.core.client.SQLQuery;
import org.aspectj.lang.ProceedingJoinPoint;
import org.openspaces.core.GigaSpace;
import org.springframework.util.StopWatch;

import java.util.logging.Logger;


public class ClientReadProfiler {

    protected Logger log = Logger.getLogger(this.getClass().getName());

    protected String name;

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
     * The same advice/profiler can be used because the arguments to read/
     * readMultiple/readIfExists/readById/readByIds are very similar.
     */
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
