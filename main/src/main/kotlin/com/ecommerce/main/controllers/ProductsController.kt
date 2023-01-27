package com.ecommerce.main.controllers

import com.ecommerce.main.dto.ResponseDTO
import com.ecommerce.main.models.Product
import com.ecommerce.main.utils.ArrayPaginator
import com.ecommerce.main.repositories.AdminRepository
import com.ecommerce.main.repositories.CostumerRepository
import com.ecommerce.main.repositories.ProductRepository
import com.ecommerce.main.services.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/products")
class ProductsController(val productRepository: ProductRepository, val costumerRepository: CostumerRepository, val adminRepository: AdminRepository) {
    val tokenService: TokenService = TokenService()
    val arrayPaginator: ArrayPaginator = ArrayPaginator()


    @GetMapping("")
    fun getAllProductsPaginated(@RequestHeader token: String, @RequestParam page: Long, limit: Long): ResponseEntity<Any> {
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        val customerFound = costumerRepository.findById(decodedToken)
        val adminFound = adminRepository.findById(decodedToken)
        if(customerFound.isEmpty() && adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))

        val products = arrayPaginator.paginate(productRepository.findAll(), page, limit)
        return ResponseEntity.status(HttpStatus.OK).body(products)
    }

    @GetMapping("/search")
    fun searchProducts(@RequestHeader token: String, @RequestParam title: String, page: Long, limit: Long):ResponseEntity<Any> {
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        val customerFound = costumerRepository.findById(decodedToken)
        val adminFound = adminRepository.findById(decodedToken)
        if(customerFound.isEmpty() && adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))

        val products = arrayPaginator.paginate(productRepository.search(title), page, limit)

        return ResponseEntity.status(HttpStatus.OK).body(products)
    }

    @GetMapping("/product")
    fun getProductById(@RequestHeader token: String, @RequestParam id: String):ResponseEntity<Any> {
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        val customerFound = costumerRepository.findById(decodedToken)
        val adminFound = adminRepository.findById(decodedToken)
        if(customerFound.isPresent() && adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("You dont have the required permissions to this action", "auth/no-permissions required", LocalDateTime.now()))
        if(adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))

        val product = productRepository.findById(id)
        if(product.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO("This product was not found", "products/product-not-found", LocalDateTime.now()))
        return ResponseEntity.status(HttpStatus.OK).body(product.get())
    }


    // User has to be an admin to access this routes

    @PostMapping("/new")
    fun saveNewProduct(@RequestHeader token: String, @RequestBody product: Product):ResponseEntity<ResponseDTO>{
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        val customerFound = costumerRepository.findById(decodedToken)
        val adminFound = adminRepository.findById(decodedToken)
        if(customerFound.isPresent() && adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("You dont have the required permissions to this action", "auth/no-permissions required", LocalDateTime.now()))
        if(adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))

        val productExits = productRepository.findByTitle(product.title)
        if (productExits?.title != null) return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO("This product is already created, Modify it or create a new one", "products/product-is-already-created", LocalDateTime.now()))

        val newProduct = Product()
        newProduct.title = product.title
        newProduct.imgURL = product.imgURL
        newProduct.price = product.price
        newProduct.description = product.description
        newProduct.categories = product.categories
        newProduct.inStock = product.inStock

        productRepository.save(newProduct)

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO("New product created", "products/product-created", LocalDateTime.now()))
    }

    @PutMapping("/edit")
    fun editProduct(@RequestParam id: String, @RequestHeader token: String, @RequestBody product: Product):ResponseEntity<ResponseDTO> {
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        val customerFound = costumerRepository.findById(decodedToken)
        val adminFound = adminRepository.findById(decodedToken)
        if(customerFound.isPresent() && adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("You dont have the required permissions to this action", "auth/no-permissions required", LocalDateTime.now()))
        if(adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))
        if(id.isEmpty() || id == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO("No product ID provided on the request", "products/no-id-provided-to-edit", LocalDateTime.now()))

        val productToEdit = Product()
        productToEdit._id = id
        productToEdit.title = product.title
        productToEdit.imgURL = product.imgURL
        productToEdit.price = product.price
        productToEdit.description = product.description
        productToEdit.categories = product.categories
        productToEdit.inStock = product.inStock

        productRepository.save(productToEdit)
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO("Product updated", "products/product-updated", LocalDateTime.now()))
    }

    @DeleteMapping("/delete")
    fun deleteProductByID(@RequestHeader token: String, @RequestParam id: String): ResponseEntity<ResponseDTO>{
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        val customerFound = costumerRepository.findById(decodedToken)
        val adminFound = adminRepository.findById(decodedToken)
        if(customerFound.isPresent() && adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("You dont have the required permissions to this action", "auth/no-permissions required", LocalDateTime.now()))
        if(adminFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))
        if(id.isEmpty() || id == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO("No product ID provided on the request", "products/no-id-provided-to-edit", LocalDateTime.now()))

        productRepository.deleteById(id)
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO("Product has been deleted", "products/product-deleted", LocalDateTime.now()))
    }
}