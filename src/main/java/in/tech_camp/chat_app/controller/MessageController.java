package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import in.tech_camp.chat_app.custom_user.CustomUserDetail;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MessageController {
  private final UserRepository userRepository;

  private final RoomUserRepository roomUserRepository;

  @GetMapping("/message")
  public String showMessages(@AuthenticationPrincipal CustomUserDetail currentUser, Model model){
    // currentUserのidでユーザー情報を取得する
    UserEntity user = userRepository.findById(currentUser.getId());
    // ビューで使用できるようにuserオブジェクトをモデルに追加する
    model.addAttribute("user", user);
    // ログインユーザーが登録されている中間テーブル一覧を取得する
    List<RoomUserEntity> roomUserEntities = roomUserRepository.findByUserId(currentUser.getId());
    // roomEntityのルームリストを取得してビューに与える。ストリームに変換して新しくroom情報だけのリストを作る
    List<RoomEntity> roomList = roomUserEntities.stream()
    // 中間テーブルのエンティティからroomEntityを取得し、
        .map(RoomUserEntity::getRoom)
        // 各roomEntityをまとめて新しいリストにまとめる
        .collect(Collectors.toList());

    // ビューでそのroomListを使えるようにroomsの名前で渡す
    model.addAttribute("rooms", roomList);
    return "messages/index";
  }
}
