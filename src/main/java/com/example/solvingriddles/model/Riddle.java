package com.example.solvingriddles.model;

import java.util.List;

/**
 * 謎解きのデータを保持するモデル (不変データ)
 * @param id 問題ID
 * @param question 問題文
 * @param answer 正解
 * @param hint ヒント
 * @param type 問題の種類
 * @param options クリック問題用のパーツリスト
 * @param level 難易度レベル
 */
public record Riddle(
    Integer id,
    String question,
    String answer,
    String hint,
    String type,
    List<RiddleOption> options,
    Integer level
) {
    /**
     * レベルの数字を星に変換するメソッド
     * HTMLからは ${r.difficultyIcon} で呼べるで
     * @return 難易度を星5つで表現した文字列 (例: "★★★☆☆")
     */
    public String difficultyIcon() {
        int lvl = (level == null) ? 1 : level; // nullなら1扱いで
        // Java 11以上なら repeat が使える
        return "★".repeat(lvl) + "☆".repeat(5 - lvl);
    }
}