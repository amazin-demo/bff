package amazin.bff.client.order;

import amazin.kernel.order.command.PlaceOrder;
import amazin.kernel.order.dto.OrderDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class DefaultOrdersClient implements OrdersClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public DefaultOrdersClient(RestTemplate restTemplate, @Value("${service.payments.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public OrderDto placeOrder(PlaceOrder placeOrder) {
        return restTemplate.postForObject(baseUrl + "/orders", placeOrder, OrderDto.class);
    }
}
