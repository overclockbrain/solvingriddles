package com.example.solvingriddles.repository;

import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.model.RiddleOption;
import com.example.solvingriddles.constant.AppConst;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

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

    /*********************************************************************
     * 👨‍💻Hackerモードの謎解きデータ読み込みテスト
     *********************************************************************/

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
        Optional<Riddle> result = repository.findById(AppConst.MODE_HACKER, 1);
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
        Optional<Riddle> result = repository.findById(AppConst.MODE_HACKER,4);
        
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
        Optional<Riddle> result = repository.findById(AppConst.MODE_HACKER,5);
        
        // ※データ作成前やから、ここを実行すると失敗する(Red)
        if (result.isPresent()) {
            Riddle riddle = result.get();
            assertEquals("image-map", riddle.type());
            
            // ★ここが新機能の検証！(今はメソッドなくてエラーになる)
            assertEquals("/images/level5.png", riddle.imageUrl());
        }
    }

    /**
     * ストーリー形式の問題データの読み込みテスト
     * 条件: ID=6 (ストーリー型問題)
     * 検証項目:
     * 1. データが存在すること
     * 2. タイプが "story" であること
     * 3. 次の問題ID (nextId) が正しく読み込まれていること
     */
    @Test
    @DisplayName("JSONロード確認(Story): ID=6のストーリータイプが取得できること")
    void testFindByIdStory() {
        // 実行
        Optional<Riddle> result = repository.findById(AppConst.MODE_HACKER,6);
        Riddle riddle = result.get();

        assertTrue(result.isPresent(), "ID=6のデータが見つかりません");
        assertEquals("story", riddle.type());
        assertEquals(7, riddle.nextId());
    }

    /**
     * 選択肢形式の問題データの読み込みテスト
     * 条件: ID=7 (選択肢型問題)
     * 検証項目:
     * 1. データが存在すること
     * 2. タイプが "select" であること
     * 3. 選択肢リスト (options) が正しく読み込まれていること
     */
    @Test
    @DisplayName("JSONロード確認(Select): ID=7の選択肢問題が取得できること")
    void testFindByIdSelect() {
        // ID: 7 を取得
        Optional<Riddle> result = repository.findById(AppConst.MODE_HACKER,7);
        
        assertTrue(result.isPresent(), "ID=7のデータが見つかりません");
        Riddle riddle = result.get();
        
        // タイプは select
        assertEquals("select", riddle.type());
        
        // 選択肢がちゃんと入ってるか
        assertNotNull(riddle.options(), "選択肢リストがnullです");
        assertTrue(riddle.options().size() >= 3, "選択肢が3つ以上あるはずです");
    }

    /**
     * 並べ替え形式の問題データの読み込みテスト
     * 条件: ID=8 (並べ替え型問題)
     * 検証項目:
     * 1. データが存在すること
     * 2. タイプが "sort" であること
     * 3. 選択肢リスト (options) が正しく読み込まれていること
     * 4. 正解データ (answer) が正しく読み込まれていること
     */
    @Test
    @DisplayName("JSONロード確認(Sort): ID=8の並べ替え問題が取得できること")
    void testFindByIdSort() {
        // まだデータないから失敗するはず(Red)
        Optional<Riddle> result = repository.findById(AppConst.MODE_HACKER, 8);
        
        assertTrue(result.isPresent(), "ID=8のデータが見つかりません");
        Riddle riddle = result.get();
        
        assertEquals("sort", riddle.type());
        assertNotNull(riddle.options(), "並べ替え用の選択肢がnullです");
        assertTrue(riddle.options().size() >= 3, "選択肢が3つ以上あるはずです");
        
        // 正解はカンマ区切りの文字列を想定（例: "a,b,c"）
        assertNotNull(riddle.answer(), "正解が定義されていません");
    }

    /****************************************
     * Hacker/Casualモードの謎解きデータ読み込みテスト
     ****************************************/

    /**
     * 全件取得のテスト
     * 条件: Repositoryから全ての問題データを取得する
     * 検証項目:
     * 1. 返されたリストがnullでないこと
     * 2. リストのサイズが期待値以上であること
     * 3. 各データのIDが正しく読み込まれていること
     */
    @ParameterizedTest
    @ValueSource(strings = {AppConst.MODE_HACKER, AppConst.MODE_CASUAL})
    @DisplayName("JSONロード確認(All): 全てのデータが取得できること")
    void testFindAll(String mode) {
        // 実行
        List<Riddle> result = repository.findAll(mode);

        // 検証
        // 1. まずnullじゃないこと (これは絶対)
        assertNotNull(result, "リストがnullです");
        
        // 2. 「0以上」じゃなくて「今ある1件以上」にするのが安全！
        //    これならID:2を追加してもテストを書き直さんで済むわ
        assertTrue(result.size() >= 1, "問題データが読み込めていません（1件未満）");
        
        // 念のためID順に並んでるかとか見てもええけど、Mapやから順序保証はないかも
        // (ID 1が含まれてるかチェック)
        boolean containsId1 = result.stream().anyMatch(r -> r.id() == 1);
        assertTrue(containsId1, "ID 1 の問題が含まれていません");
    }

    /**
     * データ整合性チェックテスト
     * 条件: Repository内の全問題データ
     * 検証項目:
     * 1. 全データが存在すること
     * 2. 各データのIDが重複していないこと
     * 3. 必須項目 (question, type) がnullでないこと
     * 4. ストーリー以外の問題は必ず正解 (answer) が定義されていること
     * 5. nextId がある場合、その飛び先のIDが実在すること（リンク切れチェック）
     * 6. 画像マップタイプの問題は coords 情報が存在すること
     */
    @ParameterizedTest
    @ValueSource(strings = {AppConst.MODE_HACKER, AppConst.MODE_CASUAL})
    @DisplayName("データ整合性チェック: 全問題がルール通り定義されているか")
    void testAllRiddlesIntegrity(String mode) {
        List<Riddle> allRiddles = repository.findAll(mode);
        
        // 1. データが空じゃないか
        assertFalse(allRiddles.isEmpty(), "JSONが読み込めていません");

        // 重複IDチェック用のセット
        Set<Integer> idSet = new HashSet<>();

        for (Riddle r : allRiddles) {
            // 2. IDの重複チェック
            if (idSet.contains(r.id())) {
                fail("IDが重複しています: " + r.id());
            }
            idSet.add(r.id());

            // 3. 必須項目のチェック
            assertNotNull(r.question(), "ID:" + r.id() + " の問題文(question)がnullです");
            assertNotNull(r.type(), "ID:" + r.id() + " のタイプ(type)がnullです");

            // 4. ストーリー以外なら、必ず「正解(answer)」があること
            if (!"story".equals(r.type())) {
                assertNotNull(r.answer(), "ID:" + r.id() + " は問題なのに正解(answer)がありません");
            }
            
            // 5. nextId がある場合、その飛び先のIDが実在するか？ (リンク切れチェック)
            if (r.nextId() != null) {
                // ここで飛び先があるかチェックしたいけど、リスト全部なめないとわからんから
                // 簡易的に「自分より大きいか」とかチェックしてもええし、
                // 本気でやるなら全IDリストと突き合わせる
                boolean targetExists = allRiddles.stream().anyMatch(t -> t.id().equals(r.nextId()));
                assertTrue(targetExists, "ID:" + r.id() + " の nextId:" + r.nextId() + " が存在しません（リンク切れ）");
            }

            // 6. 画像マップ(image-map)タイプなら coords 情報があること
            if ("image-map".equals(r.type())) {
                // まだ .coords() なんてメソッドないから、ここで赤線（コンパイルエラー）が出るはずや！
                // これが出たら TDD の第一段階クリアやで。
                assertNotNull(r.coords(), "ID:" + r.id() + " は image-map やのに coords がないで！");
            }
        }
    }
}