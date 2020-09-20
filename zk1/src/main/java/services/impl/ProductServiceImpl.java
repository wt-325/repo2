package services.impl;

import mapper.OrderMapper;
import mapper.ProductMapper;
import models.Order;
import models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.ProductService;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void reduceStock(int id) throws Exception {
        // 获取库存
        Product product = productMapper.getProduct(id);
        if (product.getStock() <= 0) {
            throw new RuntimeException("库存已空...");
        }
        int i = productMapper.reduceStore(id);
        if (i == 1) {
            Order order = new Order();
            order.setId(UUID.randomUUID().toString());
            order.setPid(id);
            order.setUserid(101);
            orderMapper.insert(order);
        } else {
            throw new RuntimeException("减库存失败...");
        }
    }
}
