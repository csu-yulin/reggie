package csu.yulin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import csu.yulin.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}

