package amazin.bff.client.order;

import amazin.kernel.order.command.PlaceOrder;
import amazin.kernel.order.dto.OrderDto;
import amazin.kernel.order.dto.OrderItemDto;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DefaultOrdersClientTest {
    @Rule
    public final PactProviderRuleMk2 pactRule = new PactProviderRuleMk2("orders", this);

    @Pact(provider = "orders", consumer = "bff")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
                .uponReceiving("Place Order")
                .path("/orders")
                .method("POST")
                .body(newJsonBody(o ->
                        o.stringValue("accountId", "12345")
                                .array("items", arr -> {
                                    arr.object(item -> {
                                        item.stringType("sku", "a");
                                        item.numberType("quantity", 1);
                                        item.numberType("price", 1);
                                    });
                                    arr.object(item -> {
                                        item.stringType("sku", "b");
                                        item.numberType("quantity", 2);
                                        item.numberType("price", 2);
                                    });
                                    arr.object(item -> {
                                        item.stringType("sku", "c");
                                        item.numberType("quantity", 3);
                                        item.numberType("price", 3);
                                    });
                                })

                ).build())
                .willRespondWith()
                .status(200)
                .matchHeader("Content-Type", "application/json;charset=UTF-8")
                .body(newJsonBody(o -> {
                    o.stringValue("accountId", "12345");
                    o.stringType("id", "abc123");
                    o.array("items", arr -> {
                        arr.object(item -> {
                            item.stringType("sku", "a");
                            item.numberType("quantity", 1);
                            item.numberType("price", 1);
                        });
                        arr.object(item -> {
                            item.stringType("sku", "b");
                            item.numberType("quantity", 2);
                            item.numberType("price", 2);
                        });
                        arr.object(item -> {
                            item.stringType("sku", "c");
                            item.numberType("quantity", 3);
                            item.numberType("price", 3);
                        });
                    });
                }).build())
                .toPact();
    }

    @Test
    @PactVerification
    public void testPlaceOrder() {
        RestTemplate restTemplate = new RestTemplate();
        final String url = pactRule.getUrl();
        final DefaultOrdersClient client = new DefaultOrdersClient(restTemplate, url);
        final OrderDto answer = client.placeOrder(new PlaceOrder("12345", Lists.newArrayList(
                new OrderItemDto("a", 1, 1),
                new OrderItemDto("b", 2, 2),
                new OrderItemDto("c", 3, 3))
        ));
        assertThat(answer.getAccountId()).isEqualTo("12345");
        assertThat(answer.getId()).isNotBlank();
        assertThat(answer.getItems().size()).isEqualTo(3);
    }
}