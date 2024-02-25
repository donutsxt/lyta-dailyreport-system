package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@SQLRestriction("delete_flg = false")
public class Reports {
    //フィールド
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",nullable = false)
    private Integer id;

    //日付
    @Column(name="report_date",nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull //空かチェック
    private LocalDate reportDate;

    //タイトル
    @Column(name="title", length = 100, nullable = false)
    @NotEmpty //空かチェック
    @Length(max = 100) //桁数チェック
    private String title;

    //内容
    @Column(name="content", nullable = false, columnDefinition="LONGTEXT")
    @NotEmpty //空かチェック
    @Length(max = 600) //桁数チェック
    private String content;

    // 削除フラグ
    @Column(name="delete_flg", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
      private Employee employee; //ForeignKey

    //引数なしコンストラクタ
    public Reports() {
    }

    //引数ありコンストラクタ
    public Reports(Integer id, LocalDate reportDate, String title, String content, boolean deleteFlg, LocalDateTime createdAt, LocalDateTime updatedAt, Employee employee) {
    this.id = id;
    this.reportDate = reportDate;
    this.title = title;
    this.content = content;
    this.deleteFlg = deleteFlg;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.employee = employee;
    }

    //ゲッターとセッター
    public Integer getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public boolean getDeleteFlg() {
        return deleteFlg;
    }
    public void setDeleteFlg(boolean deleteFlg) {
        this.deleteFlg = deleteFlg;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
