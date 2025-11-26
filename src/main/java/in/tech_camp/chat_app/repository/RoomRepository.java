package in.tech_camp.chat_app.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.chat_app.entity.RoomEntity;

@Mapper
public interface RoomRepository {
  // ルームの作成保存メソッド
  @Insert("INSERT INTO rooms(name) VALUES(#{name})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(RoomEntity roomEntity);

  // room_idでroomを検索し、RoomEntityを取得するメソッド
  @Select("SELECT * FROM rooms WHERE id = #{id}")
  RoomEntity findById(Integer id);

  // ルームの削除
  @Delete("DELETE FROM rooms WHERE id = #{id}")
  void deleteById(Integer id);
}
