package io.fabric8.kurbine.discovery;

import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.discovery.InstanceDiscovery;
import io.fabric8.kubernetes.api.model.EndpointAddress;
import io.fabric8.kubernetes.api.model.EndpointSubset;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KubernetesDiscovery implements InstanceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesDiscovery.class);

    private static final String HYSTRIX_ENABLED = "hystrix.enabled";
    private static final String HYSTRIX_CLUSTER = "hystrix.cluster";

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT = "default";
    private static final Map<String, String> DEFAULT_LABELS = new HashMap<String, String>();

    static {
        DEFAULT_LABELS.put(HYSTRIX_ENABLED, "true");
    }


    private final KubernetesClient client;
    private final Map<String, String> labels;

    public KubernetesDiscovery() {
        this(new DefaultKubernetesClient(), DEFAULT_LABELS);
    }

    public KubernetesDiscovery(KubernetesClient client, Map<String, String> labels) {
        this.client = client;
        this.labels = labels;
    }

    public Collection<Instance>  getInstanceList() throws Exception {
        List<Instance> result = new ArrayList<Instance>();
        for (Endpoints endpoint : client.endpoints().withLabels(labels).list().getItems()) {
            try {
                result.add(toInstance(endpoint));
            } catch (Throwable t) {
                LOGGER.error("Error processing endpoint", t);
            }
        }
        return result;
    }


    private static List<EndpointAddress> addresses(Endpoints e) {
        List<EndpointAddress> result = new ArrayList<EndpointAddress>();
        for (EndpointSubset endpointSubset : e.getSubsets()) {
            result.addAll(endpointSubset.getAddresses());
        }
        return result;
    }


    private static Instance toInstance(Endpoints e) {
        if (e.getSubsets().isEmpty()) {
            throw new IllegalArgumentException("Endpoint e is not valid: Has not subsets");
        } else if (e.getSubsets().size() > 1) {
            throw new IllegalArgumentException("Endpoint e is not valid: Has too many subsets");
        } else {
            String clusterName = e.getMetadata().getLabels().containsKey(HYSTRIX_CLUSTER) ?
                    e.getMetadata().getLabels().get(HYSTRIX_CLUSTER) :
                    DEFAULT;
            EndpointAddress endpointAddress = toAddress(e.getSubsets().get(0));
            return new Instance(endpointAddress.getIp(), clusterName, true);
        }
    }

    private static EndpointAddress toAddress(EndpointSubset e) {
        if (e.getAddresses().isEmpty()) {
            throw new IllegalArgumentException("EndpointSubset e is not valid: Has not addresses");
        } else if (e.getAddresses().size() > 1) {
            throw new IllegalArgumentException("EndpointSubset e is not valid: Has too many addresses");
        } else {
            return e.getAddresses().get(0);
        }
    }
}
