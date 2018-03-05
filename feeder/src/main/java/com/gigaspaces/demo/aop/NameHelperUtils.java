package com.gigaspaces.demo.aop;

import com.gigaspaces.client.ReadModifiers;

public class NameHelperUtils {

    public static String createName(String name, String gigaProxyName,
                                    String methodName, String queryType, ReadModifiers readModifiers) {
        if (readModifiers == null) {
            return name + "_" + gigaProxyName + "_" + methodName + "_" + queryType;
        } else {
            return name + "_" + gigaProxyName + "_" + methodName + "_" + queryType + "_" + NameHelperUtils.getSimpleName(readModifiers.toString());
        }
    }

    /*
     * Remove the package name from the class name.
     */
    public static String getSimpleName(String className) {
        int lastIndex = className.lastIndexOf(".");
        if (lastIndex == -1) {
            return className;
        } else {
            return className.substring(lastIndex + 1);
        }
    }
}