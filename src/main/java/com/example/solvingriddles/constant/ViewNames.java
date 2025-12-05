package com.example.solvingriddles.constant;

/**
 * View（HTMLファイル名）の定数クラス
 * 各画面のHTMLテンプレート名を定数として定義し、
 * コード内でのハードコーディングを防ぐ
 */
public class ViewNames {
    
    // インスタンス化禁止（new ViewNames() させない）
    private ViewNames() {}

    public static final String INDEX = "index";
    public static final String LIST = "list";
    public static final String QUIZ = "quiz";
    public static final String RESULT = "result";
}