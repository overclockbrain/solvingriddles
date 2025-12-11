package com.example.solvingriddles.constant;

/**
 * URLパスを管理する定数クラス
 * 各コントローラで使用するURLパスを定義し、
 * ハードコーディングを防ぐ目的で使用する
 */
public class UrlConst {
    // インスタンス化禁止（new UrlConst() させない）
    private UrlConst() {}

    // 🏠 ランチャー (ルート画面)
    public static final String ROOT = "/";

    // 🕵️‍♂️ ハッカーモード用URL (ベースパスを追加！)
    public static final String HACKER_BASE = "/hacker"; // ★コントローラのクラス自体につける
    public static final String HACKER_LIST = "/list";   // → /hacker/list になる
    public static final String HACKER_QUIZ = "/quiz";   // → /hacker/quiz/{id}
    public static final String HACKER_QUIZ_CHECK = "/quiz/check";
    public static final String HACKER_QUIZ_CHECK_IMAGE = "/quiz/check-image";

    // 🍰 カジュアルモード用URL
    public static final String CASUAL_BASE = "/casual";
    public static final String CASUAL_INDEX = "/index";
    public static final String CASUAL_LIST = "/list";
    public static final String CASUAL_QUIZ = "/quiz";
    public static final String CASUAL_QUIZ_CHECK = "/quiz/check";
    public static final String CASUAL_RESULT = "/result";
}