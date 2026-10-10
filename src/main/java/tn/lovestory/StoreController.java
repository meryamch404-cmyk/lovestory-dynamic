package tn.lovestory;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class StoreController {
 private final CustomerRepo customers;
 private final OrderRepo orders;
 private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
 public StoreController(CustomerRepo customers, OrderRepo orders) { this.customers=customers; this.orders=orders; }

 @GetMapping("/health")
 public Map<String,String> health(){ return Map.of("status","ok","store","Love Story"); }

 @PostMapping("/register")
 public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
  if (r.email()==null || r.email().isBlank() || r.password()==null || r.password().length()<8
      || r.username()==null || r.username().isBlank() || r.firstname()==null || r.lastname()==null)
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Vérifiez les champs obligatoires (mot de passe : 8 caractères minimum).");
  if (customers.existsByEmailIgnoreCase(r.email().trim()))
    throw new ResponseStatusException(HttpStatus.CONFLICT,"Cette adresse e-mail est déjà inscrite.");
  Customer c = new Customer();
  c.firstname=r.firstname().trim(); c.lastname=r.lastname().trim(); c.email=r.email().trim().toLowerCase();
  c.username=r.username().trim(); c.phone=r.phone(); c.country=r.country(); c.city=r.city();
  c.passwordHash=encoder.encode(r.password());
  customers.save(c);
  return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Inscription enregistrée avec succès."));
 }

 @PostMapping("/orders")
 public ResponseEntity<?> createOrder(@RequestBody OrderRequest r) {
  if (r.customerName()==null || r.customerName().isBlank() || r.email()==null || !r.email().contains("@")
      || r.items()==null || r.items().isEmpty())
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nom, e-mail et panier sont obligatoires.");
  double total=0;
  List<String> lines=new ArrayList<>();
  for (OrderItem item:r.items()) {
   if (!Set.of("Black Love Hoodie","White Love Hoodie","Green Love Hoodie","Purple Love Hoodie").contains(item.name())
       || item.quantity()<1 || item.quantity()>15)
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Produit ou quantité invalide.");
   double price=90.0;
   total += price*item.quantity();
   lines.add(item.name()+" x "+item.quantity());
  }
  StoreOrder o=new StoreOrder(); o.customerName=r.customerName().trim(); o.email=r.email().trim().toLowerCase();
  o.phone=r.phone(); o.items=String.join(" | ",lines); o.total=total; o.createdAt=LocalDateTime.now();
  orders.save(o);
  return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Commande enregistrée !","orderId",o.id,"total",total));
 }

 record RegisterRequest(String lastname,String firstname,String email,String phone,String date,String username,
                        String password,String confirmPassword,String country,String city) {}
 record OrderItem(String name,int quantity) {}
 record OrderRequest(String customerName,String email,String phone,List<OrderItem> items) {}
}

@Entity
class Customer {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String firstname;
 @Column(nullable=false) public String lastname;
 @Column(nullable=false,unique=true) public String email;
 @Column(nullable=false) public String username;
 @Column(nullable=false) public String passwordHash;
 public String phone; public String country; public String city;
}
interface CustomerRepo extends JpaRepository<Customer,Long> { boolean existsByEmailIgnoreCase(String email); }

@Entity
@Table(name="store_orders")
class StoreOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String customerName;
 @Column(nullable=false) public String email;
 public String phone;
 @Column(nullable=false,length=2000) public String items;
 public double total;
 public LocalDateTime createdAt;
}
interface OrderRepo extends JpaRepository<StoreOrder,Long> {}
