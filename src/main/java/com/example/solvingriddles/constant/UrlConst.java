package com.example.solvingriddles.constant;

/**
 * URLパスを管理する定数クラス
 * 各コントローラで使用するURLパスを定義し、
 * ハードコーディングを防ぐ目的で使用する
 */
public class UrlConst {
    // インスタンス化禁止（new UrlConst() させない）
    private UrlConst() {}

    public static final String ROOT = "/";
    public static final String LIST = "/list";
    public static final String QUIZ = "/quiz"; // "/quiz/{id}" のベース
    public static final String QUIZ_CHECK = "/quiz/check";
    public static final String QUIZ_CHECK_IMAGE = "/quiz/check-image";
}