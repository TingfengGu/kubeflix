package io.fabric8.kurbine.ribbon;

import com.netflix.loadbalancer.Server;

public class KubernetesServiceEndpoint extends Server {

    public KubernetesServiceEndpoint(String host, int port) {
        super(host, port);
    }

    public KubernetesServiceEndpoint(String id) {
        super(id);
    }
}
