package com.techacademy.controller;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;


@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private EmployeeRepository employeeRepository; //追加
    private PasswordEncoder passwordEncoder; //追加

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 従業員一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", employeeService.findAll().size());
        model.addAttribute("employeeList", employeeService.findAll());

        return "employees/list";
    }

    // 従業員詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable String code, Model model) {

        model.addAttribute("employee", employeeService.findByCode(code));
        return "employees/detail";
    }

    // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Employee employee) {

        return "employees/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Employee employee, BindingResult res, Model model) {

        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         */
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));

            return create(employee);

        }

        // 入力チェック
        if (res.hasErrors()) {
            return create(employee);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee);
        }

        return "redirect:/employees";
    }

    // 従業員削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = employeeService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employee", employeeService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/employees";
    }



    /* ここから追加 */

    @GetMapping(value = "{id}/update")
    public String getUpdate(@PathVariable("id") String code, @ModelAttribute Employee employee, Model model) {
        model.addAttribute("employee", employeeService.findByCode(code));
        return "employees/update";
    }


    /* 従業員更新処理　追加 */
    @Transactional
    @PostMapping(value = "{id}/update")
    public String postUpdate(@PathVariable("id") String code, @ModelAttribute @Validated Employee employee, BindingResult res, Model model) {

        // パスワード空白チェック
        String requestPassword = employee.getPassword();
        if ("".equals(requestPassword)) {
            // System.out.println("パスワードが空白だった場合"+requestPassword);
            employee.setPassword(employeeService.findByCode(code).getPassword());// パスワード未入力の場合は以前のパスワードを引き継ぎ
        } else {
            // System.out.println("パスワードが空白でない場合"+requestPassword);
            // パスワード桁数チェック
            int passwordLen = requestPassword.length();
            int passwordMin = 8;
            int passwordMax = 16;
            if(passwordLen < passwordMin || passwordLen > passwordMax) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.RANGECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.RANGECHECK_ERROR));
//                System.out.println("8-16以外の場合"+model);
                return "employees/update";
            }
            // パスワード形式チェック
            String regex = "^[A-Za-z0-9]+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(requestPassword);
            if(matcher.matches()) {
//                System.out.println("A-Zの場合"+requestPassword);
                employee.setPassword(passwordEncoder.encode(requestPassword));// パスワードが新しく入力された場合は暗号化
                } else {
                    model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.HALFSIZE_ERROR),
                            ErrorMessage.getErrorValue(ErrorKinds.HALFSIZE_ERROR));
//                    System.out.println("A-Z以外の場合"+model);
                    return "employees/update";
                }
        }

        // 入力チェック
        if (res.hasErrors()) {
            return "employees/update";
        }

        // 保存
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(employeeService.findByCode(code).getCreatedAt());
        employee.setUpdatedAt(now);
        System.out.println("employee"+employee);
        employeeRepository.save(employee);

        return "redirect:/employees";
    }

}
