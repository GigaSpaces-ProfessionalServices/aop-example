package com.gigaspaces.demo;

import org.openspaces.core.GigaSpace;
import com.gigaspaces.demo.common.MyData;

public class Feeder {

    private GigaSpace gigaSpace;

    public void setGigaSpace(GigaSpace gigaSpace) {
        this.gigaSpace = gigaSpace;
    }

    public void postConstruct() {
        for( int i =0; i < 10; i++ ) {
            MyData data = new MyData();
            data.setId(i);
            data.setMessage("msg - " + i);

            gigaSpace.write(data);
        }


        MyData retValue = gigaSpace.read(new MyData());

    }
}
