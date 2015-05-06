package com.wacai.sdk.jtr;

import org.junit.Test;

import static com.wacai.sdk.jtr.RequestPath.base;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JSPServletTest {
    @Test
    public void should_get_base_name() throws Exception {
        assertThat(base("/a.jsp"), is("/a"));
        assertThat(base("/a/b.jsp"), is("/a/b"));
    }

    @Test
    public void should_get_query_value_by_key() throws Exception {
        assertThat(JsonModel.get("m=a", "m", "b"), is("a"));
        assertThat(JsonModel.get("m=a&", "m", "b"), is("a"));
        assertThat(JsonModel.get("", "m", "b"), is("b"));
    }
}