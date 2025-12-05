package com.example.solvingriddles.service;

import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.model.RiddleOption;
import com.example.solvingriddles.repository.RiddleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 謎解きの判定ロジック (RiddleService) を検証するユニットテスト
 * Repositoryはモック化 (@Mock) し、純粋な判定ロジックの動作確認を行う。
 */
@ExtendWith(MockitoExtension.class)
class RiddleServiceTest {

    @Mock
    private RiddleRepository repository;

    @InjectMocks
    private RiddleService service;

    /**
     * 正解判定のテスト: 正しい答えならtrueが返ること
     * 条件: テキスト形式の問題 (Optionsなし)
     * 期待値: true
     */
    @Test
    @DisplayName("正解判定: 正しい答えならtrueが返ること")
    void testCheckAnswerCorrect() {
        // 準備: Optionsはnull
        Riddle mockRiddle = new Riddle(1, "テスト問題", "Answer", "ヒント", "text", null,1, null);
        when(repository.findById(1)).thenReturn(Optional.of(mockRiddle));

        // 実行
        boolean result = service.checkAnswer(1, "Answer");

        // 検証
        assertTrue(result, "正解なのにfalseが返ってきています");
    }

    /**
     * 正解判定のテスト: 間違いならfalseが返ること
     * 条件: テキスト形式の問題 (Optionsなし)
     * 期待値: false
     */
    @Test
    @DisplayName("正解判定: 間違いならfalseが返ること")
    void testCheckAnswerIncorrect() {
        // 準備
        Riddle mockRiddle = new Riddle(1, "テスト問題", "Answer", "ヒント", "text", null,1, null);
        when(repository.findById(1)).thenReturn(Optional.of(mockRiddle));

        // 実行
        boolean result = service.checkAnswer(1, "Wrong");

        // 検証
        assertFalse(result, "間違いなのにtrueが返ってきています");
    }

    /**
     * 正解判定のテスト: 大文字小文字を無視すること
     * 条件: 正解が大文字混じり ("Answer")
     * 入力: 全部小文字 ("answer")
     * 期待値: true
     */
    @Test
    @DisplayName("正解判定: 大文字小文字を無視すること")
    void testCheckAnswerIgnoreCase() {
        // 準備
        Riddle mockRiddle = new Riddle(1, "テスト問題", "Answer", "ヒント", "text", null,1, null);
        when(repository.findById(1)).thenReturn(Optional.of(mockRiddle));

        // 実行
        boolean result = service.checkAnswer(1, "answer");

        // 検証
        assertTrue(result, "大文字小文字が区別されてしまっています");
    }

    /**
     * 正解判定のテスト: Clickタイプ(Optionsあり)でも正しく判定できること
     * 条件: RiddleOptionのリストを持つ複雑なデータ構造
     * 期待値: true (判定ロジック自体はデータ構造の影響を受けないこと)
     */
    @Test
    @DisplayName("正解判定: Clickタイプ(Optionsあり)でも正しく判定できること")
    void testCheckAnswerWithOptions() {
        // 準備: オプションのリストを作る
        List<RiddleOption> options = List.of(
            new RiddleOption("ダミー", "fake"),
            new RiddleOption("正解", "target")
        );

        // 準備: Optionsが入ったRiddleを作る (答えは "found")
        Riddle mockRiddle = new Riddle(4, "クリック問題", "found", "ヒント", "click", options,1, null);
        
        when(repository.findById(4)).thenReturn(Optional.of(mockRiddle));

        // 実行: 正解の "found" を渡す
        boolean result = service.checkAnswer(4, "found");

        // 検証
        assertTrue(result, "Clickタイプだと判定に失敗しています");
    }

    /**
     * 全件取得のテスト
     * 条件: Repositoryが複数のデータを返す場合
     * 期待値:
     * 1. 返されたリストのサイズが正しいこと
     * 2. 各データのIDが正しいこと
     */
    @Test
    @DisplayName("全件取得: RepositoryのfindAllを呼び出すこと")
    void testFindAll() {
        // 準備
        List<Riddle> mockList = List.of(
            new Riddle(1, "Q", "A", "H", "text", null, 1, null)
        );
        when(repository.findAll()).thenReturn(mockList);

        // 実行
        List<Riddle> result = service.findAll();

        // 検証
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).id());
    }
}