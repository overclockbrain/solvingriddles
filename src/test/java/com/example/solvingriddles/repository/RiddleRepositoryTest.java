package com.example.solvingriddles.repository;

import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.model.RiddleOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 謎解きデータの保管庫 (RiddleRepository) を検証する統合テスト
 * 実際にSpring Contextを起動し、JSONファイル ("riddles.json") が
 * 正しく読み込まれ、Javaのオブジェクトに変換されているかを確認する。
 */
@SpringBootTest
class RiddleRepositoryTest {

    @Autowired
    private RiddleRepository repository;

    /**
     * テキスト形式の問題データの読み込みテスト
     * 条件: ID=1 (標準的なテキスト問題)
     * 検証項目:
     * 1. データが存在すること
     * 2. タイプが "text" であること
     * 3. クリック用の options が null (または空) であること
     * 4. 難易度レベルが正しく読み込まれていること
     * 5. 難易度アイコンが正しく生成されていること
     */
    @Test
    @DisplayName("JSONロード確認(Text): ID=1のテキスト問題が取得できること")
    void testFindByIdText() {
        Optional<Riddle> result = repository.findById(1);
        assertTrue(result.isPresent(), "ID=1のデータが見つかりません");

        Riddle riddle = result.get();
        assertEquals(1, riddle.id());
        assertEquals("text", riddle.type());
        
        // テキスト問題なら options は null のはず
        assertNull(riddle.options(), "テキスト問題に予期せぬoptionsが含まれています");

        // レベルの確認
        assertEquals(1, riddle.level(), "難易度が正しく読み込めていません");

        // 難易度アイコンの確認
        assertEquals("★☆☆☆☆", riddle.difficultyIcon(), "難易度アイコンの生成がおかしいです");
    }

    /**
     * クリック探索形式の問題データの読み込みテスト
     * 条件: ID=4 (クリック探索型問題)
     * 検証項目:
     * 1. データが存在すること
     * 2. タイプが "click" であること
     * 3. options リストが正しく読み込まれ、各パーツ(role, text)が格納されていること
     * 4. 難易度レベルが正しく読み込まれていること
     * 5. 難易度アイコンが正しく生成されていること
     */
    @Test
    @DisplayName("JSONロード確認(Click): ID=4のクリック問題とOptionsが取得できること")
    void testFindByIdClick() {
        // 実行
        Optional<Riddle> result = repository.findById(4);
        
        // 検証
        assertTrue(result.isPresent(), "ID=4のデータが見つかりません");
        Riddle riddle = result.get();
        
        assertEquals(4, riddle.id());
        assertEquals("click", riddle.type());
        
        // リストの中身をチェック
        List<RiddleOption> options = riddle.options();
        assertNotNull(options, "optionsリストが読み込めていません");
        assertEquals(5, options.size(), "パーツの数がJSONと一致しません");
        
        // 具体的な中身の検証 (2番目がダミー、4番目が正解か)
        assertEquals("fake", options.get(1).role());
        assertEquals("target", options.get(3).role());

        // レベルの確認
        assertEquals(4, riddle.level(), "難易度が正しく読み込めていません");

        // 難易度アイコンの確認
        assertEquals("★★★★☆", riddle.difficultyIcon(), "難易度アイコンの生成がおかしいです");
    }

    /**
     * 全件取得のテスト
     * JSONファイルに定義されている全データ（現在は4件）が取得できること
     * 検証項目:
     * 1. 返されたリストが null でないこと
     * 2. リストのサイズが4であること
     * 3. ID=4の問題が含まれていること
     */
    @Test
    @DisplayName("JSONロード確認(All): 全てのデータが取得できること")
    void testFindAll() {
        // 実行
        List<Riddle> result = repository.findAll();

        // 検証
        assertNotNull(result);
        assertEquals(4, result.size(), "問題数がJSONの定義数(4つ)と一致しません");
        
        // 念のためID順に並んでるかとか見てもええけど、Mapやから順序保証はないかも
        // (ID 4が含まれてるかチェック)
        boolean containsId4 = result.stream().anyMatch(r -> r.id() == 4);
        assertTrue(containsId4, "ID 4 の問題が含まれていません");
    }

    /**
     * 画像マップ形式の問題データの読み込みテスト
     * 条件: ID=5 (画像マップ型問題)
     * 検証項目:
     * 1. データが存在すること
     * 2. タイプが "image-map" であること
     * 3. 画像URLが正しく読み込まれていること
     */
    @Test
    @DisplayName("JSONロード確認(Image): ID=5の画像問題とパスが取得できること")
    void testFindByIdImage() {
        // まだデータないけど、未来の仕様を先に書く！
        Optional<Riddle> result = repository.findById(5);
        
        // ※データ作成前やから、ここを実行すると失敗する(Red)
        if (result.isPresent()) {
            Riddle riddle = result.get();
            assertEquals("image-map", riddle.type());
            
            // ★ここが新機能の検証！(今はメソッドなくてエラーになる)
            assertEquals("/images/level5.png", riddle.imageUrl());
        }
    }
}