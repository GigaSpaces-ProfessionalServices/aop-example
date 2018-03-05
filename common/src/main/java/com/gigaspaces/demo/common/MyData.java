package com.gigaspaces.demo.common;


import java.io.Serializable;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class MyData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String message;

    public MyData() {
        super();
    }

    public MyData(Integer id, String message) {
        this();
        this.id = id;
        this.message = message;
    }

    @SpaceId
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
