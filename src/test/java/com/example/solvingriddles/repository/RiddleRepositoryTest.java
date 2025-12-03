package com.example.solvingriddles.repository;

import com.example.solvingriddles.model.Riddle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RiddleRepositoryTest {

    @Autowired
    private RiddleRepository repository;

    /**
     * JSONファイルが正しく読み込まれ、データが取得できることを確認するテスト
     * <p>
     * ID: 1 のデータを検索し、以下の点を確認する
     * 1. データが存在すること (Not Empty)
     * 2. IDが一致すること
     * 3. 問題文や答えがJSONの内容と一致すること
     */
    @Test
    @DisplayName("JSONデータのロード確認: ID=1の問題が正しく取得できること")
    void testFindByIdSuccess() {
        // 実行
        Optional<Riddle> result = repository.findById(1);

        // 検証1: データが存在すること
        assertTrue(result.isPresent(), "データが見つかりません。JSONの読み込みに失敗している可能性があります。");

        // 検証2: 中身の確認
        Riddle riddle = result.get();
        assertEquals(1, riddle.id());
        // JSONの中身が変わってもテストが落ちにくいよう、キーワードで判定
        assertTrue(riddle.question().contains("2進数") || riddle.question().contains("数列"), 
            "問題文が想定と異なります");
        assertEquals("101", riddle.answer(), "答えが想定と異なります");
    }

    /**
     * 存在しないIDを指定した場合、空のOptionalが返されることを確認するテスト
     */
    @Test
    @DisplayName("存在しないID検索: 空のOptionalが返却されること")
    void testFindByIdNotFound() {
        // 実行: ありえないID (999) を検索
        Optional<Riddle> result = repository.findById(999);

        // 検証: 空であることを確認
        assertTrue(result.isEmpty(), "存在しないIDなのにデータが返ってきています");
    }
}