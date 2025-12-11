package com.example.solvingriddles.constant;

/**
 * View（HTMLファイル名）の定数クラス
 * 各画面のHTMLテンプレート名を定数として定義し、
 * コード内でのハードコーディングを防ぐ
 */
public class ViewNames {
    
    // インスタンス化禁止（new ViewNames() させない）
    private ViewNames() {}

    // 🏠 ランチャー画面 (ルートにあるのでそのまま)
    public static final String LAUNCHER = "index";

    // 🕵️‍♂️ ハッカーモード (★hackerフォルダの中に移動したから変更！)
    // HTMLファイルの場所を指すパスやから、先頭に "hacker/" をつけるんや
    public static final String HACKER_LIST   = "hacker/list";
    public static final String HACKER_QUIZ   = "hacker/quiz";
    public static final String HACKER_RESULT = "hacker/result";

    // 🍰 カジュアルモード (casualフォルダの中)
    public static final String CASUAL_INDEX = "casual/index";
    public static final String CASUAL_LIST = "casual/list";
    public static final String CASUAL_QUIZ = "casual/quiz";
    public static final String CASUAL_RESULT = "casual/result";
}