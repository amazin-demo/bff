package amazin.bff.client.order;

import amazin.kernel.order.command.PlaceOrder;
import amazin.kernel.order.dto.OrderDto;

public interface OrdersClient {

    OrderDto placeOrder(PlaceOrder placeOrder);
}
