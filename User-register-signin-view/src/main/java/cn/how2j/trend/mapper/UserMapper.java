package cn.how2j.trend.mapper;

import java.util.List;

import cn.how2j.trend.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;



/*MyBatis使用Mapper来实现映射，而且Mapper必须是接口
 * Mapper中定义访问users表的接口方法。
 * 
 * 有了Mapper接口，还需要对应的实现类才能真正操作数据库
 * 可以自己实现，但Mybatis提供了一个MapperFactoryBean来自动创建所有Mapper的实现类
 * @MapperScan("包名")
 */

@Repository //指示一个持久层的映射,使用mybatis持久层框架
public interface UserMapper {
	
	@Select("SELECT * FROM users WHERE id = #{id}")
	User getById(@Param("id") Long id);
	
	@Select("SELECT * FROM users WHERE email = #{email}")
	User getByEmail(@Param("email") String email);
	//offset--表记录的起始位置之前，maxResults--获取记录条数
	@Select("SELECT * FROM users LIMIT #{offset}, #{maxResults}")
	List<User> getAll(@Param("offset") int offset, @Param("maxResults") int maxResults);
	
	//返回影响数据库记录条数
	@Insert("INSERT INTO users (email, password, name, createdAt) VALUES (#{user.email}, #{user.password}, #{user.name}, #{user.createdAt})")
	int insert(@Param("user") User user);

	//返回影响数据库记录条数
	@Update("UPDATE users SET name = #{user.name}, createdAt = #{user.createdAt} WHERE id = #{user.id}")
	int update(@Param("user") User user);

	//返回影响数据库记录条数
	@Delete("DELETE FROM users WHERE id = #{id}")
	int deleteById(@Param("id") long id);
	
	
}
