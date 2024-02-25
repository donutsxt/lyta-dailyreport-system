package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Reports;
import com.techacademy.repository.ReportsRepository;

@Service
public class ReportsService {

    private final ReportsRepository reportsRepository;

    @Autowired
    public ReportsService(ReportsRepository reportsRepository) {
        this.reportsRepository = reportsRepository;
    }

    // 日報新規作成
    @Transactional
    public ErrorKinds save(Reports reports, UserDetail userDetail, Integer id) {

        // 重複チェック
        if (reports.getEmployee().getCode().equals(userDetail.getEmployee().getCode())) {
            List<Reports> reportsList = findByEmployee(userDetail.getEmployee());
            for (Reports str : reportsList) {
                LocalDate date1 = str.getReportDate();
                LocalDate date2 = reports.getReportDate();
                if(date1.equals(date2)) {
                    return ErrorKinds.DATECHECK_ERROR;
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        reports.setDeleteFlg(false);
        reports.setCreatedAt(now);
        reports.setUpdatedAt(now);

        System.out.println("保存開始");
        reportsRepository.save(reports);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Reports reports, UserDetail userDetail, Integer id) {

        // 重複チェック
        if (reports.getEmployee().getCode().equals(userDetail.getEmployee().getCode())) {
            List<Reports> reportsList = findByEmployee(userDetail.getEmployee());
            for (Reports report : reportsList) {
                LocalDate existDate = report.getReportDate();//既に存在する日付
                LocalDate innputDate = reports.getReportDate();//入力された日付
                LocalDate originalDate = getReport(id).getReportDate();//もともとの日付
                if(innputDate.equals(originalDate)) {
                    // もともとの日付と入力された日付が変わらない時はエラーにせずそのまま更新
                } else {
                    // 入力された日付が既に存在する日付の場合は「既に登録されている日付です」を表示
                    if(innputDate.equals(existDate)) {
                        return ErrorKinds.DATECHECK_ERROR;
                    }
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        reports.setDeleteFlg(false);
        reports.setCreatedAt(reports.getCreatedAt());
        reports.setUpdatedAt(now);

        reportsRepository.save(reports);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer code) {

        Reports reports = getReport(code);
        LocalDateTime now = LocalDateTime.now();
        reports.setUpdatedAt(now);
        reports.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報一覧検索処理
    public List<Reports> findAll() {
        return reportsRepository.findAll();
    }

    // 日報情報検索処理
    public List<Reports> findByEmployee(Employee employee) {
        List<Reports> reports = reportsRepository.findByEmployee(employee);
        return reports;
    }

    // idで検索して1件返す
    public Reports getReport(Integer id) {
        return reportsRepository.findById(id).get();
    }
}
