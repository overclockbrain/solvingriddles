package com.example.solvingriddles.service;

import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.model.RiddleOption;
import com.example.solvingriddles.repository.RiddleRepository;
import com.example.solvingriddles.constant.AppConst;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 謎解きの判定ロジック (RiddleService) を検証するユニットテスト(hacker/casual両モードで実行)
 * Repositoryはモック化 (@Mock) し、純粋な判定ロジックの動作確認を行う。
 */
@ExtendWith(MockitoExtension.class)
class RiddleServiceTest {

    @Mock
    private RiddleRepository repository;

    @InjectMocks
    private RiddleService service;

    /**
     * 正解判定のテスト(hacker/casual両モードで実行): 正しい答えならtrueが返ること
     * 条件: テキスト形式の問題 (Optionsなし)
     * 期待値: true
     */
    @ParameterizedTest
    @ValueSource(strings = { AppConst.MODE_HACKER, AppConst.MODE_CASUAL })
    @DisplayName("正解判定: 正しい答えならtrueが返ること")
    void testCheckAnswerCorrect(String mode) {
        // 準備: Optionsはnull
        Riddle mockRiddle = new Riddle(1, "テスト問題", "Answer", "ヒント", "text", null,1, null,null,null);
        when(repository.findById(mode, 1)).thenReturn(Optional.of(mockRiddle));

        // 実行
        boolean result = service.checkAnswer(mode, 1, "Answer");

        // 検証
        assertTrue(result, "正解なのにfalseが返ってきています");
    }

    /**
     * 正解判定のテスト(hacker/casual両モードで実行): 間違いならfalseが返ること
     * 条件: テキスト形式の問題 (Optionsなし)
     * 期待値: false
     */
    @ParameterizedTest
    @ValueSource(strings = { AppConst.MODE_HACKER, AppConst.MODE_CASUAL })
    @DisplayName("正解判定: 間違いならfalseが返ること")
    void testCheckAnswerIncorrect(String mode) {
        // 準備
        Riddle mockRiddle = new Riddle(1, "テスト(hacker/casual両モードで実行)問題", "Answer", "ヒント", "text", null,1, null,null,null);
        when(repository.findById(mode, 1)).thenReturn(Optional.of(mockRiddle));

        // 実行
        boolean result = service.checkAnswer(mode, 1, "Wrong");

        // 検証
        assertFalse(result, "間違いなのにtrueが返ってきています");
    }

    /**
     * 正解判定のテスト(hacker/casual両モードで実行): 大文字小文字を無視すること
     * 条件: 正解が大文字混じり ("Answer")
     * 入力: 全部小文字 ("answer")
     * 期待値: true
     */
    @ParameterizedTest
    @ValueSource(strings = { AppConst.MODE_HACKER, AppConst.MODE_CASUAL })
    @DisplayName("正解判定: 大文字小文字を無視すること")
    void testCheckAnswerIgnoreCase(String mode) {
        // 準備
        Riddle mockRiddle = new Riddle(1, "テスト(hacker/casual両モードで実行)問題", "Answer", "ヒント", "text", null,1, null,null,null);
        when(repository.findById(mode, 1)).thenReturn(Optional.of(mockRiddle));

        // 実行
        boolean result = service.checkAnswer(mode, 1, "Answer");

        // 検証
        assertTrue(result, "大文字小文字が区別されてしまっています");
    }

