package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.LoginForm;
import in.tech_camp.chat_app.form.UserEditForm;
import in.tech_camp.chat_app.form.UserForm;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.service.UserService;
import in.tech_camp.chat_app.validation.ValidationOrder;
import lombok.AllArgsConstructor;


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
  public String createUser(@ModelAttribute("userForm") @Validated(ValidationOrder.class) UserForm userForm, BindingResult result, Model model) {
    userForm.validatePasswordConfirmation(result);
    if (userRepository.existsByEmail(userForm.getEmail())) {
      result.rejectValue("email", "null", "Email already exists");
    }

    if (result.hasErrors()) {
      List<String> errorMessages = result.getAllErrors().stream()
              .map(DefaultMessageSourceResolvable::getDefaultMessage)
              .collect(Collectors.toList());

      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("userForm", userForm);
      return "users/signUp";
    }

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

  // 編集画面を表示する
  @GetMapping("/users/{userId}/edit")
  // pathに含まれるuserIdを取得するために@PathVariableを使う。ここで処理した内容はビューで表示するのでモデルオブジェクトも渡す
  public String editUserForm(@PathVariable("userId") Integer userId, Model model) {
    // 取得したidのuser情報をとってきてuserに格納する（idでdb検索するメソッドはリポジトリに記載）
    UserEntity user = userRepository.findById(userId);
    
    UserEditForm userForm = new UserEditForm();
    userForm.setId(user.getId());
    userForm.setName(user.getName());
    userForm.setEmail(user.getEmail());

    model.addAttribute("user", userForm);
    return "users/edit";
  }

  // ユーザー情報を更新する
  @PostMapping("users/{userId}")
  // urlからuserIdを受け取り、ビューから送られてきたデータをuserという名前でuserEditFormに紐づける。
  public String updateUser(@PathVariable("userId") Integer userId, @ModelAttribute("user") @Validated(ValidationOrder.class) UserEditForm userEditForm, BindingResult result, Model model) {
    String newEmail = userEditForm.getEmail();
    if (userRepository.existsByEmailExcludingCurrent(newEmail, userId)) {
      result.rejectValue("email", "error.user", "Email already exists");
    }
    if (result.hasErrors()) {
      List<String> errorMessages = result.getAllErrors().stream()
                                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                    .collect(Collectors.toList());
      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("user", userEditForm);
      return "users/edit";
    }
    
    // 取得したidのuser情報をとってきてuserに格納する（idでdb検索するメソッドはリポジトリに記載）
    UserEntity user = userRepository.findById(userId);
    // そのuserに、フォームから送られてきたnameとemailをセットする
    user.setName(userEditForm.getName());
    user.setEmail(userEditForm.getEmail());

    // 成功すればupdate処理を実行する。引数はuser
    try {
      userRepository.update(user);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      model.addAttribute("user", userEditForm);
      return "users/edit";
    }
    return "redirect:/";
  }
}
