package com.example.solvingriddles.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
        Riddle riddle = new Riddle(1, "Q", "A", "H", "text", null, 1, null,null,null);

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
        Riddle riddle = new Riddle(1, "Q", "A", "H", "text", null, 3, null,null,null);
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
        Riddle riddle = new Riddle(1, "Q", "A", "H", "text", null, 5, null,null,null);
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
        Riddle riddle = new Riddle(1, "Q", "A", "H", "text", null, null, null,null,null);
        
        // コード内で「nullなら1」にしてるか確認
        assertEquals("★☆☆☆☆", riddle.difficultyIcon());
    }

    /**
     * getShuffledOptionsメソッドのテスト群
     * 条件: optionsリストに複数のOptionが設定されている場合
     * 期待値: シャッフルされた新しいリストが返されること
     */
    @Test
    @DisplayName("getShuffledOptionsは、元のリストとは別のインスタンスを返すこと")
    void getShuffledOptionsReturnsNewInstance() {
        // Arrange
        // RiddleOption(text, role) の順番で作成
        List<RiddleOption> originalOptions = List.of(
            new RiddleOption("A", "normal"),
            new RiddleOption("B", "target"),
            new RiddleOption("C", "fake")
        );
        
        // Riddleレコードを作成（引数多いけど頑張って埋めるで）
        Riddle riddle = new Riddle(
            1, "Question", "Answer", "Hint", "sort", 
            originalOptions, // options
            1, null, null, null
        );

        // Act
        List<RiddleOption> shuffled = riddle.getShuffledOptions();

        // Assert
        assertNotNull(shuffled, "nullであってはならない");
        assertNotSame(originalOptions, shuffled, "元のリストと同じインスタンスであってはならない（コピーであること）");
        assertEquals(3, shuffled.size(), "サイズが変わってはならない");
    }

    @Test
    @DisplayName("getShuffledOptionsは、元のリストの要素をすべて含んでいること")
    void getShuffledOptionsContainsAllElements() {
        // Arrange
        RiddleOption opt1 = new RiddleOption("Java", "target");
        RiddleOption opt2 = new RiddleOption("Python", "fake");
        RiddleOption opt3 = new RiddleOption("Ruby", "fake");
        List<RiddleOption> expectedList = List.of(opt1, opt2, opt3);
        
        Riddle riddle = new Riddle(
            1, "Q", "A", "H", "sort",
            List.of(opt1, opt2, opt3),
            1, null, null, null
        );

        // Act
        List<RiddleOption> shuffled = riddle.getShuffledOptions();

        // Assert
        // サイズチェック
        assertEquals(3, shuffled.size());
        
        // 中身が全部揃ってるかチェック（順番不問）
        // ※JUnit標準には containsExactlyInAnyOrder がないから、containsAllで代用
        assertTrue(shuffled.containsAll(expectedList), "元の要素がすべて含まれていること");
    }

    @Test
    @DisplayName("optionsがnullの場合、空のリストを返して落ちないこと")
    void getShuffledOptionsHandlesNull() {
        // Arrange
        Riddle riddle = new Riddle(
            1, "Q", "A", "H", "text",
            null, // optionsがnullの場合
            1, null, null, null
        );

        // Act
        List<RiddleOption> result = riddle.getShuffledOptions();

        // Assert
        assertNotNull(result, "nullの代わりに空リストを返すべき");
        assertTrue(result.isEmpty(), "リストは空であるべき");
    }
}