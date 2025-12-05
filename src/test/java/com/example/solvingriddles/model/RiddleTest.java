package com.example.solvingriddles.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Riddleレコードのロジックを検証する単体テスト
 * 主に、データ加工メソッド（難易度の星変換など）が
 * 正しく動作することを確認する。
 */
class RiddleTest {

    /**
     * 難易度アイコン生成のテスト: レベル1の場合
     * 条件: level = 1
     * 期待値: "★☆☆☆☆" (星1つ)
     */
    @Test
    @DisplayName("難易度アイコン: レベル1なら星1つ")
    void testDifficultyIconLevel1() {
        // 準備: レベル1の問題を作る (他の項目は適当でOK)
        Riddle riddle = new Riddle(1, "Q", "A", "H", "text", null, 1, null);

        // 検証
        assertEquals("★☆☆☆☆", riddle.difficultyIcon());
    }

    /**
     * 難易度アイコン生成のテスト: レベル3の場合
     * 条件: level = 3
     * 期待値: "★★★☆☆" (星3つ)
     */
    @Test
    @DisplayName("難易度アイコン: レベル3なら星3つ")
    void testDifficultyIconLevel3() {
        Riddle riddle = new Riddle(1, "Q", "A", "H", "text", null, 3, null);
        assertEquals("★★★☆☆", riddle.difficultyIcon());
    }

    /**
     * 難易度アイコン生成のテスト: レベル5の場合
     * 条件: level = 5 (最大値)
     * 期待値: "★★★★★" (星5つ)
     */
    @Test
    @DisplayName("難易度アイコン: レベル5なら星5つ")
    void testDifficultyIconLevel5() {
        Riddle riddle = new Riddle(1, "Q", "A", "H", "text", null, 5, null);
        assertEquals("★★★★★", riddle.difficultyIcon());
    }

    /**
     * 難易度アイコン生成のテスト: nullの場合
     * 条件: level = null (データ不備などのケース)
     * 期待値: "★☆☆☆☆" (デフォルトで星1つとして扱う)
     */
    @Test
    @DisplayName("難易度アイコン: nullならデフォルトで星1つ")
    void testDifficultyIconNull() {
        // レベルに null を渡してみる
        Riddle riddle = new Riddle(1, "Q", "A", "H", "text", null, null, null);
        
        // コード内で「nullなら1」にしてるか確認
        assertEquals("★☆☆☆☆", riddle.difficultyIcon());
    }
}