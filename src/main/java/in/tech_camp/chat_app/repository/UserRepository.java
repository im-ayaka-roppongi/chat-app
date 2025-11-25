package in.tech_camp.chat_app.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

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

  // idでユーザーを検索するメソッド
  @Select("SELECT * FROM users WHERE id = #{id}")
  UserEntity findById(Integer id);

  // ユーザー情報更新
  @Update("UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}")
  void update(UserEntity user);

  // メールアドレスが一意かチェック
  @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
  boolean existsByEmail(String email);

  // 更新時用:指定のIDのユーザー以外でEmailが使用されていないか確認するメソッド
  @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email} AND id != #{userId}")
  boolean existsByEmailExcludingCurrent(String email, Integer userId);

  // ルーム作成時にユーザーをプルダウンで表示するためのメソッド
  @Select("SELECT * FROM users WHERE id <> #{exludedId}")
  List<UserEntity> findAllExcept(Integer excludedId);
}
