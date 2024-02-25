package com.techacademy.controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Reports;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportsService;
import com.techacademy.service.UserDetail;


@Controller
@RequestMapping("reports")
public class ReportsController {

    private final ReportsService reportsService;
    private final EmployeeService employeeService;
//    private final UserDetail userDetail;

    @Autowired
    public ReportsController(ReportsService repotsService, EmployeeService employeeService) {//, UserDetail userDetail) {
        this.reportsService = repotsService;
        this.employeeService = employeeService;
//        this.userDetail = userDetail;
    }


    // 一覧画面へ遷移
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {

        String authoritie = userDetail.getEmployee().getRole().toString();

        // 管理者権限のユーザーの場合
        if(authoritie == "ADMIN") {
            System.out.println("adminです");
            model.addAttribute("listSize", reportsService.findAll().size());
            model.addAttribute("reportList", reportsService.findAll());
        } else {
         // 管理者権限以外のユーザーの場合
          System.out.println("adminじゃないです1");
            Employee employee = userDetail.getEmployee();
            model.addAttribute("listSize", reportsService.findByEmployee(employee).size());
            model.addAttribute("reportList", reportsService.findByEmployee(employee));
        }

        return "reports/list";
    }

    // 新規登録画面へ遷移
    @GetMapping(value = "/add")
    public String create(Reports reports, Model model, @AuthenticationPrincipal UserDetail userDetail) {

        String code = userDetail.getEmployee().getCode();
        model.addAttribute("employeeName", employeeService.findByCode(code).getName());
        model.addAttribute("reports", reports);

        return "reports/new";
    }

    // 新規登録処理
    @Transactional
    @PostMapping(value = "/create")
    public String add(@Validated Reports reports, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(reports, model, userDetail);
        }

        Integer code = Integer.parseInt(userDetail.getEmployee().getCode());
        reports.setEmployee(userDetail.getEmployee());
        ErrorKinds result = reportsService.save(reports, userDetail, code);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(reports, model, userDetail);

        }

        return "redirect:/reports";
    }

    // 詳細画面へ遷移
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable Integer code, Model model) {

        model.addAttribute("reports", reportsService.getReport(code));

        return "reports/detail";
    }

    // 更新画面へ遷移
    @GetMapping(value = "/{id}/update/")
    public String edit(@PathVariable("id") Integer id, Reports reports, Model model) {

        model.addAttribute("employeeName", reportsService.getReport(id).getEmployee().getName());
        model.addAttribute("reports", reportsService.getReport(id));

        return "reports/update";
    }

    // 更新処理
    @PostMapping(value = "/{id}/update/")
    public String update(@PathVariable("id") Integer id, @Validated Reports reports, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            model.addAttribute("employeeName", reportsService.getReport(id).getEmployee().getName());
            return "reports/update";
        }

        Integer code = Integer.parseInt(userDetail.getEmployee().getCode());
        reports.setEmployee(reportsService.getReport(id).getEmployee());
        reports.setCreatedAt(reportsService.getReport(id).getCreatedAt());
        ErrorKinds result = reportsService.update(reports, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employeeName", reportsService.getReport(id).getEmployee().getName());
            return "reports/update";
        }

        return "redirect:/reports";
    }

    // 削除処理
    @PostMapping(value = "/{code}/delete/")
    public String delete(@PathVariable("code") Integer code) {

        reportsService.delete(code);

        return "redirect:/reports";
    }

}