    /**
     * 正解判定のテスト(hacker/casual両モードで実行): Clickタイプ(Optionsあり)でも正しく判定できること
     * 条件: RiddleOptionのリストを持つ複雑なデータ構造
     * 期待値: true (判定ロジック自体はデータ構造の影響を受けないこと)
     */
    @ParameterizedTest
    @ValueSource(strings = { AppConst.MODE_HACKER, AppConst.MODE_CASUAL })
    @DisplayName("正解判定: Clickタイプ(Optionsあり)でも正しく判定できること")
    void testCheckAnswerWithOptions(String mode) {
        // 準備: オプションのリストを作る
        List<RiddleOption> options = List.of(
            new RiddleOption("ダミー", "fake"),
            new RiddleOption("正解", "target")
        );

        // 準備: Optionsが入ったRiddleを作る (答えは "found")
        Riddle mockRiddle = new Riddle(4, "クリック問題", "found", "ヒント", "click", options,1, null,null,null);
        
        when(repository.findById(mode, 4)).thenReturn(Optional.of(mockRiddle));

        // 実行: 正解の "found" を渡す
        boolean result = service.checkAnswer(mode, 4, "found");

        // 検証
        assertTrue(result, "Clickタイプだと判定に失敗しています");
    }

    /**
     * 全件取得のテスト(hacker/casual両モードで実行)
     * 条件: Repositoryが複数のデータを返す場合
     * 期待値:
     * 1. 返されたリストのサイズが正しいこと
     * 2. 各データのIDが正しいこと
     */
    @ParameterizedTest
    @ValueSource(strings = { AppConst.MODE_HACKER, AppConst.MODE_CASUAL })
    @DisplayName("全件取得: RepositoryのfindAllを呼び出すこと")
    void testFindAll(String mode) {
        // 準備
        List<Riddle> mockList = List.of(
            new Riddle(1, "Q", "A", "H", "text", null, 1, null,null,null)
        );
        when(repository.findAll(mode)).thenReturn(mockList);

        // 実行
        List<Riddle> result = service.findAll(mode);

        // 検証
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).id());
    }

    /**
     * 正規表現の修正確認: Takoyakiの綴りを厳格に判定すること
     * 条件: 正解データが "^[Tt]a[ck]oyak[iy]$" の場合
     * 検証:
     * 1. 許容される8パターンすべてでtrueが返ること
     * 2. それ以外のパターンではfalseが返ること
     */
    @ParameterizedTest
    @ValueSource(strings = { AppConst.MODE_HACKER }) // Hackerモードで確認
    @DisplayName("正規表現厳格化: Tacoyakiなどの揺らぎを許可しないこと")
    void testCheckAnswerRegexPatterns(String mode) {
        // 準備: 8パターン許容する正規表現
        String regex = "^[Tt]a[ck]oyak[iy]$";
        Riddle mockRiddle = new Riddle(99, "タコヤキ問題", regex, "H", "text", null, 1, null,null,null);
        
        when(repository.findById(mode, 99)).thenReturn(Optional.of(mockRiddle));

        // 検証: 全8パターンが true になるはず！
        // 1. k - i
        assertTrue(service.checkAnswer(mode, 99, "Takoyaki"));
        assertTrue(service.checkAnswer(mode, 99, "takoyaki"));
        // 2. c - i
        assertTrue(service.checkAnswer(mode, 99, "Tacoyaki"));
        assertTrue(service.checkAnswer(mode, 99, "tacoyaki"));
        // 3. k - y
        assertTrue(service.checkAnswer(mode, 99, "Takoyaky"));
        assertTrue(service.checkAnswer(mode, 99, "takoyaky"));
        // 4. c - y
        assertTrue(service.checkAnswer(mode, 99, "Tacoyaky"));
        assertTrue(service.checkAnswer(mode, 99, "tacoyaky"));

        // 1. 最後が違う (uはダメ)
        assertFalse(service.checkAnswer(mode, 99, "Takoyaku"), "最後がuなら不正解");
        
        // 2. 真ん中が違う (zはダメ)
        assertFalse(service.checkAnswer(mode, 99, "Takozaki"), "k/c以外は不正解");
        
        // 3. 文字足らず
        assertFalse(service.checkAnswer(mode, 99, "Takoyak"), "1文字足りないなら不正解");
        
        // 4. 全然違うやつ
        assertFalse(service.checkAnswer(mode, 99, "Okonomiyaki"), "お好み焼きは不正解");
    }
}