package cn.how2j.trend.service;


import cn.how2j.trend.entity.User;
import cn.how2j.trend.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
public class UserService {

	final Logger logger = LoggerFactory.getLogger(getClass());

	 @Autowired
	 UserMapper userMapper;

	public User getUserById(long id) {
		User user = userMapper.getById(id);
		if (user == null) {
	            throw new RuntimeException("User not found by id.");
	    }
		return user;
	}

	public User getUserByEmail(String email) {
		User user = userMapper.getByEmail(email);
		if (user == null) {
	            throw new RuntimeException("User not found by email.");
	    }
		return user;
	}

	public User signin(String email, String password) {
		logger.info("try login by {}...", email);
		User user = getUserByEmail(email);
		if (user.getPassword().equals(password)) {
			return user;
		}
		throw new RuntimeException("login failed.");
	}

	public User register(String email, String password, String name) {
		logger.info("try register by {}...", email);
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		user.setName(name);
		user.setCreatedAt(System.currentTimeMillis());	
		if (1 != userMapper.insert(user)){
			throw new RuntimeException("Insert failed.");
		}
		user.setId(userMapper.getByEmail(email).getId());
		return user;
	}
/*
	public void updateUser(User user) {
		if(1 != userMapper.update(user)){
			throw new RuntimeException("User not found by id");
		}
	}
	*/
}
