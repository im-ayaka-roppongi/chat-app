package in.tech_camp.chat_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.UserForm;
import in.tech_camp.chat_app.form.LoginForm;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@AllArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  private final UserService userService;
  
  // ユーザー新規登録画面の表示。ビューに渡すデータをmodelオブジェクトに保持
  @GetMapping("/users/sign_up")
  public String showSignUp(Model model){
    model.addAttribute("userForm", new UserForm());
    return "users/signUp";
  }

  // ユーザーの新規登録を保存
  @PostMapping("/user")
  public String createUser(@ModelAttribute("userForm") UserForm userForm, Model model) {

    UserEntity userEntity = new UserEntity();
    userEntity.setName(userForm.getName());
    userEntity.setEmail(userForm.getEmail());
    userEntity.setPassword(userForm.getPassword());

    try {
      userService.createUserWithEncryptedPassword(userEntity);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      model.addAttribute("userForm", userForm);
      return "users/signUp";
    }

    return "redirect:/";  }

  // ログイン画面の表示。ビューに渡すデータをmodelオブジェクトに保持
  @GetMapping("/users/login")
  public String loginUser(Model model){
    model.addAttribute("loginForm", new LoginForm());
    return "users/login";
  }

  // ログインに失敗したときの処理
  @GetMapping("/login")
  // urlパラメータからerror値を取得。required = falseによってパラメータが無い場合はnullになる
  public String showLoginWithError(@RequestParam(value = "error", required = false) String error, @ModelAttribute("loginForm") LoginForm loginForm, Model model) {
    if(error != null) {
      model.addAttribute("loginError", "Invalid email or password.");
    }
      return "users/login";
  }
}
