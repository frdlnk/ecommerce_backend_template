package com.ecommerce.main.utils

import com.ecommerce.main.models.Product
import java.util.stream.Collectors

class ArrayPaginator {
    fun paginate(productsList: List<Product>, page: Long, limit: Long) :List<Product> {
        var startIndex = page -1
        startIndex *= limit
        var endIndex = page * limit

        return productsList.stream().skip(startIndex).limit(endIndex).collect(Collectors.toList())
    }
}