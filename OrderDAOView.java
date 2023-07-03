package eStoreProduct.DAO;

import java.util.List;

import eStoreProduct.model.Invoice;
import eStoreProduct.model.OrdersViewModel;

public interface OrderDAOView {
  
  // Retrieve ordered products for a given customer ID
  public List<OrdersViewModel> getorderProds(int c);

  // Retrieve the details of a specific ordered product by customer ID and product ID
  public OrdersViewModel OrdProductById(int c, Integer productId,int orderid);

  // Cancel an order in orderProducts table by product ID and order ID
  public void cancelorderbyId(Integer productId, int orderId);

  // Get the shipment status of an order by product ID and order ID
  public String getShipmentStatus(int productId, int orderId);

 

  // Check if all products in an order are cancelled
  public boolean areAllProductsCancelled(int orderId);

  // Update the shipment status of an order in Orders table
  public void updateOrderShipmentStatus(int orderId, String shipmentStatus);


  
  public Invoice getInvoiceByOrderId(int orderId,int productId);
}
