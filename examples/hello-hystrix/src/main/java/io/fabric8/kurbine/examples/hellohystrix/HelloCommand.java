package io.fabric8.kurbine.examples.hellohystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class HelloCommand extends HystrixCommand<String> {

    protected HelloCommand() {
        super(HystrixCommandGroupKey.Factory.asKey("HelloGroup"));
    }

    @Override
    protected String run() throws Exception {
        return "hello";
    }
}
