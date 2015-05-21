package com.wacai.sdk.jtr;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonModelTest {
    @Test
    public void issue10() throws Exception {
        final String queryString = "bankId=11&accType=3&entryName=wewewe&isUpdate=1";
        assertThat(JsonModel.get(queryString, "m", "default"),is("default"));
    }

    @Test
    public void should_get_query_param_m() throws Exception {
        final String queryString = "m=model";
        assertThat(JsonModel.get(queryString, "m", "default"),is("model"));
    }
}