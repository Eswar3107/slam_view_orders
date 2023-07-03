package eStoreProduct.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import eStoreProduct.DAO.OrderDAOView;
import eStoreProduct.DAO.ProdStockDAO;
import eStoreProduct.model.Invoice;
import eStoreProduct.model.OrdersViewModel;
import eStoreProduct.model.Product;
import eStoreProduct.model.custCredModel;
import eStoreProduct.utility.ProductStockPrice;

@Controller
public class customerOrderController {
	private static final Logger logger = 
			LoggerFactory.getLogger(customerOrderController.class);
  
  @Autowired
  private OrderDAOView orderdaov;
  private ProdStockDAO productstockdao;
  public customerOrderController(OrderDAOView odaov,ProdStockDAO productdao)
  {
	  orderdaov=odaov;
	  productstockdao=productdao;
  }
  
  @RequestMapping("/CustomerOrdersProfile")
  // Method to show ordered products of the user
  public String showOrders(Model model, HttpSession session) {
      logger.info("Showing orders");

    custCredModel cust = (custCredModel) session.getAttribute("customer");
    // Getting ordered products from the DAO
    List<OrdersViewModel> orderProducts = orderdaov.getorderProds(cust.getCustId());
    
    model.addAttribute("orderProducts", orderProducts);
    return "orders";
  }
  
  // Getting the details of the specific product when clicked on it
  @GetMapping("/productDetails")
  public String getProductDetails(@RequestParam("id") int productId, @RequestParam("orderId") int orderid,Model model, HttpSession session) {
    custCredModel cust = (custCredModel) session.getAttribute("customer");
    logger.info("Getting product details");
    OrdersViewModel product = orderdaov.OrdProductById(cust.getCustId(), productId,orderid);
    model.addAttribute("product", product);
    return "OrdProDetails";
  }

  // Cancelling the order
  @PostMapping("/cancelOrder")
  @ResponseBody
  public String cancelOrder(@RequestParam("orderproId") Integer productId, @RequestParam("orderId") int orderId) {
    // Cancelling order in the orderproducts table and updating the status
	  logger.debug("Cancelling order with ID: " + productId + orderId);
    orderdaov.cancelorderbyId(productId, orderId);
    
    // Checking whether all the products in an order are cancelled or not
    boolean allProductsCancelled = orderdaov.areAllProductsCancelled(orderId);
    
    if (allProductsCancelled) {
      // Update the shipment status of the order in slam_Orders table
      orderdaov.updateOrderShipmentStatus(orderId, "cancelled");
      
    }
    productstockdao.updateStock(productId, orderId);
    return "Order with ID " + productId + orderId + " has been cancelled.";
  }
  
  @RequestMapping(value = "/trackOrder", method = RequestMethod.GET)
  @ResponseBody
  // Method to track the order
  public String trackOrder(@RequestParam("orderproId") int productId, @RequestParam("orderId") int orderId) {
    // Retrieve the shipment status for the given order ID
      logger.debug("Tracking order with ID: " + productId + orderId);

    String shipmentStatus = orderdaov.getShipmentStatus(productId, orderId);
    return shipmentStatus;
  }
  //method to view the bill 
  @GetMapping("/viewInvoice")
 
  public ResponseEntity<String> viewInvoice(@RequestParam("orderId") int orderId, @RequestParam("productId") int productId, Model model) {
      Invoice invoice = orderdaov.getInvoiceByOrderId(orderId, productId);
      model.addAttribute("invoice", invoice);
     String res = invoice.toString();
     System.out.println(res);
 	return ResponseEntity.status(HttpStatus.OK).body(res);
  }
  
}


  
 