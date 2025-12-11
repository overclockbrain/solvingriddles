package com.example.solvingriddles.controller.casual;

import com.example.solvingriddles.constant.UrlConst;
import com.example.solvingriddles.constant.ViewNames;
import com.example.solvingriddles.constant.AppConst;
import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.service.RiddleService;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CasualControllerの単体テストクラス
 * カジュアルモード用コントローラの動作確認を行う
 * 主に画面表示の確認を目的とする
 */
@WebMvcTest(CasualController.class) // ★ここが赤線(未作成)ならOK！
class CasualControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiddleService riddleService;
    /**
     * カジュアルモードのトップ画面表示テスト
     * GETリクエストで/casual/indexにアクセスし、
     * ステータスコード200と正しいビュー名が返されることを確認する
     * @throws Exception
     */
    @Test
    @DisplayName("カジュアルモードのトップ画面が表示されること")
    void testIndex() throws Exception {
        // GET /casual/index
        mockMvc.perform(get(UrlConst.CASUAL_BASE + UrlConst.CASUAL_INDEX))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewNames.CASUAL_INDEX));
    }

    /**
     * カジュアルモードのリスト画面表示テスト
     * GETリクエストで/casual/listにアクセスし、
     * ステータスコード200と正しいビュー名が返され、
     * さらにモデルに"riddles"属性が存在することを確認する
     * @throws Exception
     */
    @Test
    @DisplayName("カジュアルモードのリスト画面が表示され、データが渡されること")
    void testList() throws Exception {
        // 準備: 何もしなくても空リストくらいは返ってくる想定
        when(riddleService.findAll(AppConst.MODE_CASUAL)).thenReturn(java.util.Collections.emptyList());

        // 実行 & 検証
        mockMvc.perform(get(UrlConst.CASUAL_BASE + UrlConst.CASUAL_LIST)) // "/casual/list"
                .andExpect(status().isOk())
                .andExpect(view().name(ViewNames.CASUAL_LIST)) // "casual/list"
                .andExpect(model().attributeExists("riddles")); // "riddles" という箱があるか
    }

    /**
     * カジュアルモードのクイズ画面表示テスト
     * GETリクエストで/casual/quiz/{id}にアクセスし、
     * ステータスコード200と正しいビュー名が返され、
     * さらにモデルに"riddle"属性が存在することを確認する
     * @throws Exception
     */
    @Test
    @DisplayName("カジュアルモードのクイズ画面が表示され、指定IDの問題が渡されること")
    void testQuiz() throws Exception {
        // 準備: ID 1 の問題を返すモック
        Riddle mockRiddle = new Riddle(1, "テスト問題", "正解", "ヒント", "choice",java.util.Collections.emptyList(),1,null,null, null);
        when(riddleService.findById(AppConst.MODE_CASUAL,1)).thenReturn(Optional.of(mockRiddle));

        // 実行 & 検証
        // /casual/quiz/1 にアクセス
        mockMvc.perform(get(UrlConst.CASUAL_BASE + UrlConst.CASUAL_QUIZ + "/1"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewNames.CASUAL_QUIZ)) // "casual/quiz"
                .andExpect(model().attributeExists("riddle")); // "riddle" という箱があるか
    }

    /**
    * 答え合わせ機能のテスト: 正解の場合
    * 検証項目
    * 1.正解の答えを送信した場合に正しいリダイレクトが行われることを確認する
    * 2.リダイレクト先が結果画面であることを確認する
    * @throws Exception
    */
    @Test
    @DisplayName("結果画面: 正解パラメータとIDを受け取って表示")
    void testCheckAnswerCorrect() throws Exception {
        // 準備: ID 1 の問題に対し "正解" を渡すと true が返るようにモック
        // ※ checkAnswerメソッドが Service にある前提やで！
        when(riddleService.checkAnswer(AppConst.MODE_CASUAL, 1, "正解")).thenReturn(true);

        // 実行 & 検証
        // POST /casual/quiz/check に id=1, answer="正解" を送信
        mockMvc.perform(post(UrlConst.CASUAL_BASE + UrlConst.CASUAL_QUIZ_CHECK)
                        .param("id", "1")
                        .param("answer", "正解")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)) // フォーム送信
                .andExpect(status().is3xxRedirection()) // リダイレクトするはず
                .andExpect(redirectedUrl(UrlConst.CASUAL_BASE + "/result?success=true&id=1")); // 結果画面へ
    }

    /**
    * 答え合わせ機能のテスト: 正解の場合
    * POSTリクエストで/casual/quiz/checkにアクセスし、
    * 正解の答えを送信した場合に正しいリダイレクトが行われることを確認する
    * @throws Exception
    */
    @Test
    @DisplayName("結果画面: 正解パラメータを受け取って表示")
    void testResultSuccess() throws Exception {
        mockMvc.perform(get(UrlConst.CASUAL_BASE + UrlConst.CASUAL_RESULT)
                        .param("success", "true")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewNames.CASUAL_RESULT)) // "casual/result"
                .andExpect(model().attribute("isSuccess", true)) // モデルに値が入ってるか
                .andExpect(model().attribute("riddleId", 1)); // モデルにIDが入ってるか
    }

    /**
    * 答え合わせ機能のテスト: 不正解の場合
    * 条件: 不正解の答えを送信した場合
    * 期待値:
    * 1. ステータス 3xx (リダイレクト)
    * 2. リダイレクト先が結果画面であり、正解パラメータが false であること
    * 3. リダイレクト先のURLにIDパラメータが含まれること
    * @throws Exception
    */
    @Test
    @DisplayName("答え合わせ: 間違いなら...リダイレクト先にもIDが含まれること！")
    void testCheckAnswerFailure() throws Exception {
        // 準備: 不正解(false)を返す
        when(riddleService.checkAnswer("CASUAL", 1, "Wrong")).thenReturn(false);

        // 実行
        mockMvc.perform(post(UrlConst.CASUAL_BASE + UrlConst.CASUAL_QUIZ_CHECK)
                        .param("id", "1")
                        .param("answer", "Wrong") // わざと間違える
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                // ★ここ修正！ "&id=1" を忘れたらアカンで！
                .andExpect(redirectedUrl(UrlConst.CASUAL_BASE + "/result?success=false&id=1"));
    }

    /**
     * クイズ画面表示のテスト: 異常系 (データなし)
     * 条件: 指定したIDの問題が存在しない場合
     * 期待値:
     * 1. ステータス 3xx (リダイレクト)
     * 2. リダイレクト先が一覧画面 ("/CASUAL_LIST") であること
     */
    @Test
    @DisplayName("クイズ画面: IDが存在しないなら一覧へリダイレクト")
    void testCasualQuizNotFound() throws Exception {
        // 準備: ID 999なら空を返す
        when(riddleService.findById(AppConst.MODE_CASUAL,999)).thenReturn(Optional.empty());

        // 実行 & 検証
        mockMvc.perform(get(UrlConst.CASUAL_BASE + UrlConst.CASUAL_QUIZ + "/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(UrlConst.CASUAL_BASE + UrlConst.CASUAL_LIST));
    }
}