package com.food.ordering.system.order.service.dataaccess.restaurant.mapper;

import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.mapper.UtilsMapper;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapper.class})
public interface RestaurantDataAccessMapper {

  default List<UUID> toRestaurantProducts(Restaurant restaurant) {
    return restaurant.getProducts().stream().map(product -> product.getId().value()).toList();
  }

  default Restaurant toRestaurant(List<RestaurantEntity> restaurantEntities) {
    RestaurantEntity restaurantEntity =
        restaurantEntities.stream()
            .findFirst()
            .orElseThrow(() -> new RestaurantDataAccessException("Restaurant not found"));
    List<Product> restaurantProducts = restaurantEntities.stream().map(this::toProduct).toList();
    return Restaurant.builder()
        .id(new RestaurantId(restaurantEntity.getRestaurantId()))
        .products(restaurantProducts)
        .active(restaurantEntity.getRestaurantActive())
        .build();
  }

  default Product toProduct(RestaurantEntity restaurantEntity) {
    return new Product(
        new ProductId(restaurantEntity.getProductId()),
        restaurantEntity.getProductName(),
        new Money(restaurantEntity.getProductPrice()));
  }
}
