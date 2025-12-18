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
 * @param imageUrl 画像URL
 * @param coords 画像マップタイプ用の座標情報
 * @param nextId 次の問題ID (ストーリー型用)
 */
public record Riddle(
    Integer id,
    String question,
    String answer,
    String hint,
    String type,
    List<RiddleOption> options,
    Integer level,
    String imageUrl,
    String coords,
    Integer nextId
) {
    /**
     * レベルの数字を星に変換するメソッド
     * HTMLからは ${r.difficultyIcon} で呼べるで
     * @return 難易度を星5つで表現した文字列 (例: "★★★☆☆")
     */
    // ここで最大レベルを定義する
    public static final int MAX_LEVEL = 5;
    public String difficultyIcon() {
        int lvl = 0;
        if (level == null){
            // nullなら1扱いで
            lvl = 1;
        } else {
            lvl = level;    
        }

        if (lvl > MAX_LEVEL) {
            // 最大値を超えたら最大値に補正
            lvl = MAX_LEVEL;
        }
        
        // Java 11以上なら repeat が使える
        return "★".repeat(lvl) + "☆".repeat(MAX_LEVEL - lvl);
    }

    /**
     * オプションリストのコピーを作成し、シャッフルして返す
     * View側でランダム表示したい時に使用
     * @return シャッフルされた新しいリスト
     */
    public java.util.List<RiddleOption> getShuffledOptions() {
        if (options == null) {
            return java.util.Collections.emptyList();
        }
        // text, role を持つ RiddleOption のリストをコピー
        java.util.List<RiddleOption> copy = new java.util.ArrayList<>(options);
        // シャッフル実行
        java.util.Collections.shuffle(copy);
        return copy;
    }
}