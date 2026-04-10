package com.example.Product.Controller;

import com.example.Product.DTO.*;
import com.example.Product.DTO.ProductCreateDTO.ProductDTO;
import com.example.Product.Model.ProductData.Product;
import com.example.Product.Model.ProductData.ProductCategory;
import com.example.Product.Model.ProductSearchAbleObject;
import com.example.Product.Model.ProductSearchReturnModel;
import com.example.Product.Model.RecommendationDataDTO;
import com.example.Product.Response.ApiResponse;
import com.example.Product.Service.BestProductInfo;
import com.example.Product.Service.ProductService;
import com.example.Product.Service.RecommendationsService;
import com.example.Product.Service.SellerProductList;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import jakarta.xml.bind.annotation.DomHandler;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @Autowired
    RecommendationsService recommendationsService;
//
//    @PostMapping("/createProdwr/fknfuct")
//    public ResponseEntity<ApiResponse<String>> createProduct(@RequestBody @Valid CreateProduct createProduct) {
//
//        Product product = productService.createProduct(createProduct);
//        ApiResponse<String> response = ApiResponse.<String>builder()
//                .success(product != null)
//                .message(product != null ? "Product saved successfully" : "Product save failed")
//                .data(product.getId())
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/deleteProduct/{productId}")
//    public ResponseEntity<ApiResponse<Boolean>> deleteProduct(@PathVariable String proudctId){
//            Boolean bool = productService.deleteProduct(proudctId);
//            ApiResponse<Boolean> response  = ApiResponse.<Boolean>builder()
//                    .success(bool)
//                    .message(bool?"deleted":"no deleted")
//                    .data(bool)
//                    .build();
//            return ResponseEntity.ok(response);
//
//    }
//

    @GetMapping("/getRecommendations/{longitude}/{latitude}")
    public ResponseEntity<?> getRecommendations(@PathVariable Double longitude,@PathVariable Double latitude){
        Page<RecommendationDataDTO> stringList =  productService.getRecommendations(longitude,latitude);
        List<RecommendationDataDTO> list1 = stringList.getContent();
//        List<SalesDataToFrontend> list =  productService.findSales(longitude,latitude);

        RecommendationPageDTO recommendationPageDTO = RecommendationPageDTO.builder()
                .list1(list1)
                .list(null)
                .build();


        if(stringList!=null){
            return ResponseEntity.ok(recommendationPageDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no search for this");

    }



    @PostMapping("/searchProducts/{num}/{longitude}/{latitude}")
    public ResponseEntity<?> getProductList(@RequestBody SearchProduct searchProduct,
                                            @PathVariable Integer num,
                                            @PathVariable Double longitude,
                                            @PathVariable Double latitude)  {
        ProductSearchReturnModel productList = productService.search(searchProduct,num,longitude,latitude);
        if (productList == null) {
            return ResponseEntity.ok(
                    ApiResponse.<ProductSearchReturnModel>builder()
                            .success(false)
                            .message("No product found near this location increase distance")
                            .data(null)
                            .build()
            );
        }

        return ResponseEntity.ok(
                ApiResponse.<ProductSearchReturnModel>builder()
                        .success(true)
                        .message("Product fetched successfully")
                        .data(productList)
                        .build()
        );
    }

//
@PostMapping("/addProduct")
public ResponseEntity<ApiResponse<String>> addProduct(
        @RequestBody ProductDTO productDTO,
        @RequestParam("phoneNumber") String phoneNumber
) {


    String productId = productService.addProduct(productDTO, phoneNumber);

    if (productId == null) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                         .success(false)
                         .data(null)
                         .message("Get Some error try again")
                         .build();
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }
    ApiResponse<String> response = ApiResponse.<String>builder()
            .success(true)
            .data(productId)
            .message("Succesfuuly added")
            .build();
    return ResponseEntity.ok(response);
}


    @GetMapping("/getAllCategories")
    public List<ProductCategory> getAllCategories() throws JsonProcessingException {
        return productService.getAllCategories();

    }

    @GetMapping("/fetchAttributes/{category}/{type}")
    public Map<String, Set<String>> fetchAllAttributes(@PathVariable ProductCategory category, @PathVariable String type){
               return productService.fetchAttributes(category,type).get(0);
    }

    @GetMapping("/fetchTypes/{category}")
    public List<String> fetchAllTypes(@PathVariable ProductCategory category){
          return productService.fetchTypes(category);
    }

    @GetMapping("/fetchProduct/{id}")
    public ResponseEntity<?> fetchProduct(@PathVariable String id) throws JsonProcessingException {
        ProductPageDetails productPageDetails = productService.fetchProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(productPageDetails);
    }


    @GetMapping("/fetchBestProduct/{longitude}/{lat}")
    public ResponseEntity<?> fetchBestProduct(@PathVariable Double longitude ,@PathVariable Double lat){
          List<RecommendedProductCard> list = recommendationsService.bestProductNearBy(longitude,lat);
          return ResponseEntity.ok(list);
    }


    @PutMapping("/updateQuantity/{productId}/{quantity}")
    public ResponseEntity<?> updateQuantity(@PathVariable String productId,
                                            @PathVariable Integer quantity){
       int c =  productService.updateQuantity(productId,quantity);
       if(c==0){
           return ResponseEntity.ok("not more product could be added");
       }else{
           return ResponseEntity.ok("product added succesfully");
       }

    }


    @GetMapping("/getAllSellerProducts/{phoneNumber}/{page}/{size}")
    public ResponseEntity<?> getAllProductSeller(@PathVariable String phoneNumber,
                                                 @PathVariable Integer page,
                                                 @PathVariable Integer size){
        List<SellerProductList> list = productService.getAllProductsForSeller(phoneNumber,page,size);
        if(list == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);

    }

    @PostMapping("/getBestProducts")
    public ResponseEntity<List<BestProductInfo>> findBestProducts(@RequestBody @Valid BestProductDTO bestProductDTO){
        List<String> hashes = bestProductDTO.getGeohashes();
        List<BestProductInfo> list = productService.findBestProducts(hashes);
        System.out.println("👍👍 part-04");
        return ResponseEntity.ok(list);
    }






}
