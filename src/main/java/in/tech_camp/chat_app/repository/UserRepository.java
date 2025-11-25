package in.tech_camp.chat_app.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import in.tech_camp.chat_app.entity.UserEntity;

@Mapper
public interface UserRepository {
  // ユーザー保存処理
  @Insert("INSERT INTO users (name, email, password) VALUES (#{name}, #{email}, #{password})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(UserEntity user);

  // emailでユーザーを検索するメソッド
  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity findByEmail(String email);
}
