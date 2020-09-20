package mapper;

import models.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    @Insert("insert into `order` values(#{id}, #{pid}, #{userid})")
    public int insert(Order order);
}
