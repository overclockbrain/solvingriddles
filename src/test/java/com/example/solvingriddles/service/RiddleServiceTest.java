package com.example.solvingriddles.service;

import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.repository.RiddleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Springを起動せず、モック機能だけ使う
class RiddleServiceTest {

    @Mock // 偽物の倉庫番を作る
    private RiddleRepository repository;

    @InjectMocks // テスト対象のServiceに、偽物の倉庫番を注入する
    private RiddleService service;

    @Test
    @DisplayName("正解判定: 正しい答えならtrueが返ること")
    void testCheckAnswerCorrect() {
        // 準備: 偽物の倉庫番に「ID 1が来たらこのデータを返せ」と仕込む
        Riddle mockRiddle = new Riddle(1, "テスト問題", "Answer", "ヒント");
        when(repository.findById(1)).thenReturn(Optional.of(mockRiddle));

        // 実行: 正解の "Answer" を渡す
        boolean result = service.checkAnswer(1, "Answer");

        // 検証
        assertTrue(result, "正解なのにfalseが返ってきたで");
    }

    @Test
    @DisplayName("正解判定: 間違いならfalseが返ること")
    void testCheckAnswerIncorrect() {
        // 準備
        Riddle mockRiddle = new Riddle(1, "テスト問題", "Answer", "ヒント");
        when(repository.findById(1)).thenReturn(Optional.of(mockRiddle));

        // 実行: 間違いの "Wrong" を渡す
        boolean result = service.checkAnswer(1, "Wrong");

        // 検証
        assertFalse(result, "間違いなのにtrueが返ってきたで");
    }

    @Test
    @DisplayName("正解判定: 大文字小文字を無視すること")
    void testCheckAnswerIgnoreCase() {
        // 準備
        Riddle mockRiddle = new Riddle(1, "テスト問題", "Answer", "ヒント");
        when(repository.findById(1)).thenReturn(Optional.of(mockRiddle));

        // 実行: "answer" (小文字) を渡す
        boolean result = service.checkAnswer(1, "answer");

        // 検証
        assertTrue(result, "大文字小文字が区別されてもうてるで");
    }
}