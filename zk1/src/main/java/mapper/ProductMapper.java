package mapper;

import models.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper {
    @Select("select * from product where id=#{id}")
    public Product getProduct(int id);

    // 减库存
    @Update("update product set stock=stock-1 where id=#{id}")
    public int reduceStore(int id);
}
