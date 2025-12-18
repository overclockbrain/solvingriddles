package com.example.solvingriddles.e2e;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.CollectionCondition.*;
import static org.assertj.core.api.Assertions.*;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

// URLチェック用のアダプター
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

import java.util.List;

/**
 * リファクタリング後のE2E回帰テスト
 * 対応する試験仕様書: refactor_test_plan.md
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HackerQuizE2ETest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        Configuration.baseUrl = "http://localhost:" + port;
        Configuration.headless = false; // 動作を見たい場合はfalse
        Configuration.timeout = 8000;   // タイムアウト延長(8秒)
    }

    /**
     * [試験番号: 1-1, 3-2(Text)]
     * 基本遷移・送信確認 / Hacker Textタイプ
     * - 正常な値を入力して送信し、次のレベルへ遷移できるか
     */
    @Test
    void testTextInputAndSubmit() {
        open("/hacker/quiz/1");

        // 正解を入力してEnter
        $("input[name='answer']").shouldBe(visible).setValue("101").pressEnter();

        // 【修正ポイント】
        // URLが「result」を含んでいるかチェック！
        // 例: http://localhost:8080/hacker/result/1 とかに変わってるはず
        webdriver().shouldHave(urlContaining("result"));

        // もしリザルト画面に「CLEAR!」とか「正解！」って文字が出るなら、それもチェックすると完璧
        // $("body").shouldHave(text("正解")); 
    }

    /**
     * [試験番号: 5-1]
     * バリデーション & エラーハンドリング
     * - 空送信時にブラウザのバリデーション(required)が機能するか
     */
    @Test
    void testEmptySubmit() {
        // 任意の問題IDを指定
        open("/hacker/quiz/1");
        
        // 空のまま送信ボタンを押す
        $("button[type='submit']").click();
        
        // HTML5の validation message が出ているか確認
        // (Selenideでは :invalid 擬似クラスでチェック可能)
        $("input[name='answer']:invalid").shouldBe(exist);
    }

    /**
     * [試験番号: 2-1, 3-2(Click)]
     * シャッフル有効確認 / Hacker Clickタイプ
     * - リロード前後で選択肢の並び順が変わっているか
     */
    @Test
    void testShuffleOnClickPuzzle() {
        // Clickタイプの問題IDを指定
        open("/hacker/quiz/5");

        // 画面上の文字要素を取得
        ElementsCollection options = $$(".target-char, .fake-char, .normal-char");
        List<String> firstOrder = options.texts();

        refresh(); // リロード

        List<String> secondOrder = $$(".target-char, .fake-char, .normal-char").texts();

        // 中身は同じだが、順番が違うことを確認
        assertThat(secondOrder).containsExactlyInAnyOrderElementsOf(firstOrder);
        assertThat(secondOrder).isNotEqualTo(firstOrder);
    }

    /**
     * [試験番号: 2-2]
     * シャッフル無効確認
     * - KeyboardやShortcutなど、順序固定の問題で並びが変わらないか
     */
    @Test
    void testNonShuffleType() {
        // Keyboard等の順序固定問題IDを指定
        open("/hacker/quiz/99"); 

        List<String> firstOrder = $$(".key-char").texts();

        refresh();

        List<String> secondOrder = $$(".key-char").texts();

        // 完全に一致すること (順番も維持)
        assertThat(secondOrder).containsExactlyElementsOf(firstOrder);
    }

    /**
     * [試験番号: 3-2(Sort), 5-2]
     * Hacker Sortタイプ / JS連携
     * - ドラッグ＆ドロップで並べ替えができ、正しく送信されるか
     */
    @Test
    void testSortPuzzle() {
        // Sortタイプの問題IDを指定
        open("/hacker/quiz/3"); 

        ElementsCollection items = $$(".sort-item");
        String firstText = items.get(0).text();

        // ドラッグ＆ドロップ (先頭を末尾へ)
        actions().dragAndDrop(items.get(0), items.get(items.size() - 1)).perform();

        // 見た目の並びが変わったか簡易チェック
        $$(".sort-item").last().shouldHave(text(firstText));

        // 送信
        $$("button").findBy(text("決定して回答")).click();

        // 遷移確認
        $("h2").shouldNotHave(text("LEVEL 3")); 
    }

    /**
     * [試験番号: 3-2(Select)]
     * Hacker Selectタイプ
     * - プルダウン選択で回答できるか
     */
    @Test
    void testSelectPuzzle() {
        // Selectタイプの問題IDを指定
        open("/hacker/quiz/4");

        // インデックス1 (2番目) を選択
        $("select[name='answer']").selectOption(1);

        $("button[type='submit']").click();

        $("h2").shouldNotHave(text("LEVEL 4"));
    }

    /**
     * [試験番号: 4-1]
     * Casual Mode UI制御 (Choice)
     * - 入力欄(text)が非表示で、ボタンのみ表示されるか
     */
    @Test
    void testCasualModeUI_ChoiceType() {
        // Casual Choiceタイプの問題IDを指定
        open("/casual/quiz/10"); 

        // ボタンがある
        $$("button.choice-btn").shouldHave(sizeGreaterThan(0)); 

        // テキスト入力欄がない
        $("input[name='answer'][type='text']").shouldNot(exist);
    }

    /**
     * [試験番号: 4-1]
     * Casual Mode UI制御 (Text)
     * - 入力欄(text)が表示されるか
     */
    @Test
    void testCasualModeUI_TextType() {
        // Casual Textタイプの問題IDを指定
        open("/casual/quiz/11"); 

        // テキスト入力欄がある
        $("input[name='answer'][type='text']").shouldBe(visible);
    }
}